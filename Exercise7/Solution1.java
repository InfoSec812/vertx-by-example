import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.*;
import java.util.stream.Collectors;

import static groovy.json.JsonOutput.toJson;

public class Solution1 extends AbstractVerticle {

    int failCount = 0;

    public void start() {

        Future<JsonObject> dbFuture = Future.future();
        if (Math.random()>=0.5) {
            vertx.eventBus().send("db.test", dbFuture.completer());
        } else {
            dbFuture.complete(new JsonObject().put("ok", "null").put("details", "blah").put("status", "Unknown"));
        }

        List<String> verticleList = Arrays.asList("java:EventVerticle.java", "java:AnotherVerticle.java");

        List<Future> futureList = verticleList.stream().map(it -> {
            Future f = Future.future();
            vertx.deployVerticle(it, new DeploymentOptions(), result -> this.deployHandler(result, failCount, new DeploymentOptions(), it, f));
            return f;
        }).collect(Collectors.toList());

        futureList.add(dbFuture);

        CompositeFuture.join(futureList).setHandler(this::resolutionHandler);
    }

    protected void resolutionHandler(AsyncResult<CompositeFuture> res) {
        if (res.succeeded()) {
            // If the EventVerticle successfully deployed, configure and start the HTTP server
            Router router = Router.router(vertx);

            router.get().handler(this::rootHandler);

            vertx.createHttpServer()            // Create a new HttpServer
                    .requestHandler(router::accept) // Register a request handler
                    .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
        } else {
            vertx.close();
        }
    }

    protected void rootHandler(RoutingContext ctx) {
        JsonObject msg = new JsonObject().put("path", ctx.request().path());
        vertx.eventBus().send("event.verticle", msg, reply -> this.replyHandler(ctx, reply));
    }

    protected void replyHandler(RoutingContext ctx, AsyncResult<Message<Object>> reply) {
        HttpServerResponse response = ctx.response()
                          .putHeader("Content-Type", "application/json");
        if (reply.succeeded()) {
            response.setStatusCode(200)
               .setStatusMessage("OK")
               .end(((JsonObject)reply.result().body()).encodePrettily());
        } else {
            response.setStatusCode(500)
                    .setStatusMessage("Server Error")
                    .end(new JsonObject().put("error", reply.cause().getLocalizedMessage()).encodePrettily());
        }
    }

    /**
     * A deployment handler which can attempt to redeploy a verticle up to 3 times if the deployment fails
     * @param res The result of the deployment
     * @param attemptCount The current attempt count
     * @param options The verticle deployment options
     * @param verticleName The name of the verticle to deploy
     * @param f A future which will be resolved either successfully or failed depending on if the verticle is deployed.
     */
    protected void deployHandler(AsyncResult<String> res, int attemptCount, DeploymentOptions options, String verticleName, Future f) {
        if (res.succeeded()) {
            LoggerFactory.getLogger("Solution1").info("Successfully deployed "+verticleName);
            f.complete();
        } else {
            LoggerFactory.getLogger("Solution1").error("Failed to deploy "+verticleName, res.cause());
            failCount++;
            if (failCount==3) {
                f.failed();
            } else {
                //
                vertx.deployVerticle(verticleName, result -> this.deployHandler(result, failCount, new DeploymentOptions(), verticleName, f));
            }
        }
    }
}

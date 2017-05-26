import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.json.JsonOutput.toJson;

public class Solution1 extends AbstractVerticle {

    public void start() {

        Future eventVerticleFuture = Future.future();
        Future anotherVerticleFuture = Future.future();
        List<Future> futureList = Arrays.asList(eventVerticleFuture, anotherVerticleFuture);

        CompositeFuture.join(futureList).setHandler(this::deployHandler);

        vertx.deployVerticle("java:EventVerticle.java", eventVerticleFuture.completer());
        vertx.deployVerticle("java:AnotherVerticle.java", anotherVerticleFuture.completer());
    }

    void rootHandler(RoutingContext ctx) {
        JsonObject msg = new JsonObject().put("path", ctx.request().path());
        vertx.eventBus().send("event.verticle", msg, reply -> this.replyHandler(ctx, reply));
    }

    void replyHandler(RoutingContext ctx, AsyncResult<Message<Object>> reply) {
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

    void deployHandler(AsyncResult<CompositeFuture> cf) {
        if (cf.succeeded()) {
            LoggerFactory.getLogger("Solution1").info("Successfully deployed all verticles");

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            Router router = Router.router(vertx);

            router.get().handler(this::rootHandler);

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router::accept) // Register a request handler
                .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
        } else {
            IntStream.range(0, cf.result().size()).forEach(x -> {
                if (cf.result().failed(x)) {
                    LoggerFactory.getLogger("Solution1").error("Failed to deploy verticle", cf.result().cause(x));
                }
            });
            vertx.close();
        }
    }
}

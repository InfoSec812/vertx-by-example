import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

public class Solution2 extends AbstractVerticle {

    public void start() {

        Future<String> allDone = Future.future();
        allDone.setHandler(res -> this.deployHandler(res));

        Future eventVerticleFuture = Future.future();

        vertx.deployVerticle("java:EventVerticle.java", eventVerticleFuture.completer());

        eventVerticleFuture.compose(v -> vertx.deployVerticle("java:AnotherVerticle.java", allDone.completer()), allDone);
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
               .end(new JsonObject().put("err", reply.cause().getLocalizedMessage()).encodePrettily());
        }
    }

    protected void deployHandler(AsyncResult<String> f) {
        if (f.succeeded()) {
            LoggerFactory.getLogger("Solution2").info("Successfully deployed all verticles");

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            Router router = Router.router(vertx);

            router.get().handler(this::rootHandler);

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router::accept) // Register a request handler
                .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
        } else {
            LoggerFactory.getLogger("Solution2").error("Failed to deploy verticle", f.cause());
            vertx.close();
        }
    }
}

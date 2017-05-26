import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

import static groovy.json.JsonOutput.toJson;

public class Exercise7 extends AbstractVerticle {

    public void start() {
        vertx.deployVerticle("groovy:EventVerticle.groovy", this::deployHandler)
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

    void deployHandler(AsyncResult<String> res) {
        if (res.succeeded()) {
            LoggerFactory.getLogger("Exercise7").info("Successfully deployed EventVerticle")

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            Router router = Router.router(vertx);

            router.get().handler(this::rootHandler);

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router::accept) // Register a request handler
                .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
        } else {
            // Otherwise, exit the application
            LoggerFactory.getLogger("Exercise7").error("Failed to deploy EventVerticle", res.cause());
            vertx.close();
        }
    }
}

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.RoutingContext;

import java.util.stream.IntStream;

public class Exercise8 extends AbstractVerticle {
    public void start() {
        Future eventVerticleFuture = Future.future();
        Future anotherVerticleFuture = Future.future();

        CompositeFuture.join(eventVerticleFuture, anotherVerticleFuture).setHandler(this::deployHandler);

        vertx.deployVerticle("java:EventVerticle.java", eventVerticleFuture.completer());
        vertx.deployVerticle("java:AnotherVerticle.java", anotherVerticleFuture.completer());
    }

    protected void deployHandler(AsyncResult<CompositeFuture> cf) {
        if (cf.succeeded()) {
            LoggerFactory.getLogger("Exercise8").info("Successfully deployed all verticles");

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            Router router = Router.router(vertx);

            router.get().handler(this::rootHandler);

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router::accept) // Register a request handler
                .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
        } else {
            IntStream.range(0, cf.result().size()).forEach(x -> {
                if (cf.result().failed(x)) {
                    LoggerFactory.getLogger("Exercise8").error("Failed to deploy verticle", cf.result().cause(x));
                }
            });
            vertx.close();
        }
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject().put("ok", true).put("path", ctx.request().path()).encode());
    }
}
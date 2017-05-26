import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.AbstractVerticle;

public class Exercise8 extends AbstractVerticle {
    public void start() {
        Future eventVerticleFuture = Future.future()
        Future anotherVerticleFuture = Future.future()

        CompositeFuture.join(eventVerticleFuture, anotherVerticleFuture).setHandler(this.&deployHandler)

        vertx.deployVerticle('groovy:EventVerticle.groovy', eventVerticleFuture.completer())
        vertx.deployVerticle('groovy:AnotherVerticle.groovy', anotherVerticleFuture.completer())
    }

    void deployHandler(CompositeFuture cf) {
        if (cf.succeeded()) {
            LoggerFactory.getLogger(Exercise8).info('Successfully deployed all verticles')

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            def router = Router.router(vertx)

            router.get().handler(this.&rootHandler)

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router.&accept) // Register a request handler
                .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        } else {
            def range = 0..(cf.size() - 1)
            range.each { x ->
                if (cf.failed(x)) {
                    LoggerFactory.getLogger(Exercise8).error('Failed to deploy verticle', cf.cause(x))
                }
            }
            vertx.close()
        }
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: true, path: ctx.request().path()]).encode())
    }
}
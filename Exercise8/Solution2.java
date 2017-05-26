import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

import static groovy.json.JsonOutput.toJson;

public class Solution2 extends AbstractVerticle {

    public void start() {

        Future allDone = Future.future()
        allDone.setHandler(this.&deployHandler)

        Future eventVerticleFuture = Future.future()

        vertx.deployVerticle('groovy:EventVerticle.groovy', eventVerticleFuture.completer())

        eventVerticleFuture.compose({ v ->
            vertx.deployVerticle('groovy:AnotherVerticle.groovy', allDone.completer())
        }, allDone)
    }

    void rootHandler(RoutingContext ctx) {
        def msg = new JsonObject([path: ctx.request().path()])
        def replyHandler = { AsyncResult<Message> reply -> this.replyHandler(ctx, reply) }
        vertx.eventBus().send('event.verticle', msg, replyHandler)
    }

    void replyHandler(RoutingContext ctx, AsyncResult<Message> reply) {
        def response = ctx.response()
                          .putHeader('Content-Type', 'application/json')
        if (reply.succeeded()) {
            response.setStatusCode(200)
               .setStatusMessage('OK')
               .end(new JsonObject(reply.result().body()).encodePrettily())
        } else {
            response.setStatusCode(500)
               .setStatusMessage('Server Error')
               .end(toJson(reply.cause()))
        }
    }

    void deployHandler(Future f) {
        if (f.succeeded()) {
            LoggerFactory.getLogger(Solution2).info('Successfully deployed all verticles')

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            def router = Router.router(vertx)

            router.get().handler(this.&rootHandler)

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router.&accept) // Register a request handler
                .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        } else {
            LoggerFactory.getLogger(Solution2).error('Failed to deploy verticle', f.cause())
            vertx.close()
        }
    }
}

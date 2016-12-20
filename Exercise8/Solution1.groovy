import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.eventbus.Message
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.lang.groovy.GroovyVerticle

import static groovy.json.JsonOutput.toJson

class Solution1 extends GroovyVerticle {

    void start() {

        Future eventVerticleFuture = Future.future()
        Future anotherVerticleFuture = Future.future()
        def futureList = [eventVerticleFuture, anotherVerticleFuture]

        CompositeFuture.join(futureList).setHandler(this.&deployHandler)

        vertx.deployVerticle('groovy:EventVerticle.groovy', eventVerticleFuture.completer())
        vertx.deployVerticle('groovy:AnotherVerticle.groovy', anotherVerticleFuture.completer())
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

    void deployHandler(CompositeFuture cf) {
        if (cf.succeeded()) {
            LoggerFactory.getLogger(Solution1).info('Successfully deployed all verticles')

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
                    LoggerFactory.getLogger(Solution1).error('Failed to deploy verticle', cf.cause(x))
                }
            }
            vertx.close()
        }
    }
}

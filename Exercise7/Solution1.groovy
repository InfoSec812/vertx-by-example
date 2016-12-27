import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.eventbus.Message
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.lang.groovy.GroovyVerticle

import static groovy.json.JsonOutput.toJson

class Solution1 extends GroovyVerticle {

    def failCount = 0

    void start() {

        def dbFuture = Future.future()
        if (Math.random()>=0.5) {
            vertx.eventBus().send('db.test', dbFuture.completer())
        } else {
            dbFuture.complete([ok: null, details: 'blah', status: 'Unknown'])
        }

        def verticleList = [
            'groovy:EventVerticle.groovy': [:],
            'groovy:AnotherVerticle.groovy': [:]
        ]

        def futureList = []
        verticleList.each {
            Future f = Future.future()
            vertx.deployVerticle(it.key, it.value, this.&deployHandler.rcurry(f).rcurry(verticleName).rcurry(it.value))
            futureList.add(f)
        }

        futureList.add(dbFuture)

        CompositeFuture.join(futureList).setHandler(this.&resolutionHandler)
    }

    void resolutionHandler(AsyncResult<CompositeFuture> res) {
        if (res.succeeded()) {
            // If the EventVerticle successfully deployed, configure and start the HTTP server
            def router = Router.router(vertx)

            router.get().handler(this.&rootHandler)

            vertx.createHttpServer()            // Create a new HttpServer
                    .requestHandler(router.&accept) // Register a request handler
                    .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        } else {
            vertx.close()
        }
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

    /**
     * A deployment handler which can attempt to redeploy a verticle up to 3 times if the deployment fails
     * @param res The result of the deployment
     * @param attemptCount The current attempt count
     * @param options The verticle deployment options
     * @param verticleName The name of the verticle to deploy
     * @param f A future which will be resolved either successfully or failed depending on if the verticle is deployed.
     */
    void deployHandler(AsyncResult<String> res, int attemptCount = 0, Map options, String verticleName, Future f) {
        if (res.succeeded()) {
            LoggerFactory.getLogger(Solution1).info("Successfully deployed ${verticleName}")
            f.complete()
        } else {
            LoggerFactory.getLogger(Solution1).error("Failed to deploy ${verticleName}", res.cause())
            failCount++
            if (failCount==3) {
                f.failed()
            } else {
                //
                vertx.deployVerticle(verticleName, this.&deployHandler.rcurry(f).rcurry(verticleName).rcurry(options))
            }
        }
    }
}

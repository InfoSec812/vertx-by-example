import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.eventbus.Message
import io.vertx.lang.groovy.GroovyVerticle

class AnotherVerticle extends GroovyVerticle {

    @Override
    void start(Future startFuture) {
        vertx.eventBus().consumer('another.verticle', this.&doSomething)

        if ((Math.round(Math.random()*1))==1) {            // Randomly succeed or fail deployment of AnotherVerticle
            LoggerFactory.getLogger(AnotherVerticle).info('Deployed AnotherVerticle')
            startFuture.complete()
        } else {
            LoggerFactory.getLogger(AnotherVerticle).error('Failed AnotherVerticle')
            startFuture.fail('Random deployment failure of AnotherVerticle')
        }
    }

    void doSomething(Message<JsonObject> msg) {
        if ((Math.round(Math.random()*1))==1) {
            msg.reply(msg.body())
        } else {
            msg.fail(1, 'Random Failure')
	    }
    }
}

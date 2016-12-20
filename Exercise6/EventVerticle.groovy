import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.core.eventbus.Message
import io.vertx.core.json.JsonObject

class EventVerticle extends GroovyVerticle {

    @Override
    void start() {
        vertx.eventBus().consumer('event.verticle', this.&doSomething)
    }

    void doSomething(Message<JsonObject> msg) {
        if ((Math.round(Math.random()*1))==1) {
            msg.reply(msg.body())
        } else {
            msg.fail(1, 'Random Failure')
	}
    }
}

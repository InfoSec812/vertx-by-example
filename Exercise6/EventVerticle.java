import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class EventVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().consumer("event.verticle", this::doSomething);
    }

    void doSomething(Message<JsonObject> msg) {
        if ((Math.round(Math.random()*1))==1) {
            msg.reply(msg.body());
        } else {
            msg.fail(1, "Random Failure");
	    }
    }
}

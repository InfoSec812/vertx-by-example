import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.AbstractVerticle;

public class AnotherVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.sharedData().getLocalMap("shared").put(context.deploymentID(), Thread.currentThread().getName());

        LoggerFactory.getLogger("AnotherVerticle").info("Deployed AnotherVerticle: ${context.deploymentID()}");
    }
}
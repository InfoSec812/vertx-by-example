import io.vertx.core.logging.LoggerFactory
import io.vertx.lang.groovy.GroovyVerticle

class AnotherVerticle extends GroovyVerticle {

    @Override
    void start() throws Exception {
        vertx.sharedData().getLocalMap('shared').put(context.deploymentID(), Thread.currentThread().name)

        LoggerFactory.getLogger(AnotherVerticle).info("Deployed AnotherVerticle: ${context.deploymentID()}")
    }
}
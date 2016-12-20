import io.vertx.core.logging.LoggerFactory
import io.vertx.lang.groovy.GroovyVerticle

class Exercise9_1 extends GroovyVerticle {

    @Override
    void start() throws Exception {

        // Deploy AnotherVerticle 10 times
        (1 .. 10).each {
            vertx.deployVerticle('groovy:AnotherVerticle.groovy')
        }

        vertx.setPeriodic(100, this.&showDeployedVerticles)
    }

    void showDeployedVerticles(Long t) {
        // Print the list of deployment IDs stored in the shared data local Map
        LoggerFactory.getLogger(Exercise9_1).info('Polling shared data map')
        def localMap = vertx.sharedData().getLocalMap('shared')
        localMap.getDelegate().keySet().each {
            println "${it} - ${localMap.get(it)}".toString()
        }
        println ''
        println ''
    }
}
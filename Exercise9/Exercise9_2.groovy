import io.vertx.core.logging.LoggerFactory
import io.vertx.lang.groovy.GroovyVerticle

class Exercise9_1 extends GroovyVerticle {

    @Override
    void start() throws Exception {

        // Get a reference to clusterWide map called 'shared'
        vertx.sharedData().getClusterWideMap('shared', { res ->
            if (res.succeeded()) {
                // Write to the map and await success
                res.result().put('deployments', Arrays.asList(context.deploymentID()), { res1 ->
                    // Deploy AnotherVerticle 10 times
                    (1 .. 10).each {
                        vertx.deployVerticle('groovy:ClusteredVerticle.groovy')
                    }
                })
            }
        })

        vertx.setPeriodic(100, this.&showDeployedVerticles)
    }

    void showDeployedVerticles(Long t) {
        // Print the list of deployment IDs stored in the shared data local Map
        LoggerFactory.getLogger(Exercise9_1).info('Polling shared data map')

        // Get reference to clusterWide map called 'shared'
        def clusteredMap = vertx.sharedData().getClusterWideMap('shared', { res ->
            if (res.succeeded()) {
                // Get the 'deployments' value from the AsyncMap
                res.result().get('deployments', { res1 ->

                    // Iterate over list of values
                    res1.result().each {
                        println it
                    }
                    println ''
                    println ''
                })
            }
        })
    }
}
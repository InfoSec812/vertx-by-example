import io.vertx.core.logging.LoggerFactory
import io.vertx.lang.groovy.GroovyVerticle

class ClusteredVerticle extends GroovyVerticle {

    @Override
    void start() throws Exception {
        // Get a reference to clusterWide map called 'shared'
        vertx.sharedData().getClusterWideMap('shared', { res ->
            if (res.succeeded()) {
                // Get the 'deployments' list
                res.result().get('deployments', { res1 ->
                    List<String> deploymentList = res1.result()
                    deploymentList.add(context.deploymentID())

                    // Update the 'deployments' list
                    res.result().put('deployments', deploymentList, { res2 ->
                        LoggerFactory.getLogger(ClusteredVerticle).info("Deployed ClusteredVerticle: ${context.deploymentID()}")
                    })
                })
            }
        })
    }
}
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.AbstractVerticle;

import java.util.List;

public class ClusteredVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Get a reference to clusterWide map called "shared"
        vertx.sharedData().getClusterWideMap("shared", res -> {
            if (res.succeeded()) {
                // Get the "deployments" list
                res.result().get("deployments", res1 -> {
                    List<String> deploymentList = (List<String>)res1.result();
                    deploymentList.add(context.deploymentID());

                    // Update the "deployments" list
                    res.result().put("deployments", deploymentList, res2 -> {
                        LoggerFactory.getLogger("ClusteredVerticle").info("Deployed ClusteredVerticle: ${context.deploymentID()}");
                    });
                });
            }
        });
    }
}
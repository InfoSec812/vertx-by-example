import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.AbstractVerticle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Exercise9_2 extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        // Get a reference to clusterWide map called "shared"
        vertx.sharedData().getClusterWideMap("shared", res -> {
            if (res.succeeded()) {
                // Write to the map and await success
                res.result().put("deployments", Arrays.asList(context.deploymentID()), res1 -> {
                    // Deploy AnotherVerticle 10 times
                    IntStream.rangeClosed(1, 10).forEach(i -> {
                        vertx.deployVerticle("java:ClusteredVerticle.java");
                    });
                });
            }
        });

        vertx.setPeriodic(100, this::showDeployedVerticles);
    }

    void showDeployedVerticles(Long t) {
        // Print the list of deployment IDs stored in the shared data local Map
        LoggerFactory.getLogger("Exercise9_2").info("Polling shared data map");

        // Get reference to clusterWide map called "shared"
        vertx.sharedData().getClusterWideMap("shared", res -> {
            if (res.succeeded()) {
                // Get the "deployments" value from the AsyncMap
                res.result().get("deployments", res1 -> {

                    // Iterate over list of values
                    ((List<String>)res1.result()).stream().forEach(it -> {
                        System.out.println(it);
                    });
                    System.out.println();
                    System.out.println();
                });
            }
        });
    }
}
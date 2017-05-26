import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.shareddata.LocalMap;

import java.util.stream.IntStream;

public class Exercise9_1 extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        // Deploy AnotherVerticle 10 times
        IntStream.rangeClosed(1, 10).forEach(i -> {
            vertx.deployVerticle("java:AnotherVerticle.java");
        });

        vertx.setPeriodic(100, this::showDeployedVerticles);
    }

    void showDeployedVerticles(Long t) {
        // Print the list of deployment IDs stored in the shared data local Map
        LoggerFactory.getLogger("Exercise9_1").info("Polling shared data map");
        LocalMap<String, Object> localMap = vertx.sharedData().getLocalMap("shared");
        localMap.keySet().stream().forEach(key -> {
            System.out.println(key+" - "+localMap.get(key));
        });
        System.out.println();
        System.out.println();
    }
}
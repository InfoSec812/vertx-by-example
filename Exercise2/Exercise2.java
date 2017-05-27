import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonObject;

public class Exercise2 extends AbstractVerticle {

    public void start() {
        // Create a JSON response
        String response = new JsonObject().put("ok", true).encode();

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(req -> req.response().end(response)) // Add request handler
             .listen(8080, "127.0.0.1"); // Listen on http://127.0.0.1:8080
    }
}
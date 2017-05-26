import io.vertx.core.AbstractVerticle;;
import io.vertx.core.json.JsonObject;;
import io.vertx.core.json.JsonObject;;

public class Exercise2 extends AbstractVerticle {

    public void start() {
        // Create a JSON response
        String response = new JsonObject().put("ok", true).encode();

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(req -> {    // Register a request handler for the HttpServer
                 req.response().end(response);
             })
             .listen(8080, "127.0.0.1"); // Listen on port 8080 and interface `127.0.0.1`
    }
}
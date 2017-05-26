import io.vertx.core.AbstractVerticle;;
import io.vertx.core.json.JsonObject;;
import io.vertx.core.http.HttpServer;;

public class Solution1 extends AbstractVerticle {

    public void start() {
        // Create a new HttpServer
        HttpServer server = vertx.createHttpServer();

        // Create a JSON response
        JsonObject response = new JsonObject().put("ok", true).encode();

        // Register a request handler for the HttpServer
        server.requestHandler(req -> req.response().putHeader('Content-Type', 'application/json').end(response));

        // Listen on port 8080 and interface `127.0.0.1`
        server.listen(8080, '127.0.0.1');
    }
}
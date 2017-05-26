import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.HttpServerRequest;

public class Solution1 extends AbstractVerticle {

    protected void requestHandler(HttpServerRequest req) {
        // Create a JSON response
        String response = new JsonObject().put("ok", true).put("path", req.path()).encode();
        req.response().end(response);
    }

    public void start() {
        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(this::requestHandler) // Register a request handler
             .listen(8080, "127.0.0.1"); // Listen on port 8080 and interface `127.0.0.1`
    }
}
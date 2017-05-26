import io.vertx.core.AbstractVerticle;;
import io.vertx.core.json.JsonObject;;
import io.vertx.core.http.HttpServerRequest;;

import java.util.HashMap;;
import java.util.Map;;

public class Solution2 extends AbstractVerticle {

    protected void requestHandler(HttpServerRequest req) {
        // Create a JSON response
        Map<String, Object> headers = new HashMap<>();
        req.headers().names().stream().forEach(name -> {
            headers.put(name, req.headers().getAll(name));
        });
        String response = new JsonObject()
                                    .put("ok", true)
                                    .put("path", req.path())
                                    .put("headers", new JsonObject(headers))
                                    .encodePrettily();
        req.response().putHeader("Content-Type", "application/json").end(response);
    }

    public void start() {
        vertx.createHttpServer()                     // Create a new HttpServer
             .requestHandler(this::requestHandler)   // Register a request handler
             .listen(8080, "127.0.0.1");  // Listen on port 8080 and interface `127.0.0.1`
    }
}
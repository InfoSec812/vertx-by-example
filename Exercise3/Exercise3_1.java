import io.vertx.core.Handler;;
import io.vertx.core.AbstractVerticle;;
import io.vertx.core.json.JsonObject;;
import io.vertx.core.http.HttpServerRequest;;

public class Exercise3_1 extends AbstractVerticle {

    private class RequestHandler implements Handler<HttpServerRequest> {
        public void handle(HttpServerRequest req) {
            String response = new JsonObject().put("ok", true).encode();
            req.response().end(response);
        }
    }

    public void start() {
        vertx.createHttpServer()                     // Create a new HttpServer
             .requestHandler(new RequestHandler())
             .listen(8080, "127.0.0.1");  // Listen on port 8080 and interface `127.0.0.1`
    }
}
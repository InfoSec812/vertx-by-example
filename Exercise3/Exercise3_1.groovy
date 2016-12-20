import io.vertx.core.Handler
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise3_1 extends GroovyVerticle {

    private class RequestHandler implements Handler<HttpServerRequest> {
        void handle(HttpServerRequest req) {
            def response = new JsonObject([ok: true]).encode()
            req.response().end(response)
        }
    }

    void start() {
        def response = new JsonObject([ok: true]).encode()  // Create a JSON response
        vertx.createHttpServer()                            // Create a new HttpServer
             .requestHandler(new RequestHandler())
             .listen(8080, '127.0.0.1')                     // Listen on port 8080 and interface `127.0.0.1`
    }
}
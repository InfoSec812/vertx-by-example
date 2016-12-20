import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise3_2 extends GroovyVerticle {

    /**
     * Handle HttpServerRequests
     */
    void handleRequest(HttpServerRequest req) {
        def response = new JsonObject([ok: true]).encode()
        req.response().end(response)
    }

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(this.&handleRequest) // Register a request handler
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
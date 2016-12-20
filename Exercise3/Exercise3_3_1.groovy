import io.vertx.core.json.JsonObject
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle

class Exercise3_3_1 extends GroovyVerticle {

    void start() {

        def reqHandler = { HttpServerRequest req ->
            // Create a JSON response
            def response = new JsonObject([ok: true]).encode()
            req.response().end(response)
        }

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(reqHandler) // Register a request handler
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
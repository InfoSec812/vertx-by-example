import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class HelloWorld extends GroovyVerticle {

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler({ req ->    // Register a request handler
                 req.response().end(response)
             })
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
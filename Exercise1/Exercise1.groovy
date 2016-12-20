import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise1 extends GroovyVerticle {

    void start() {
        // Create a new HttpServer
        def server = vertx.createHttpServer()

        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        // Register a request handler for the HttpServer
        server.requestHandler({ req ->
                 req.response().end(response) })

        // Listen on port 8080 and interface `127.0.0.1`
        server.listen(8080, '127.0.0.1')
    }
}
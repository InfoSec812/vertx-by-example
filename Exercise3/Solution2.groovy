import io.vertx.core.json.JsonObject
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle

class Solution2 extends GroovyVerticle {

    void start() {

        def reqHandler = { HttpServerRequest req ->
            // Create a JSON response
            def headers = [:]
            req.headers().names().each { it ->
                def valueList = []
                headers.put(it, req.headers().getAll(it))
            }
            def response = new JsonObject([
                ok: true,
                path: req.path(),
                headers: headers
            ]).encodePrettily()
            req.response().putHeader('Content-Type', 'application/json').end(response)
        }

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(reqHandler) // Register a request handler
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
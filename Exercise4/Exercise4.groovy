import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

class Exercise4 extends GroovyVerticle {

    void start() {
        def router = Router.router(vertx)

        router.get('/')              .handler(this.&rootHandler)
        router.get('/something/else').handler(this.&otherHandler)

        vertx.createHttpServer()             // Create a new HttpServer
             .requestHandler(router.&accept) // Register a request handler
             .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: true, path: ctx.request().path()]).encode())
    }

    void otherHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: false, message: 'Something Else']).encode())
    }
}
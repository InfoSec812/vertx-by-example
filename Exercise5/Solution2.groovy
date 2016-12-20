import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

class Solution2 extends GroovyVerticle {

    void start() {
        def router = Router.router(vertx)

        router.get('/')            .handler(this.&rootHandler)
        router.get('/customer/:id').handler(this.&custHandler)
        router.get('/customer/:id/address/:index').handler(this.&addrHandler)
        router.getWithRegex($/^/product/(\d+)/$).handler(this.&regexHandler)

        vertx.createHttpServer()             // Create a new HttpServer
            .requestHandler(router.&accept) // Register a request handler
            .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: true, path: ctx.request().path()]).encode())
    }

    void custHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([
           ok: false,
           custID: ctx.request().getParam('id')
        ]).encode())
    }

    void addrHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([
           ok: false,
           custID: ctx.request().getParam('id'),
           addrIndex: ctx.request().getParam('index')
        ]).encode())
    }

    void regexHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([
           ok: false,
           product: ctx.request().getParam('param0')
        ]).encode())
    }
}
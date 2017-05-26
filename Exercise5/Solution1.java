import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class Solution1 extends AbstractVerticle {

    public void start() {
        def router = Router.router(vertx)

        router.get('/')            .handler(this::rootHandler)
        router.get('/customer/:id').handler(this::custHandler)
        router.get('/customer/:id/address/:index').handler(this::addrHandler)

        vertx.createHttpServer()             // Create a new HttpServer
            .requestHandler(router::accept) // Register a request handler
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
}
import io.vertx.core.AbstractVerticle;;
import io.vertx.ext.web.Router;;
import io.vertx.ext.web.RoutingContext;;
import io.vertx.core.json.JsonObject;;

public class Exercise4 extends AbstractVerticle {

    public public void start() {
        Router router = Router.router(vertx);

        router.get("/")              .handler(this::rootHandler);
        router.get("/something/else").handler(this::otherHandler);

        vertx.createHttpServer()                         // Create a new HttpServer
             .requestHandler(router::accept)             // Register a request handler
             .listen(8080, "127.0.0.1");      // Listen on 127.0.0.1:8080
    }

    public void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject().put("ok", true).put("path", ctx.request().path()).encode());
    }

    public void otherHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject().put("ok", false).put("message", "Something Else").encode());
    }
}
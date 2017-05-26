import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;

public class Exercise14 extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(this::authHandler);
        router.route("/rest/*").handler(this::restHandler);
        router.get("/rest/customer/:id").handler(this::customerByIdHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, "0.0.0.0");
    }

    void authHandler(RoutingContext ctx) {
        // Do something to validate authentication
        ctx.put("authenticated", true);
        ctx.next();
    }

    void restHandler(RoutingContext ctx) {
        // All REST requests will have certain common requirements

        ctx.response()
            .putHeader("Content-Type", "application/json") // Set the response Content-Type to application/json
            .putHeader("Cache-Control", "nocache") // Disable caching for browsers which respect this header
            .putHeader("Expires", "Tue, 15 Nov 1994 12:45:26 GMT"); // Set some expiry date in the past to help prevent caching
        ctx.next();
    }

    void customerByIdHandler(RoutingContext ctx) {
        if (ctx.get("authenticated")) {
            // The "authenticated" value is set in the authHandler method/route, and so it should be present here!!
            ctx.response().setStatusCode(OK.code())
                          .setStatusMessage(OK.reasonPhrase())
                          .end("{}");  // The headers set in restHandler are already set as well!
        } else {
            ctx.response().setStatusCode(UNAUTHORIZED.code())
                          .setStatusMessage(UNAUTHORIZED.reasonPhrase())
                          .end("{}");  // The headers set in restHandler are already set as well!
        }
    }
}
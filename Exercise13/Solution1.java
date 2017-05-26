import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class Solution1 extends AbstractVerticle {

    public static final Logger LOG = LoggerFactory.getLogger("Solution1");
    private JsonObject config;

    @Override
    public void start() throws Exception {
        if (vertx.getOrCreateContext().config().isEmpty()) {
            config = new JsonObject()
                            .put("webHost", "www.google.com")
                            .put("webUri", "/")
                            .put("webPort", 443)
                            .put("webSSL", true)
                            .put("hostname", "www.reddit.com")
                            .put("filename", System.getenv().get("PWD")+"/Exercise1/Exercise1.java")
                            .put("dnsServer", "8.8.8.8")
                            .put("dnsPort", 53);
        } else {
            config = vertx.getOrCreateContext().config();
        }

        Router router = Router.router(vertx);

        router.route("/merged/").handler(this::reqHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080, "0.0.0.0");
    }

    void reqHandler(RoutingContext ctx) {
        LOG.info("Request recieved");
        List<Future> futureList = new ArrayList<>();

        Future<String> googFuture = Future.future();
        futureList.add(googFuture);
        vertx.createHttpClient(new HttpClientOptions()).getNow(443, config.getString("webHost"), config.getString("webUri"), resp -> this.httpClientResponseHandler(googFuture, resp));

        Future<String> fileFuture = Future.future();
        futureList.add(fileFuture);
        vertx.fileSystem().readFile(config.getString("filename"), res -> {
            if (res.succeeded()) {
                fileFuture.complete(res.result().toString());
            } else {
                fileFuture.fail(res.cause());
            }
        });

        Future<String> dnsFuture = Future.future();
        futureList.add(dnsFuture);
        vertx.createDnsClient(config.getInteger("dnsPort"), config.getString("dnsServer")).lookup4(config.getString("hostname"), dnsFuture.completer());

        CompositeFuture.all(futureList).setHandler(cf -> this.resultHandler(ctx, cf));
    }

    void httpClientResponseHandler(Future googFuture, HttpClientResponse res) {
        LOG.info("HTTP Request response recieved.");
        if (res.statusCode() == OK.code()) {
            res.bodyHandler(b -> googFuture.complete(b.toString()));
        } else {
            LOG.error("HTTP request failed");
            googFuture.fail(res.statusMessage());
        }
    }

    void resultHandler(RoutingContext ctx, AsyncResult<CompositeFuture> res) {
        LOG.info("All futures resolved.");
        if (res.failed()) {
            LOG.error("One or more items failed.", res.cause());
            ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code())
                    .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
                    .putHeader("Content-Type", "text/plain")
                    .end(res.cause().getLocalizedMessage());
        } else {
            LOG.info("All futures succeeded.");
            CompositeFuture cf = res.result();
            String html = "";
            String ipAddr = "";
            String fileContent = "";
            LOG.info("CompositeFuture Size: ${cf.size()}");
            for (int x=0; x<cf.size(); x++) {
                if (((String)cf.resultAt(x)).length()>2000) {
                    html = cf.resultAt(x);
                } else if (((String)cf.resultAt(x)).length()<30) {
                    ipAddr = cf.resultAt(x);
                } else {
                    fileContent = cf.resultAt(x);
                }
            }
            LOG.info("All results stored");

            String insert = "<pre>"+fileContent+" - "+ipAddr+"</pre></body>";

            LOG.info("Insert generated: "+insert);

            try {
                Pattern p = Pattern.compile("</body>");
                Matcher m = p.matcher(html);
                String output = m.replaceAll(insert);

                LOG.info("Output generated");
                ctx.response().setStatusCode(OK.code())
                        .setStatusMessage(OK.reasonPhrase())
                        .putHeader("Content-Type", "text/html")
                        .end(output);
            } catch (Throwable t) {
                LOG.error("Something Bad!", t);
                ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code())
                        .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
                        .putHeader("Content-Type", "text/plain")
                        .end(t.getLocalizedMessage());
            }
        }
    }
}
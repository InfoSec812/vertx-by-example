import io.vertx.core.AsyncResult
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.CompositeFuture
import io.vertx.groovy.core.Future
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.lang.groovy.GroovyVerticle

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import static io.netty.handler.codec.http.HttpResponseStatus.OK

class Solution1 extends GroovyVerticle {

    public static final Logger LOG = LoggerFactory.getLogger(Solution1)

    def config = [
        webHost: 'www.google.com',
        webUri: '/',
        webPort: 443,
        webSSL: true,
        hostname: 'www.reddit.com',
        filename: System.getenv().PWD+'/Exercise1/Exercise1.groovy',
        dnsServer: '8.8.8.8',
        dnsPort: 53
    ]

    @Override
    void start() throws Exception {
        Router router = Router.router(vertx)

        config += context.config()

        router.route('/merged/').handler(this.&reqHandler)

        vertx.createHttpServer().requestHandler(router.&accept).listen(8080, '0.0.0.0')
    }

    void reqHandler(RoutingContext ctx) {
        LOG.info('Request recieved')
        def futureList = []

        def googFuture = Future.future()
        futureList.add(googFuture)
        vertx.createHttpClient([ssl:true]).getNow(config.webPort, config.webHost, config.webUri, this.&httpClientResponseHandler.curry(googFuture))

        def fileFuture = Future.future()
        futureList.add(fileFuture)
        vertx.fileSystem().readFile(config.filename, fileFuture.completer())

        def dnsFuture = Future.future()
        futureList.add(dnsFuture)
        vertx.createDnsClient(config.dnsPort, config.dnsServer).lookup4(config.hostname, dnsFuture.completer())

        CompositeFuture.all(futureList).setHandler(this.&resultHandler.curry(ctx))
    }

    void httpClientResponseHandler(Future googFuture, HttpClientResponse res) {
        LOG.info('HTTP Request response recieved.')
        if (res.statusCode() == OK.code()) {
            res.bodyHandler({ b -> googFuture.complete(b)})
        } else {
            LOG.error('HTTP request failed')
            googFuture.fail(res.statusMessage())
        }
    }

    void resultHandler(RoutingContext ctx, AsyncResult<CompositeFuture> res) {
        LOG.info('All futures resolved.')
        if (res.failed()) {
            LOG.error('One or more items failed.', res.cause())
            ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code())
                          .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
                          .putHeader('Content-Type', 'text/plain')
                          .end(res.cause().localizedMessage)
        } else {
            LOG.info('All futures succeeded.')
            def cf = res.result()
            String html
            String ipAddr
            String fileContent
            LOG.info("CompositeFuture Size: ${cf.size()}")
            def range = cf.size() - 1
            (0..range).each { idx ->
                switch (cf.resultAt(idx)) {
                    case { it -> (it.class.simpleName=='String') }: // DNS response
                        LOG.info('DNS Parsed')
                        ipAddr = cf.resultAt(idx)
                        break
                    case { it -> (it.class.simpleName=='Buffer' && ((Buffer)it).length()>2000) }:  // HTML response from Google
                        LOG.info('HTML Parsed')
                        def b = cf.resultAt(idx) as Buffer
                        html = b.toString()
                        break
                    default:
                        LOG.info('FILE Parsed')
                        def b = cf.resultAt(idx) as Buffer
                        fileContent = b.toString()
                }
            }
            LOG.info('All results stored')

            def insert = "<pre>\n${ipAddr}\n\n${fileContent}</pre></body>".toString()

            LOG.info("Insert generated: ${insert}")

            try {
                def matcher = (html =~ /<.body>/)
                def output = matcher.replaceAll(insert)

                LOG.info('Output generated')
                ctx.response().setStatusCode(OK.code())
                              .setStatusMessage(OK.reasonPhrase())
                              .putHeader('Content-Type', 'text/html')
                              .end(output)
            } catch (Throwable t) {
                LOG.error('Something Bad!', t)
                ctx.response().setStatusCode(INTERNAL_SERVER_ERROR.code())
                              .setStatusMessage(INTERNAL_SERVER_ERROR.reasonPhrase())
                              .putHeader('Content-Type', 'text/plain')
                              .end(t.localizedMessage)
            }
        }
    }
}
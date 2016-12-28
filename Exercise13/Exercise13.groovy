import io.vertx.core.AsyncResult
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.CompositeFuture
import io.vertx.groovy.core.Future
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.lang.groovy.GroovyVerticle

class Exercise13 extends GroovyVerticle {


    public static final Logger LOG = LoggerFactory.getLogger(Exercise13)

    @Override
    void start() throws Exception {
        Router router = Router.router(vertx)

        router.route('/merged/').handler(this.&reqHandler)

        vertx.createHttpServer().requestHandler(router.&accept).listen(8080, '0.0.0.0')
    }

    void reqHandler(RoutingContext ctx) {
        LOG.info('Request recieved')
        def futureList = []

        def googFuture = Future.future()
        futureList.add(googFuture)
        vertx.createHttpClient([ssl:true]).getNow(443, 'www.google.com', '/', { res ->
            LOG.info('HTTP Request response recieved.')
            if (res.statusCode()==200) {
                res.bodyHandler({ b ->
                    LOG.info('HTML Body content recieved')
                    googFuture.complete(b)
                })
            } else {
                LOG.error('HTTP request failed')
                googFuture.fail(res.statusMessage())
            }
        })

        def fileFuture = Future.future()
        futureList.add(fileFuture)
        vertx.fileSystem().readFile("${System.getenv().PWD}/../.gitignore", fileFuture.completer())

        def dnsFuture = Future.future()
        futureList.add(dnsFuture)
        vertx.createDnsClient(53, '8.8.8.8').lookup4('www.google.com', dnsFuture.completer())

        CompositeFuture.all(futureList).setHandler(this.&resultHandler.curry(ctx))
    }

    void resultHandler(RoutingContext ctx, AsyncResult<CompositeFuture> res) {
        LOG.info('All futures resolved.')
        if (res.failed()) {
            LOG.error('One or more items failed.', res.cause())
            ctx.response().setStatusCode(500)
                          .setStatusMessage('INTERNAL SERVER ERROR')
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
                ctx.response().setStatusCode(200)
                              .setStatusMessage('OK')
                              .putHeader('Content-Type', 'text/html')
                              .end(output)
            } catch (Throwable t) {
                LOG.error('Something Bad!', t)
                ctx.response().setStatusCode(500)
                              .setStatusMessage('INTERNAL SERVER ERROR')
                              .putHeader('Content-Type', 'text/plain')
                              .end(t.localizedMessage)
            }
        }
    }
}
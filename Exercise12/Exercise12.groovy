import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.http.HttpClientResponse
import io.vertx.lang.groovy.GroovyVerticle

class Exercise12 extends GroovyVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Exercise12)

    @Override
    void start() throws Exception {
        def client = vertx.createHttpClient()
                          .getNow('www.google.com', '/', this.&responseHandler)
    }

    void responseHandler(HttpClientResponse response) {
        if (response.statusCode()==200 && response.statusMessage()=='OK') {
            LOG.info('Success!')
        } else {
            LOG.warn("Got ${response.statusCode()} as the response code.")
        }
        vertx.close()
    }
}
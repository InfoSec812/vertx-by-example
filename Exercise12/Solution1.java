import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.AbstractVerticle;

public class Solution1 extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Solution1)

    @Override
    public void start() throws Exception {
        def req = vertx.createHttpClient()
                          .get('www.google.com', '/', this.&responseHandler)
        def base64key = Base64.encoder.encodeToString(new StringBuilder('username')
                                                .append(":").append('password')
                                                .toString().getBytes())

        req.putHeader('Authorization', "Basic ${base64key}".toString())
        req.end()
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
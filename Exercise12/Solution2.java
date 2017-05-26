import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.AbstractVerticle;

public class Solution2 extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(Solution2)

    @Override
    public void start() throws Exception {
        def req = vertx.createHttpClient()
                          .post('www.google.com', '/', this::responseHandler)
        req.end(new JsonObject([ok: true, status: 'Good', detail: 'Something']).encode())
    }

    void responseHandler(HttpClientResponse response) {
        if (response.statusCode()==202 && response.statusMessage()=='OK') {
            LOG.info('Success!')
        } else {
            LOG.warn("Got ${response.statusCode()} as the response code.")
        }
        vertx.close()
    }
}
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

public class Solution1 extends AbstractVerticle {

    public void start() {
        JsonObject opts = new JsonObject()
                .put("host", "0.0.0.0")
                .put("port", 1080)
                .put("logActivity", true);

        NetServer server = vertx.createNetServer(new NetServerOptions(opts));
        server.connectHandler(this::connectHandler).listen();
        LoggerFactory.getLogger("Solution1").info("Deployed NetServer Solution1");
    }

    void connectHandler(NetSocket socket) {
        socket.handler(b -> this.dataHandler(socket, b));
    }

    void dataHandler(NetSocket socket, Buffer b) {
        if (b.toString().matches("[A-Za-z0-9 \n\r]*")) {
            socket.write("Hello "+b.toString());
        }
    }
}
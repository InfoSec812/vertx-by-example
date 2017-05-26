import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.AbstractVerticle;

public class Exercise10 extends AbstractVerticle {

    public void start() {
        JsonObject opts = new JsonObject()
                .put("host", "0.0.0.0")
                .put("port", 1080)
                .put("logActivity", true);

        NetServer server = vertx.createNetServer(new NetServerOptions(opts));
        server.connectHandler(this::connectHandler).listen();
    }

    void connectHandler(NetSocket socket) {
        socket.handler(b -> this.dataHandler(socket, b));
    }

    void dataHandler(NetSocket socket, Buffer b) {
        LoggerFactory.getLogger("Exercise10").info(b.toString());
        socket.write(b);
    }
}
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.AbstractVerticle;

public class Exercise10 extends AbstractVerticle {

    public void start() {
        def opts = [
            host: '0.0.0.0',
            port: 1080,
            logActivity: true
        ]

        NetServer server = vertx.createNetServer(opts)
        server.connectHandler(this.&connectHandler).listen()
    }

    void connectHandler(NetSocket socket) {
            socket.handler(this.&dataHandler.curry(socket))
    }

    void dataHandler(NetSocket socket, Buffer b) {
        LoggerFactory.getLogger(Exercise10).info(b.toString())
        socket.write(b)
    }
}
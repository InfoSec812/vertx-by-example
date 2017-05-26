import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

public class Solution1 extends AbstractVerticle {

    public void start() {
        def opts = [
            host: '0.0.0.0',
            port: 1080,
            logActivity: true
        ]

        NetServer server = vertx.createNetServer(opts)
        server.connectHandler(this.&connectHandler).listen()
        LoggerFactory.getLogger(Solution1).info('Deployed NetServer Solution1')
    }

    void connectHandler(NetSocket socket) {
            socket.handler(this.&dataHandler.curry(socket))
    }

    void dataHandler(NetSocket socket, Buffer b) {
        if (b.toString().matches(/[A-Za-z0-9 \n\r]*/)) {
            socket.write("Hello ${b.toString()}".toString())
        }
    }
}
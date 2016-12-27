import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.net.NetServer
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.NetSocket

class Exercise10 extends AbstractVerticle {

    void start() {
        NetServerOptions opts = new NetServerOptions()
                .setHost("0.0.0.0")
                .setPort(1080)
                .setLogActivity(true)

        NetServer server = vertx.createNetServer(opts)
        server.connectHandler(this.&connectHandler).listen()
    }

    void connectHandler(NetSocket socket) {
            socket.handler(this.&dataHandler.curry(socket))
    }

    void dataHandler(NetSocket socket, Buffer b) {
        LoggerFactory.getLogger(EchoServer).info(b.toString())
        socket.write(b)
    }
}
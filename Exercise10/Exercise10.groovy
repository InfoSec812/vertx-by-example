import io.vertx.core.buffer.Buffer
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.net.NetServer
import io.vertx.groovy.core.net.NetSocket
import io.vertx.lang.groovy.GroovyVerticle

class Exercise10 extends GroovyVerticle {

    void start() {
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
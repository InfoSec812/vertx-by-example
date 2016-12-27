import io.vertx.core.AsyncResult
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.datagram.DatagramPacket
import io.vertx.groovy.core.datagram.DatagramSocket
import io.vertx.lang.groovy.GroovyVerticle

class Exercise11 extends GroovyVerticle {
    private final Logger LOG = LoggerFactory.getLogger(UDPEchoServer)

    @Override
    void start() throws Exception {
        DatagramSocket socket = vertx.createDatagramSocket()

        socket.listen(1080, "0.0.0.0", this.&socketHandler)
    }

    void socketHandler(AsyncResult<DatagramSocket> res) {
        if (res.succeeded()) {
            // Successfully received a datagram
            def socket = res.result()
            socket.handler(this.&datagramHandler.curry(socket))
        }
    }

    void datagramHandler(DatagramSocket socket, DatagramPacket p) {
        socket.send(p.data(), p.sender().port(), p.sender().host(), this.&sendHandler)
    }

    void sendHandler(sent) {
        if (sent.succeeded()) {
            LOG.info("SUCCESS")
        } else {
            LOG.error("FAILED")
        }
    }
}
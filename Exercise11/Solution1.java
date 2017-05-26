import io.vertx.core.AsyncResult;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramPacket;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.AbstractVerticle;

public class Solution1 extends AbstractVerticle {
    private final Logger LOG = LoggerFactory.getLogger("UDPEchoServer");

    @Override
    public void start() throws Exception {
        DatagramSocket socket = vertx.createDatagramSocket();

        socket.listen(1080, "0.0.0.0", this::socketHandler);
    }

    void socketHandler(AsyncResult<DatagramSocket> res) {
        if (res.succeeded()) {
            // Successfully received a datagram
            DatagramSocket socket = res.result()''
            socket.handler(p -> this.datagramHandler(socket, p));
        }
    }

    void datagramHandler(DatagramSocket socket, DatagramPacket p) {
        if (p.data().toString().matches("[A-Za-z0-9 \n\r]*")) {
            Buffer reply = Buffer.buffer("Hello ").appendBuffer(p.data());
            socket.send(reply, p.sender().port(), p.sender().host(), this::sendHandler);
        }
    }

    void sendHandler(AsyncResult<DatagramSocket> sent) {
        if (sent.succeeded()) {
            LOG.info("SUCCESS");
        } else {
            LOG.error("FAILED");
        }
    }
}
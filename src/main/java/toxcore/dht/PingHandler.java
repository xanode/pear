package toxcore.dht;

import java.net.DatagramPacket;

public class PingHandler extends Handler {

    protected PingHandler(Manager manager, DatagramPacket packet) {
        super(manager, packet);
    }

    @Override
    public void run() {
        // Decode packet

    }
}

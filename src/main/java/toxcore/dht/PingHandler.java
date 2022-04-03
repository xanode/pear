package toxcore.dht;

import java.net.DatagramPacket;

public class PingHandler extends Handler<Node, Ping> implements Runnable {

    final Manager manager;
    final DatagramPacket packet;

    protected PingHandler(Manager manager, DatagramPacket packet) {
        super();
        this.manager = manager;
        this.packet = packet;
    }

    @Override
    public void run() {
        // Decode packet

    }
}

package toxcore.dht;

import java.net.DatagramPacket;

public abstract class Handler implements Runnable {

    final Manager manager;
    final DatagramPacket packet;

    protected Handler(Manager manager, DatagramPacket packet) {
        this.manager = manager;
        this.packet = packet;
    }

}

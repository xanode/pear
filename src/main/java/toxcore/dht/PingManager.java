package toxcore.dht;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;

public class PingManager extends Manager {

    private HashMap<byte[], Handler> askedPing;
    private HashMap<byte[], Thread> handledPing;

    protected PingManager(int port) throws SocketException {
        super(port);
    }

    @Override
    public void manage() {
        while (this.isRunning()) {
            DatagramPacket packet = new DatagramPacket(this.getBuffer(), this.getBuffer().length);
            try {
                this.receive(packet);
                // TODO: check if ping comes from here
                Thread thread = new Thread(new PingHandler(this, packet));
                this.serviceThread(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
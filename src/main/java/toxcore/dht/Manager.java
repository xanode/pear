package toxcore.dht;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public abstract class Manager {

    private ArrayList<Thread> servicingThread;
    private boolean running;
    private DatagramSocket socket;
    private int port;
    private byte[] buffer;

    protected Manager(int port) throws SocketException {
        this.servicingThread = new ArrayList<>();
        this.running = true;
        // Set network
        this.port = port;
        this.buffer = new byte[65536];
        this.socket = new DatagramSocket(port);
    }

    public abstract void manage();

    public void closeManager() {
        this.running = false;
        try {
            for (Thread thread: servicingThread) {
                if (thread.isAlive()) {
                    thread.join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void serviceThread(Thread thread) {
        this.servicingThread.add(thread);
    }

    protected boolean isRunning() {
        return this.running;
    }

    protected int getPort() {
        return this.port;
    }

    protected byte[] getBuffer() {
        return this.buffer;
    }

    protected void receive(DatagramPacket packet) throws IOException {
        this.socket.receive(packet);
    }
}

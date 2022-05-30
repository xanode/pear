package fr.xanode.pear.core.dht;

import com.muquit.libsodiumjna.SodiumUtils;
import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import fr.xanode.pear.core.dht.network.Network;
import fr.xanode.pear.core.dht.network.Node;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class Main {
    public static void main(String... args) throws SodiumLibraryException {
        try {
            DHT dht = new DHT();
            Signal.handle(new Signal("INT"), signal -> dht.close()); // Close DHT when interrupted by SIGINT
            if (args.length == 2) {
                InetAddress bootstrapNodeInetAddress = InetAddress.getByName(args[1]);
                byte[] nodeKey = SodiumUtils.hex2Binary(args[0]);
                Node node = new Node(
                        dht,
                        nodeKey,
                        bootstrapNodeInetAddress,
                        Network.DHT_PORT
                );
                new Thread(node::isAlive).start();
            }
            dht.start();
        } catch (IndexOutOfBoundsException e) {
            log.error("Not enough arguments (" + args.length + "): " + e.getMessage());
        } catch (UnknownHostException | SocketException e) {
            log.error(e.getMessage());
        }
    }
}

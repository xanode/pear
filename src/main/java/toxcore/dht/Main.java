package toxcore.dht;

import com.muquit.libsodiumjna.exceptions.SodiumLibraryException;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Signal;

import java.net.SocketException;

@Slf4j
public class Main {
    public static void main(String[] args) throws SocketException, SodiumLibraryException {
        DHT dht = new DHT();
        Signal.handle(new Signal("INT"), signal -> dht.getNetwork().close()); // Close DHT when interrupted by SIGINT
        dht.getNetwork().handle();
    }
}

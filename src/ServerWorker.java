import java.net.DatagramSocket;

public class ServerWorker implements Runnable {
    private final DatagramSocket clientSocket;

    ServerWorker(DatagramSocket cls) {
        this.clientSocket = cls;
    }

    @Override
    public void run() {

    }
}

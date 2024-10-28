import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CommUDP {

    public static void main(String[] args) throws IOException {

        final int PORT = 65432;
        final int MAX_BUFFER_SIZE = 1024;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss:SSS");

        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[MAX_BUFFER_SIZE];

        DatagramPacket receivedPacketFromSocket;

        System.out.println("\nAgent is working...\n");

        while (true) {
            receivedPacketFromSocket = new DatagramPacket(buffer, MAX_BUFFER_SIZE);
            socket.receive(receivedPacketFromSocket); // receives the UDP packet from the socket

            InetAddress clientAddress = receivedPacketFromSocket.getAddress(); // address of the Manager
            int clientPort = receivedPacketFromSocket.getPort(); // port of the Manager

            System.out.println(Arrays.toString(receivedPacketFromSocket.getData()));


            LocalDateTime timestamp = LocalDateTime.now();
            Duration diff = Duration.between(packet.getTimestamp(), timestamp);
            System.out.println("[" + timestamp.format(formatter) + ", Address: " + clientAddress + ", Port: " + clientPort + "]  " + packet.getType() + packet.getIIDList().getListElements());
            System.out.println("Time in transit: " + diff.toMillis() + " ms");
        }

    }

}

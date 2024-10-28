import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommUDP {

    public static void main(String[] args) throws IOException {

        final int PORT = 65432;
        final int MAX_BUFFER_SIZE = 1024;
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss:SSS");

        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[MAX_BUFFER_SIZE];

        DatagramPacket receivedDatagramPacketFromSocket;

        System.out.println("\nAgent is working...\n");

        while (true) {
            receivedDatagramPacketFromSocket = new DatagramPacket(buffer, MAX_BUFFER_SIZE);
            socket.receive(receivedDatagramPacketFromSocket); // receives the UDP packet from the socket

            InetAddress clientAddress = receivedDatagramPacketFromSocket.getAddress(); // address of the Manager
            int clientPort = receivedDatagramPacketFromSocket.getPort(); // port of the Manager

            String receivedPacket = new String(receivedDatagramPacketFromSocket.getData(),0, receivedDatagramPacketFromSocket.getLength());
            Frame packet = Frame.readPDU(receivedPacket);

            LocalDateTime timestamp = LocalDateTime.now();
            Duration diff = Duration.between(packet.getTimestamp(), timestamp);
            System.out.println("[" + timestamp.format(formatter) + ", Address: " + clientAddress + ", Port: " + clientPort + "]  " + packet.getType() + packet.getIIDList().getListElements());
            System.out.println("Time in transit: " + diff.toMillis() + " ms\n");
        }

    }

}

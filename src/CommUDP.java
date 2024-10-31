import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommUDP {
    private static final int PORT = 65432;
    private static final int MAX_BUFFER_SIZE = 1024;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss:SSS");

    public static void main(String[] args) {
        new CommUDP().startServer();
    }

    public void startServer() {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)){
            System.out.println("\nAgent is working on port " + PORT + "\n");

            // Loop to continuously receive incoming packets
            while (true) {
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                DatagramPacket receivedDatagramPacketFromSocket;

                receivedDatagramPacketFromSocket = new DatagramPacket(buffer, MAX_BUFFER_SIZE);
                serverSocket.receive(receivedDatagramPacketFromSocket); // receives the UDP packet from the socket

                Thread clientHandler = new Thread(new ClientHandler(serverSocket,receivedDatagramPacketFromSocket));
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivedPacket;

        public ClientHandler(DatagramSocket socket, DatagramPacket packet) {
            this.serverSocket = socket;
            this.receivedPacket = packet;
        }

        @Override
        public void run() {
            // Convert the UDP packet into a String
            String packetToString = new String(receivedPacket.getData(),0, receivedPacket.getLength());
            System.out.println(packetToString);

            InetAddress clientAddress = receivedPacket.getAddress(); // address of the Manager
            int clientPort = receivedPacket.getPort(); // port of the Manager

            Frame packet = Frame.readPDU(packetToString);

            LocalDateTime timestamp = LocalDateTime.now();
            Duration diff = Duration.between(packet.getTimestamp(), timestamp);

            System.out.println("[" + timestamp.format(Frame.displayFormatter) + ", Address: " + clientAddress + ", Port: " + clientPort + "]  " + packet.getType() + packet.getIIDList().getListElements());
            System.out.println("Time in transit: " + diff.toMillis() + " ms\n");

            // Repeat packet to Manager for testing
            byte[] sendBuffer = packetToString.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);

            try {
                serverSocket.send(sendPacket);

                System.out.println("Message sent");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

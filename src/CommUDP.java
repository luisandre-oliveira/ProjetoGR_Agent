import MIB.MibImp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class CommUDP {
    private static final int PORT = 65432;
    private static final int MAX_BUFFER_SIZE = 1024;
    private static final String TAG = "kdk847ufh84jg87g";

    public static void main(String[] args) {
        new CommUDP().startServer();
    }

    private static void sendPacket(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort, String type, String iid_list, String value_list, String error_list) throws IOException {
        DatagramPacket UDPPacket; // UDP packet to be sent
        String dataStringToBeSent = Frame.createPDU(TAG,type,LocalDateTime.now().format(Frame.packetFormatter),0,iid_list,value_list, error_list);
        byte[] packetInBytes = dataStringToBeSent.getBytes();

        if (packetInBytes.length <= MAX_BUFFER_SIZE) { // check for buffer overflow
            UDPPacket = new DatagramPacket(packetInBytes, packetInBytes.length, clientAddress, clientPort);
            serverSocket.send(UDPPacket);
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
            MibImp instance = MibImp.getInstance(); // included to force the singleton design pattern

            // Convert the UDP packet into a String
            String packetToString = new String(receivedPacket.getData(),0, receivedPacket.getLength());

            InetAddress clientAddress = receivedPacket.getAddress(); // address of the Manager
            int clientPort = receivedPacket.getPort(); // port of the Manager

            Frame framedPacket = Frame.readPDU(packetToString); // convert string into a Frame class packet

            LocalDateTime timestamp = LocalDateTime.now();
            Duration diff = Duration.between(framedPacket.getTimestamp(), timestamp);

            System.out.println("[" + timestamp.format(Frame.displayFormatter) + ", Address: " + clientAddress + ", Port: " + clientPort + "]  "
                    + framedPacket.getType() + framedPacket.getIIDList().getListElements() + framedPacket.getValueList().getListElements());
            System.out.println("Time in transit: " + diff.toMillis() + " ms\n");

            ArrayList<String> tempListOfValues;
            ArrayList<String> tempListOfErrors;

            switch (framedPacket.getType()) {

                case "G":

                    // Prepare packet with values and send to Manager
                    tempListOfValues = new ArrayList<>();
                    tempListOfErrors = new ArrayList<>();

                    for(String key_iid: framedPacket.getIIDList().getListElements()) {
                        String valueOfIID = MibImp.findValueByIID(key_iid);
                        if (!Objects.equals(valueOfIID, "-1")) {

                            if(valueOfIID.contains(",")){
                                String[] values = valueOfIID.split(",");
                                for(String value : values) {
                                    tempListOfValues.add(String.valueOf(value));
                                }
                            } else {
                                tempListOfValues.add(valueOfIID);
                            }


                        } else {
                            tempListOfErrors.add("5");
                        }
                    }

                    String iid_list = Frame.createList(framedPacket.getIIDList().getListElements());
                    String value_list = Frame.createList(tempListOfValues);
                    String error_list = Frame.createList(tempListOfErrors);

                    try {
                        sendPacket(serverSocket, clientAddress, clientPort, "R", iid_list, value_list, error_list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "S":

                    // valueOfIID is the value the iid holds. Ex: default value in 3.3.1 is 75
                    String valueOfIID = MibImp.findValueByIID(framedPacket.getIIDList().getListElements().get(0));

                    if(!Objects.equals(valueOfIID, "-1")) {
                        MibImp.setInstanceOfObject(framedPacket.getIIDList().getListElements().get(0), framedPacket.getValueList().getListElements().get(0));
                    }

                    break;

                default:

            }

        }

    }

    public void startServer() {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)){
            System.out.println("\nAgent is working on port " + PORT + "\n");

            Thread simulatorThread = new Thread(new Simulator());
            simulatorThread.setDaemon(true); // Set as daemon thread, so it stops with the main program
            simulatorThread.start();

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
}

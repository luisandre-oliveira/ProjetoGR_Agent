import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frame implements Serializable {
    private final String Tag;
    private final String Type;
    private final LocalDateTime Timestamp;
    private final int MessageIdentifier;
    private final CustomList IIDList;
    private final CustomList ValueList;
    private final CustomList ErrorList;
    public static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss:SSS");
    public static final DateTimeFormatter packetFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy:HH:mm:ss:SSS");


    public Frame(String tag, String type, LocalDateTime time, int msgid, CustomList iidlist, CustomList valuelist, CustomList errorlist) {
        this.Tag = tag;
        this.Type = type;
        this.Timestamp = time;
        this.MessageIdentifier = msgid;
        this.IIDList = iidlist;
        this.ValueList = valuelist;
        this.ErrorList = errorlist;
    }

    public String getTag() { return this.Tag; }
    public String getType() { return this.Type; }
    public LocalDateTime getTimestamp() { return Timestamp; }
    public int getMessageIdentifier() { return MessageIdentifier; }
    public CustomList getIIDList() { return IIDList; }
    public CustomList getValueList() { return ValueList; }
    public CustomList getErrorList() { return ErrorList; }

    public static String createPDU(String tag, String type, String timestamp, int mi, String iids, String values, String errors) {
        return tag + "\0" + type + "\0" + timestamp + "\0" + mi + "\0" + iids + "\0" + values + "\0" + errors + "\0";
    }

    public static String createList(ArrayList<String> listElements) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(listElements.size()).append("\0");

        for (String listElement : listElements) {
            stringBuilder.append(listElement);
            stringBuilder.append("\0");
        }

        if (!stringBuilder.isEmpty() && stringBuilder.charAt(stringBuilder.length() - 1) == '\0') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    public static Frame readPDU(String packet) {
        String[] splitPacket = packet.split("\0"); // 0 -> 3 is TAG TO MSGID // REST ARE LISTS, DEPENDING ON SIZE

        String tag; String type; LocalDateTime timestamp; int msgId;
        ArrayList<CustomList> bigList = new ArrayList<>(); // arraylist with the 3 lists, using readList()

        if(splitPacket.length > 4) { // making sure it has any lists, or it's just empty after (it should never be)
            String[] packetWithOnlyLists = Arrays.copyOfRange(splitPacket, 4, splitPacket.length); // get the lists part of the packet
            bigList = readList(packetWithOnlyLists);
        }

        tag = splitPacket[0];
        type = splitPacket[1];
        timestamp = LocalDateTime.parse(splitPacket[2], packetFormatter);
        msgId = Integer.parseInt(splitPacket[3]);

        return new Frame(tag, type, timestamp, msgId, bigList.get(0), bigList.get(1), bigList.get(2));
    }

    public static ArrayList<CustomList> readList(String[] packet) {
        int size_iids, size_values, size_errors;
        ArrayList<String> temp_iid_array, temp_value_array, temp_error_array;

        // Process IIDs
        size_iids = Integer.parseInt(packet[0]); // Get nº of iids
        if(size_iids > 0) {
            temp_iid_array = new ArrayList<>(Arrays.asList(packet).subList(1, size_iids + 1));
        } else {
            temp_iid_array = new ArrayList<>();
        }

        // Update packet array to exclude the processed IID part
        packet = Arrays.copyOfRange(packet, size_iids + 1, packet.length);

        // Process Values
        size_values = Integer.parseInt(packet[0]);
        if(size_values > 0) {
            temp_value_array = new ArrayList<>(Arrays.asList(packet).subList(1, size_values + 1));
        } else {
            temp_value_array = new ArrayList<>();
        }

        // Update packet array to exclude the processed Values par
        packet = Arrays.copyOfRange(packet, size_values + 1, packet.length);

        // Process Errors
        size_errors = Integer.parseInt(packet[0]);
        if(size_errors > 0) {
            temp_error_array = new ArrayList<>(Arrays.asList(packet).subList(1, size_errors + 1));
        } else {
            temp_error_array = new ArrayList<>();
        }

        CustomList iid_list = new CustomList(size_iids, temp_iid_array);
        CustomList values_list = new CustomList(size_values, temp_value_array);
        CustomList errors_list = new CustomList(size_errors, temp_error_array);

        ArrayList<CustomList> finalList = new ArrayList<>();
        finalList.add(iid_list); finalList.add(values_list); finalList.add(errors_list);

        return finalList;
    }
}

class CustomList implements Serializable {
    private final int numberElements;
    private final ArrayList<String> listElements;

    public CustomList(int numberOfElements, ArrayList<String> listOfElements) {
        this.numberElements = numberOfElements;
        this.listElements = listOfElements;
    }

    public int getNumberElements() { return numberElements; }
    public ArrayList<String> getListElements() { return listElements; }
}

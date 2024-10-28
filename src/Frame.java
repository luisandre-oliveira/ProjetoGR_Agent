import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frame implements Serializable {
    private final String Tag;
    private final char Type;
    private final LocalDateTime Timestamp;
    private final int MessageIdentifier;
    private final CustomList IIDList;
    private final CustomList ValueList;
    private final CustomList ErrorList;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss:SSS");

    public Frame(String tag, char type, LocalDateTime time, int msgid, CustomList iidlist, CustomList valuelist, CustomList errorlist) {
        this.Tag = tag;
        this.Type = type;
        this.Timestamp = time;
        this.MessageIdentifier = msgid;
        this.IIDList = iidlist;
        this.ValueList = valuelist;
        this.ErrorList = errorlist;
    }

    public String getTag() { return this.Tag; }
    public char getType() { return this.Type; }
    public LocalDateTime getTimestamp() { return Timestamp; }
    public int getMessageIdentifier() { return MessageIdentifier; }
    public CustomList getIIDList() { return IIDList; }
    public CustomList getValueList() { return ValueList; }
    public CustomList getErrorList() { return ErrorList; }

    public static Frame readPDU(String packet) {
        String[] splitPacket = packet.split("\0"); // 0 -> 3 is TAG TO MSGID // REST ARE LISTS, DEPENDING ON SIZE

        String tag; char type; LocalDateTime timestamp; int msgId;
        ArrayList<CustomList> bigList = new ArrayList<>(); // arraylist with the 3 lists, using readList()

        if(splitPacket.length > 4) { // making sure it has any lists or it's just empty after (it should never be)
            String[] packetWithOnlyLists = Arrays.copyOfRange(splitPacket, 4, splitPacket.length); // get the lists part of the packet
            bigList = readList(packetWithOnlyLists);
        }

        tag = splitPacket[0];
        type = splitPacket[1].charAt(0);
        timestamp = LocalDateTime.parse(splitPacket[2], formatter);
        msgId = Integer.parseInt(splitPacket[3]);

        return new Frame(tag, type, timestamp, msgId, bigList.get(0), bigList.get(1), bigList.get(2));
    }

    public static ArrayList<CustomList> readList(String[] packet) {
        int size_iids; int size_values; int size_errors;
        ArrayList<String> temp_iid_array; ArrayList<String> temp_value_array; ArrayList<String> temp_error_array;

        size_iids = Integer.parseInt(packet[0]); // get nº of iids

        if(size_iids > 0) {
            temp_iid_array = new ArrayList<>(Arrays.asList(packet).subList(1, size_iids + 1));
        } else {
            temp_iid_array = new ArrayList<>();
        }

        packet = Arrays.copyOfRange(packet, 0, size_iids + 1);

        size_values = Integer.parseInt(packet[0]);

        if(size_values > 0) {
            temp_value_array = new ArrayList<>(Arrays.asList(packet).subList(1, size_values + 1));
        } else {
            temp_value_array = new ArrayList<>();
        }

        packet = Arrays.copyOfRange(packet, 0, size_values + 1);

        size_errors = Integer.parseInt(packet[0]);
        if(size_values > 0) {
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
    private final List<String> listElements;

    public CustomList(int numberOfElements, List<String> listOfElements) {
        this.numberElements = numberOfElements;
        this.listElements = listOfElements;
    }

    public int getNumberElements() { return numberElements; }
    public List<String> getListElements() { return listElements; }
}

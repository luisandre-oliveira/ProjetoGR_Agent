import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Frame implements Serializable {
    private final String Tag;
    private final char Type;
    private final LocalDateTime Timestamp;
    private final int MessageIdentifier;
    private final CustomList IIDList;
    private final CustomList ValueList;
    private final CustomList ErrorList;

    public Frame(char type, LocalDateTime time, int msgid, CustomList iidlist, CustomList valuelist, CustomList errorlist) {
        this.Tag = "kdk847ufh84jg87g\0";
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

    public Frame readPDU(String packet) {
        Frame pdu;

        String[] parts = packet.split("\0"); // 0 -> 3 is TAG TO MSGID // REST ARE LISTS



        pdu = new Frame(parts[0], parts[1], parts[2], parts[3],);

        return pdu;
    }

    public CustomList readLists() {
        return new CustomList(0, new ArrayList<>());
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

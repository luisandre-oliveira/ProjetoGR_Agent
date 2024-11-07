package MIB;

public class MibObj {
    private final String Type;
    private final int Access; // define 0 as read-only and 1 as read-write
    private final String Description;
    private final String IID;
    private String Value;

    public MibObj(String type, int access, String description, String iid, String value) {
        this.Type = type;
        this.Access = access;
        this.Description = description;
        this.IID = iid;
        this.Value = value;
    }

    public String getType() { return this.Type; }
    public int getAccess() { return this.Access; }
    public String getDescription() { return this.Description; }
    public String getIID() { return this.IID; }
    public String getValue() { return this.Value; }

    public void setValue(String newValue) {
        if(this.getAccess() == 1) {
            this.Value = newValue;
        }
    }
}

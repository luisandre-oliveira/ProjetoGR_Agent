package MIB;

import java.util.HashMap;
import java.util.Map;

public class MibImp {
    private static Map<String,MibObj> MIB;
    private static MibImp instance; // Singleton design pattern implementation

    private MibImp() {
        MIB = new HashMap<>();
        MIBLazyLoader();
    }

    private void MIBLazyLoader() {
        MIB.put("3.0", new MibObj("Table", 0, "Table", "3.0", "6"));
        MIB.put("3.1.0", new MibObj("String", 0, "Tag", "3.1", "2"));
    }

    // TODO criar um método init para inicializar os valores do HASHMAP

    public static MibImp getInstance() {
        if (instance == null) {
            instance = new MibImp();  // Initialize if not yet created
        }
        return instance;    }

    public static synchronized MibObj find(String iid) {
        return MIB.get(iid);

    }

    /*public synchronized update(String s) {

    }*/
}

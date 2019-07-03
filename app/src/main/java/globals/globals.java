package globals;

import io.realm.Realm;

public class globals {

    final static String mqtt_server = "tcp://192.168.138.1:1883";
    public static String base_url="http://192.168.138.1/porchlytAPI/apiService";

    //handle realm
    public static Realm getDB()
    {
        Realm db = Realm.getDefaultInstance();
        return db;
    }

}

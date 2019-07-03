package models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class mClient extends RealmObject {
    @PrimaryKey
    public String _id = UUID.randomUUID().toString();
    public String name;
    public String surname;
    public String mobile;
    public String email;
    public String physical_address;
    public String otp;
    public boolean registered;
    public boolean synced;
    public String app_id;//the app id of this client
}

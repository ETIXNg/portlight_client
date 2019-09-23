package models;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

@Entity
@Keep
public class mClient {

    @PrimaryKey
    @NonNull
    public String _id= UUID.randomUUID().toString();
    public String name;
    public String surname;

    public String mobile;
    public String mobile_country_code = "";
    public String email;
    public String physical_address;
    public String otp;
    public boolean registered;
    public boolean synced;
    public String app_id;//the app id of this client
    public boolean enabled=true;//i this artisan enabled or disabled
}

package models;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;


//this class is for general settings for the mobile app
@Entity
@Keep
public class appSettings  {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String app_id=UUID.randomUUID().toString();
}

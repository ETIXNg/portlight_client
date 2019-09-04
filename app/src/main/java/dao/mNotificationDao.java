package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import models.mNotification;

@Dao
public interface mNotificationDao {

    @Insert
    public void insert_one(mNotification item);

    @Insert
    public void insert_many(mNotification... item);

    @Update
    public void update_one(mNotification item);

    @Update
    public void update_many(mNotification... item);


    @Delete
    public void delete_one(mNotification item);

    @Delete
    public void delete_many(mNotification... item);

    @Query("select * from mNotification")
    public List<mNotification> get_notifications();


}

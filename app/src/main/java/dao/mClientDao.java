package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import models.mClient;

@Dao
public interface mClientDao {

    @Insert
    public void insert_one(mClient client);

    @Insert
    public void insert_many(mClient... clients);

    @Update
    public void update_one(mClient client);

    @Update
    public void update_many(mClient... client);


    @Delete
    public void delete_one(mClient client);

    @Delete
    public void delete_many(mClient... client);

    @Query("select * from mClient")
    public mClient get_client();


}

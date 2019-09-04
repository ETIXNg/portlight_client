package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import models.mArtisan.Location;


@Dao
public interface LocationDao {

    @Insert
    public void insert_one(Location item);

    @Insert
    public void insert_many(Location... item);

    @Update
    public void update_one(Location item);

    @Update
    public void update_many(Location... item);


    @Delete
    public void delete_one(Location item);

    @Delete
    public void delete_many(Location... item);

    @Query("select * from Location")
    public Location get_location();


}

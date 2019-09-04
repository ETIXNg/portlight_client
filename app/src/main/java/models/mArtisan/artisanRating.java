package models.mArtisan;


import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;


@Entity
@Keep
public class artisanRating
{
    @PrimaryKey
    @SerializedName("_id")
    public String _id = UUID.randomUUID().toString();
    public int numStars;//the number of stars given;
}

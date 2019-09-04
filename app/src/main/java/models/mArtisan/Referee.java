package models.mArtisan;

import androidx.annotation.Keep;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Keep
public class Referee
{
    @PrimaryKey
    public String _id = UUID.randomUUID().toString();
    public String refname;
    public String refemail;
    public String refmobile;

    //contructor
    public Referee()
    {
        refname = "";
        refmobile = "";
        refemail = "";
    }

}

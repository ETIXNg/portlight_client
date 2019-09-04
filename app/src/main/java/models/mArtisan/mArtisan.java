package models.mArtisan;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Keep
public class mArtisan {
    @PrimaryKey
    @NonNull
    public String _id = UUID.randomUUID().toString();
    public String dateRegistered = DateTime.now().toString();//the date this artisan registered in case we will need to clear the database
    public String image = "image";
    public String name = "";
    public String surname = "";
    public String password = "";
    public String mobile = "";//this is also the primary key
    public String email = "";
    public double hourlyRate = 0.0;
    public String streetAddress = "";
    public String employmentType = EmploymentType.partTime.toString();
    public String country = "";
    public String stateOrPorvince = "";
    public String cityOrTown = "";
    public String otp;
    public String skills_="";
    @Ignore
    public List<String> skills= new ArrayList<String>();
    //public ArrayList<Referee> referees = new ArrayList<>();
    //public ArrayList<artisanRating> artisanRating = new ArrayList<>();
    public int numJobs;//the number of jobs that i have done
    public int myrating;
    public boolean registered;
    public boolean busy;//is this artisan currently busy or not, you do not get another work untill you finish your current work
    public boolean on_duty;//am i working or am i on leave? on duty
    //public Location location = new Location(0.0,0.0);
    public boolean synced;
    public String app_id;//the app id of this artisan
    public String account_bank;
    public String account_number;
    public String subaccount_id;
    public String subaccount_id_id;

}


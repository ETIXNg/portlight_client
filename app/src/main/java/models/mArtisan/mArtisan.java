package models.mArtisan;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

public class mArtisan extends RealmObject {
    @PrimaryKey
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
    public RealmList<String> skills = new RealmList<>();
    public RealmList<Referee> referees = new RealmList<>();
    public RealmList<artisanRating> artisanRating = new RealmList<>();
    public int numJobs;//the number of jobs that i have done
    public int myrating;
    public boolean registered;
    public boolean busy;//is this artisan currently busy or not, you do not get another work untill you finish your current work
    public boolean on_duty;//am i working or am i on leave? on duty
    public Location location = new Location(0.0,0.0);
    public boolean synced;
    public String app_id;//the app id of this artisan

}


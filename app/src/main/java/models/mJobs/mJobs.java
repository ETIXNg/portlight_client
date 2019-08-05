package models.mJobs;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import models.mArtisan.mArtisan;

//this is a single job per visit
//this job may have many sub tasks in the job
//this class must match with the artian app class of mjobs
public class mJobs extends RealmObject {
    @PrimaryKey
    public String _id= UUID.randomUUID().toString();
    public String _job_id;//this is commmon between both the client and the artisan
    public String artisan_app_id;//identify the artisan doing this job
    public String artisan_mobile;
    public String artisan_name;
    public String start_time;
    public String end_time;
    public String country;
    public String category;
    public String city_or_state;
    public String geoLocationLatitude;//coordinates of this job
    public String geoLocationLongitude;
    public double price;
    public String description;//any notes the artisan may want to note
    public RealmList<mTask> tasks;
    public String job_status = JobStatus.opened.toString();

    //artisans bank details
    public String account_bank;
    public String account_number;
    public String subaccount_id;
    public String subaccount_id_id;


    //contructor
    public mJobs(){

        start_time = LocalDateTime.now().toString();
        tasks= new RealmList<>();
    }



    public double getTheTotalPrice() {
        double total = 0;
        for (mTask t : this.tasks) {
            total += t.price;
        }
        return total;
    }
}

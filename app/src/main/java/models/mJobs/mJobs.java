package models.mJobs;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.sirachlabs.portchlyt_services.app;

import org.joda.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

//this is a single job per visit
//this job may have many sub tasks in the job
//this class must match with the artian app class of mjobs
@Entity
@Keep
public class mJobs {
    @PrimaryKey
    @NonNull
    public String _id= UUID.randomUUID().toString();
    public String _job_id;//this is commmon between both the client and the artisan
    public String artisan_app_id;//identify the artisan doing this job
    public String artisan_mobile;
    public String artisan_name;
    public String start_time=  LocalDateTime.now().toString();
    public String end_time;
    public String country;
    public String category;
    public String city_or_state;
    public String geoLocationLatitude;//coordinates of this job
    public String geoLocationLongitude;
    public double price;
    public String description;//any notes the artisan may want to note
    public String job_status = JobStatus.opened.toString();

    //artisans bank details
    public String account_bank;
    public String account_number;
    public String subaccount_id;
    public String subaccount_id_id;



    public double get_the_total_price() {
        double total = 0;
        List<mTask> tasks = app.db.taskDao().get_tasks(_job_id);
        for (mTask t : tasks) {
            total += t.price;
        }
        return total;
    }
}

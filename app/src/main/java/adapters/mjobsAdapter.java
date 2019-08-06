package adapters;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmRecyclerViewAdapter;
import models.mJobs.JobStatus;
import models.mJobs.mJobs;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.ViewJobActivity;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Collections;
import java.util.List;
import globals.*;


//this is just an adapter used to display the running jobs that this client has on the job fragment
public class mjobsAdapter extends RecyclerView.Adapter<mjobsAdapter.myViewHolder> {

    public static List<mJobs> jobs;
    public Activity act;

    public mjobsAdapter(Activity act) {
        Realm db = globals.getDB();
        List<mJobs>_jobs = db.where(mJobs.class).findAll();
        jobs = db.copyFromRealm(_jobs);
        Collections.reverse(jobs);
        db.close();
        this.act = act;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mjobs_row_item, parent, false);
        return new myViewHolder(itemView);
    }



    @Override
    public int getItemCount() {
        return jobs.size();
    }




    @Override
    public void onBindViewHolder(myViewHolder viewHolder, int i) {
        myViewHolder vh = (myViewHolder) viewHolder;
        mJobs job = jobs.get(i);
        DateTimeFormatter dtf = ISODateTimeFormat.localDateOptionalTimeParser();
        long time_in_millis = dtf.parseLocalDateTime(job.start_time).toDateTime().getMillis();
        vh.txt_date_time.setText( TimeAgo.using(time_in_millis) );
        vh.txt_description.setText(job.description);
        if (i % 2 == 0) {//alternate the background color
            vh.cardView.setCardBackgroundColor(act.getResources().getColor(R.color.light_grey_bg));
        } else {
            vh.cardView.setCardBackgroundColor(act.getResources().getColor(R.color.white));
        }

        //load image into imageview
        Glide   .with(act)
                .load(globals.base_url+"/fetch_artisan_profile_picture?artisan_app_id="+job.artisan_app_id)
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .placeholder(R.drawable.ic_worker)
                .into(vh.img_artisan_icon);



        //open the activty to view the job on  clicking this item
        vh.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vj = new Intent(act, ViewJobActivity.class);
                vj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                vj.putExtra("_job_id",job._job_id);//using the primary key
                act.startActivity(vj);
            }
        });

        //indicate that this job is completed
        if(job.job_status.equals(JobStatus.closed.toString()))
        {
            vh.img_status.setImageResource((R.drawable.ic_verified_user_black_24dp));
        }

        if (job.job_status.equals(JobStatus.cancelled.toString())) {
            vh.img_status.setImageResource((R.drawable.ic_cancel_black_24dp));
        }

        if (job.job_status.equals(JobStatus.disputed.toString())) {
            vh.img_status.setImageResource((R.drawable.ic_announcement_black_24dp));
        }
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_date_time;
        public TextView txt_description;
        public ImageView img_artisan_icon;
        public CardView cardView;
        ImageView img_status;

        public myViewHolder(View itemView) {
            super(itemView);
            txt_date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
            txt_description = (TextView) itemView.findViewById(R.id.txt_description);
            img_artisan_icon = (ImageView) itemView.findViewById(R.id.img_artisan_icon);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            img_status = (ImageView) itemView.findViewById(R.id.img_status);
        }

    }


}

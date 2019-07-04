package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.ViewJobActivity;
import com.samaritan.portchlyt_services.app;

import org.w3c.dom.Text;

import java.util.List;

import globals.globals;
import io.realm.Realm;
import models.mJobs.mJobs;
import models.mJobs.mTask;

//this adapter is used by the view jobs activity
public class mTasksAdapter extends RecyclerView.Adapter<mTasksAdapter.myViewHolder> {

    mJobs job;
    Context act;
    public mTasksAdapter(String _job_id)
    {
        Realm db = globals.getDB();
        job      = db.copyFromRealm(   db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst()  );
        db.close();
        this.act= app.ctx;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.m_task_item, parent, false);
        return new mTasksAdapter.myViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        mTasksAdapter.myViewHolder vh = (mTasksAdapter.myViewHolder) holder;
        mTask task = job.tasks.get(position);
        vh.txt_task_description.setText(task.description);
        vh.txt_task_price.setText( globals.formatCurrency( task.price ) );

        //highlight the background
        if(position%2==0)
        {
            vh.linlay.setBackgroundColor(act.getResources().getColor(R.color.light_grey_bg));
        }
        else
        {
            vh.linlay.setBackgroundColor(act.getResources().getColor(R.color.white));
        }


    }

    @Override
    public int getItemCount() {
        return job.tasks.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_task_description;
        public TextView txt_task_price;
        public LinearLayout linlay;
        public myViewHolder(View view)
        {
            super(view);
            linlay= (LinearLayout)view.findViewById(R.id.linlay);
            txt_task_description=(TextView)view.findViewById(R.id.txt_task_description);
            txt_task_price=(TextView)view.findViewById(R.id.txt_task_price);
        }
    }
}

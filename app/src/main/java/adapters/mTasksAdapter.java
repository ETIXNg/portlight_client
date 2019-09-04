package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sirachlabs.portchlyt_services.R;
import com.sirachlabs.portchlyt_services.app;

import java.util.List;

import globals.globals;
import models.mJobs.mJobs;
import models.mJobs.mTask;

//this adapter is used by the view jobs activity
public class mTasksAdapter extends RecyclerView.Adapter<mTasksAdapter.myViewHolder> {

    mJobs job;
    Context act;
    List<mTask> tasks;
    public mTasksAdapter(String _job_id)
    {
        job = app.db.mJobsDao().get_job(_job_id);
        tasks=app.db.taskDao().get_tasks(_job_id);
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
        mTask task = tasks.get(position);
        vh.txt_task_description.setText(task.description);
        vh.txt_task_price.setText( globals.formatCurrencyPlain( task.price ) );

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
        return tasks.size();
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

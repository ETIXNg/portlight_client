package adapters;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samaritan.portchlyt_services.R;

import java.util.ArrayList;

//this adapter is for the add jobs activity
public class mAddJobsAdapter extends BaseAdapter {
    ArrayList<String> jobs;
    Activity act;

    public mAddJobsAdapter(ArrayList<String> j, Activity a) {
        super();
        jobs = j;
        act = a;
    }

    @Override
    public int getCount() {
        if (jobs != null) return jobs.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return jobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_job_activity_item_row, parent, false);
        TextView txt_job_cat = (TextView) view.findViewById(R.id.txt_job_cat);
        ImageView img_delete = (ImageView) view.findViewById(R.id.img_delete);
        CardView laylin = (CardView) view.findViewById(R.id.linlay);
        txt_job_cat.setText(jobs.get(position));
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobs.remove(position);
                //notify dataset changed
            }
        });

        if (position % 2 == 0) {//alternate the background color
            //laylin.setCardBackgroundColor(act.getResources().getColor(R.color.primary));
        } else {
            laylin.setCardBackgroundColor(act.getResources().getColor(R.color.primary_light));
        }

        return view;
    }
}




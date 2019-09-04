package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.sirachlabs.portchlyt_services.R;
import com.sirachlabs.portchlyt_services.app;

import java.util.List;

import MainActivityTabs.ListOfArtisansFragment;
import globals.globals;
import models.ListOfArtisansModel;

public class mList_of_Artisans_Adapter extends RecyclerView.Adapter<mList_of_Artisans_Adapter.myHolder> {

    private Activity activity_context;
    List<ListOfArtisansModel> artisans;


    public mList_of_Artisans_Adapter(Activity activity_context, List<ListOfArtisansModel> artisans) {
        this.artisans = artisans;
        this.activity_context = activity_context;
    }//.constructor




    @Override
    public int getItemViewType(int position) {
        return artisans.get(position) != null ? 1 : 0;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        myHolder viewHolder = null;
        if(viewType == 1){
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artisan_profile_item, parent, false);
            viewHolder = new myHolder(layoutView);
        }else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(layoutView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

        ListOfArtisansModel artisan = artisans.get(position);

        if(holder instanceof myHolder){

            holder.ratingBar.setRating(artisan.rating);
            holder.txt_artisan_name.setText(artisan.name);
            holder.txt_num_jobs.setText(artisan.num_of_jobs + " " + activity_context.getResources().getString(R.string.jobs));
            holder.txt_skills.setText(artisan.skills);
            holder.txt_hourly_rate.setText( globals.formatCurrency( artisan.hourly_rate ) + activity_context.getString(R.string.per_hr));
            holder.txt_mobile.setText( artisan.mobile );

            if(position%2==0)
            {
                holder.lin_lay.setBackgroundColor(app.ctx.getResources().getColor(R.color.light_grey_bg) );
            }
            else {
                holder.lin_lay.setBackgroundColor(app.ctx.getResources().getColor(R.color.white) );
            }

            //load image into imageview
            Glide.with(activity_context)
                    .load(globals.base_url + "/fetch_artisan_profile_picture?artisan_app_id=" + artisan.artisan_app_id)
                    .centerCrop()
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    //.skipMemoryCache(true)
                    .placeholder(R.drawable.ic_worker)
                    .into(holder.img_profile);



        }else{
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }


        //long press to open this option
        holder.lin_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListOfArtisansFragment.show_artisan_contact_dialog(artisan);
            }
        });


    }


    @Override
    public int getItemCount() {
        if(artisans==null)return 0;
        return artisans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class myHolder extends RecyclerView.ViewHolder {
        public ImageView img_profile;
        public TextView txt_artisan_name;
        public TextView txt_skills;
        public SimpleRatingBar ratingBar;
        public TextView txt_num_jobs;
        public TextView txt_hourly_rate;
        public LinearLayout lin_lay;
        public TextView txt_mobile;


        public myHolder(View v) {
            super(v);
            img_profile = (ImageView) v.findViewById(R.id.img_profile);
            txt_artisan_name = (TextView) v.findViewById(R.id.txt_artisan_name);
            txt_hourly_rate = (TextView) v.findViewById(R.id.txt_hourly_rate);
            txt_skills = (TextView) v.findViewById(R.id.txt_skills);
            lin_lay = (LinearLayout) v.findViewById(R.id.lin_lay);
            ratingBar = (SimpleRatingBar) v.findViewById(R.id.ratingBar);
            txt_num_jobs = (TextView) v.findViewById(R.id.txt_num_jobs);
            txt_mobile = (TextView) v.findViewById(R.id.txt_mobile);

        }
    }//.class


    public class ProgressViewHolder extends myHolder{
        public ProgressBar progressBar;
        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }//.

}//.adapter

package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sirachlabs.portchlyt_services.R;
import com.sirachlabs.portchlyt_services.ViewJobActivity;

import java.util.List;

import globals.globals;
import models.mArtisan.mArtisan;

///adapter for the artisan that have been found and displayed on the home/search screen
public class foundArtisansAdapter extends RecyclerView.Adapter<foundArtisansAdapter.MyHolder> {

    List<mArtisan> artisans;
    List<Integer> artisans_rating;
    Context ctx;
    String tag="foundArtisansAdapter";

    public foundArtisansAdapter(Context ctx, List<mArtisan> artisans,List<Integer>artisans_rating) {
        this.artisans = artisans;
        this.artisans_rating = artisans_rating;
        this.ctx = ctx;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artisan_found_list_item,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        mArtisan artisan = artisans.get(position);
        int artisan_rating = artisans_rating.get(position);
        holder.lbl_artisan_skills.setText(artisan.skills_);
        holder.txt_artisan_name.setText(artisan.name);
        holder.txt_artisan_mobile.setText(artisan.mobile);
        holder.ratingBar.setRating(artisan_rating);
        //set the image
        //load image into imageview
        Glide   .with(ctx)
                .load(globals.base_url+"/fetch_artisan_profile_picture?artisan_app_id="+artisan.app_id)
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .placeholder(R.drawable.ic_worker)
                .into(holder.img_artisan_image);

        //todo open the viewjob when artian image is clicked
        //open the job when an artisan if clicked
        holder.linlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vj =  new Intent(ctx, ViewJobActivity.class);
               // vj.putExtra("_job_id",);
            }
        });

    }



    @Override
    public int getItemCount() {
        return artisans.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        public ImageView img_artisan_image;
        public TextView txt_artisan_name;
        public TextView txt_artisan_mobile;
        public TextView lbl_artisan_skills;
        public RatingBar ratingBar;
        LinearLayout linlay;

        public MyHolder(View v) {
            super(v);
            img_artisan_image = (ImageView) v.findViewById(R.id.img_artisan_image);
            txt_artisan_name = (TextView) v.findViewById(R.id.txt_artisan_name);
            txt_artisan_mobile = (TextView) v.findViewById(R.id.txt_artisan_mobile);
            lbl_artisan_skills = (TextView) v.findViewById(R.id.lbl_artisan_skills);
            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
            linlay = (LinearLayout) v.findViewById(R.id.linlay);
        }


    }


}

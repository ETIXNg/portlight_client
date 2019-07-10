package adapters.ListOfArtisansPager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.app;

import java.util.List;

import MainActivityTabs.ListOfArtisansFragment;
import globals.globals;
import models.ListOfArtisansModel;

public class mList_of_Artisans_Adapter extends RecyclerView.Adapter<mList_of_Artisans_Adapter.myHolder> {

    private Activity activity_context;
    List<ListOfArtisansModel> artisans;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    private OnLoadMoreListener onLoadMoreListener;


    public mList_of_Artisans_Adapter(Activity activity_context, List<ListOfArtisansModel> artisans,RecyclerView recyclerView) {
        this.artisans = artisans;
        this.activity_context = activity_context;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if(!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                        if(onLoadMoreListener != null){
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }//.if
    }//.constructor




    @Override
    public int getItemViewType(int position) {
        return artisans.get(position) != null ? 1 : 0;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artisan_profile_item, parent, false);
            //return new myHolder(itemView);

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
            holder.txt_hourly_rate.setText( globals.formatCurrency( artisan.hourly_rate ));
            holder.txt_mobile.setText( artisan.mobile );

            if(position%2==0)
            {
                holder.lin_lay.setBackgroundColor(app.ctx.getResources().getColor(R.color.light_grey_bg) );
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
        holder.lin_lay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                ListOfArtisansFragment.show_artisan_contact_dialog(artisan);
                return false;
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

    public void setLoad(){
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public void setLoaded() {
        loading = false;
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


    public class ProgressViewHolder extends mList_of_Artisans_Adapter.myHolder{
        public ProgressBar progressBar;
        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }//.

}//.adapter

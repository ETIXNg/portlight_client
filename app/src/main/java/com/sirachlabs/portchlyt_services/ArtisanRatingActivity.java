package com.sirachlabs.portchlyt_services;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.jackandphantom.circularimageview.CircleImage;
import com.koushikdutta.ion.Ion;

import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import MainActivityTabs.SearchServicesFragment;
import globals.globals;
import models.mJobs.JobStatus;
import models.mJobs.mJobs;


//this activity is a dialog
public class ArtisanRatingActivity extends AppCompatActivity {

    String tag = "ArtianRatingActivity.java";

    SimpleRatingBar mrating_1;
    SimpleRatingBar mrating_2;
    SimpleRatingBar mrating_3;
    SimpleRatingBar mrating_4;
    SimpleRatingBar over_all_rating;
    CircleImage img_profile;
    ScrollView linlay;

    String _job_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artian_rating);

        _job_id = getIntent().getStringExtra("_job_id");

        //update this job since the job is complete
        mJobs job=app.db.mJobsDao().get_job(_job_id);
        job.job_status= JobStatus.closed.toString();
        if(job.end_time==null)
        {
            job.end_time = LocalDateTime.now().toString();;
        }
        //update job
        app.db.mJobsDao().update_one(job);

        mrating_1 = (SimpleRatingBar) findViewById(R.id.mrating_1);
        mrating_2 = (SimpleRatingBar) findViewById(R.id.mrating_2);
        mrating_3 = (SimpleRatingBar) findViewById(R.id.mrating_3);
        mrating_4 = (SimpleRatingBar) findViewById(R.id.mrating_4);
        over_all_rating = (SimpleRatingBar) findViewById(R.id.over_all_rating);
        img_profile = (CircleImage) findViewById(R.id.img_profile);
        linlay = (ScrollView) findViewById(R.id.linlay);

        mrating_1.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                compute_artisan_over_all_rating();
            }
        });


        mrating_2.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                compute_artisan_over_all_rating();
            }
        });

        mrating_3.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                compute_artisan_over_all_rating();
            }
        });

        mrating_4.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                compute_artisan_over_all_rating();
            }
        });

        //
        try {
            Glide
                    .with(ArtisanRatingActivity.this)
                    .load(globals.base_url + "/fetch_artisan_profile_picture?artisan_app_id=" + job.artisan_app_id)
                    .centerCrop()
                    .placeholder(R.drawable.ic_worker)
                    .into(img_profile);
        } catch (Exception x) {
            Log.e(tag, x.getMessage());
        }
        //compute first time
        compute_artisan_over_all_rating();

    }

    //get the overall rating
    private int compute_artisan_over_all_rating() {
        try {
            int fiveRatings = 0;
            int fourRatings = 0;
            int threeRatings = 0;
            int twoRatings = 0;
            int oneRatings = 0;

            //
            if (mrating_1.getRating() == 5) fiveRatings++;
            if (mrating_2.getRating() == 5) fiveRatings++;
            if (mrating_3.getRating() == 5) fiveRatings++;
            if (mrating_4.getRating() == 5) fiveRatings++;

            //
            if (mrating_1.getRating() == 4) fourRatings++;
            if (mrating_2.getRating() == 4) fourRatings++;
            if (mrating_3.getRating() == 4) fourRatings++;
            if (mrating_4.getRating() == 4) fourRatings++;

            //
            //
            if (mrating_1.getRating() == 3) threeRatings++;
            if (mrating_2.getRating() == 3) threeRatings++;
            if (mrating_3.getRating() == 3) threeRatings++;
            if (mrating_4.getRating() == 3) threeRatings++;

            //
            if (mrating_1.getRating() == 2) twoRatings++;
            if (mrating_2.getRating() == 2) twoRatings++;
            if (mrating_3.getRating() == 2) twoRatings++;
            if (mrating_4.getRating() == 2) twoRatings++;

            //
            if (mrating_1.getRating() == 1) oneRatings++;
            if (mrating_2.getRating() == 1) oneRatings++;
            if (mrating_3.getRating() == 1) oneRatings++;
            if (mrating_4.getRating() == 1) oneRatings++;


            int rating = (
                    5 * fiveRatings +
                            4 * fourRatings +
                            3 * threeRatings +
                            2 * twoRatings +
                            1 * oneRatings
            ) /
                    (
                            fiveRatings + fourRatings + threeRatings + twoRatings + oneRatings
                    );
            over_all_rating.setRating(rating);

            return rating;
        } catch (Exception ex) {
            over_all_rating.setRating(0);
            return 0;
        }
    }


    public void exec_complete_rating(View v) {
        try {

            mJobs m = app.db.mJobsDao().get_job(_job_id);
            ProgressDialog pd = new ProgressDialog(ArtisanRatingActivity.this);
            pd.setMessage(getString(R.string.please_wait));
            pd.show();
            Ion.with(ArtisanRatingActivity.this)
                    .load(globals.base_url + "/add_artisan_rating")
                    .setBodyParameter("artisan_app_id", m.artisan_app_id)
                    .setBodyParameter("rating", compute_artisan_over_all_rating() + "")
                    .asString()
                    .setCallback((e, result) -> {
                        pd.dismiss();
                        if (e == null) {
                            try {
                                JSONObject json = new JSONObject(result);
                                String res = json.getString("res");
                                String msg = json.getString("msg");
                                if (res.equals("ok")) {
                                    Toast.makeText(ArtisanRatingActivity.this, getString(R.string.thank_you_for_your_feedback), Toast.LENGTH_SHORT).show();
                                    SearchServicesFragment.rel_cancel_request.callOnClick();//clears the search so it can start again
                                    finish();
                                } else {
                                    Log.e(tag, msg);
                                    Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                Log.e(tag, ex.getMessage());
                                Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();
                        }

                    });
        } catch (Exception ex) {
            Log.e(tag, ex.getMessage());
        }
    }


}

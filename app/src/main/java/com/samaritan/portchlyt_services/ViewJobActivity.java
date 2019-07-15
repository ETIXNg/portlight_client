package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import adapters.mTasksAdapter;
import io.realm.Realm;
import models.mClient;
import models.mJobs.JobStatus;
import models.mJobs.mJobs;
import globals.*;

public class ViewJobActivity extends AppCompatActivity {
    Toolbar mtoolbar;

    static String _job_id;
    static TextView lbl_total_price;
    static TextView txt_total_time;
    static TextView txt_start_time;
    static TextView txt_end_time;
    static TextView txt_service_type;
    static TextView txt_client_mobile;
    static RecyclerView list_tasks;
    static LinearLayout tbl_lay;
    static LinearLayout content_view;

    //buttons
    BootstrapButton btn_open_dispute;
    BootstrapButton btn_cash_payment;
    BootstrapButton btn_card_payment;


    //
    static String tag = "ViewJobActivity";


    public static int request_code_for_cancel_job=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);
        content_view = (LinearLayout) findViewById(R.id.content_view);
        _job_id = getIntent().getStringExtra("_job_id");


        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.job_details));


        //
        lbl_total_price = (TextView) findViewById(R.id.lbl_total_price);
        txt_total_time = (TextView) findViewById(R.id.txt_total_time);
        txt_start_time = (TextView) findViewById(R.id.txt_start_time);
        txt_end_time = (TextView) findViewById(R.id.txt_end_time);
        txt_service_type = (TextView) findViewById(R.id.txt_service_type);
        txt_client_mobile = (TextView) findViewById(R.id.txt_client_mobile);

        list_tasks = (RecyclerView) findViewById(R.id.list_tasks);


        tbl_lay = (LinearLayout) findViewById(R.id.tbl_lay);

        //buttons
        btn_open_dispute = (BootstrapButton) findViewById(R.id.btn_open_dispute);
        btn_cash_payment = (BootstrapButton) findViewById(R.id.btn_cash_payment);
        btn_card_payment = (BootstrapButton) findViewById(R.id.btn_card_payment);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list_tasks.setLayoutManager(layoutManager);

        //
        getTheJob();

        //show that this job is currently closed
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();


        if (job.job_status.equals(JobStatus.closed.toString())) {
            Snackbar.make(content_view, getString(R.string.this_job_is_closed), Snackbar.LENGTH_INDEFINITE).show();
            btn_card_payment.setVisibility(View.GONE);//hide these two
            btn_cash_payment.setVisibility(View.GONE);
        }

        if (job.job_status.equals(JobStatus.cancelled.toString())) {
            Snackbar.make(content_view, getString(R.string.this_job_was_cancelled), Snackbar.LENGTH_INDEFINITE).show();
            btn_card_payment.setVisibility(View.GONE);//hide these two
            btn_cash_payment.setVisibility(View.GONE);
            btn_open_dispute.setVisibility(View.GONE);
        }

        String artisan_app_id = job.artisan_app_id;
        db.close();


        //load image into view
        //load artisan image
        ImageView img_profile = (ImageView) findViewById(R.id.img_profile);
        Glide.with(this)
                .load(globals.base_url + "/fetch_artisan_profile_picture?artisan_app_id=" + artisan_app_id)
                .centerCrop()
                .placeholder(R.drawable.ic_worker)
                .into(img_profile);
        //
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vp = new Intent(ViewJobActivity.this, ViewArtisanProfilePictureActivity.class);
                vp.putExtra("artisan_app_id", artisan_app_id);
                startActivity(vp);
            }
        });


    }//oncreate


    //get the job from the database and display it
    public static void getTheJob() {
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        try {

            DateTimeFormatter dtf = ISODateTimeFormat.localDateOptionalTimeParser();
            DateTimeFormatter dtf2 = DateTimeFormat.forPattern("d MMM,yyyy HH:mm");
            lbl_total_price.setText(app.ctx.getString(R.string.total_price) + ": " + job.getTheTotalPrice() + "");

            txt_start_time.setText(dtf2.print(dtf.parseLocalDateTime(job.start_time)));
            if (job.end_time != null && !job.end_time.equals("")) {
                txt_end_time.setText(dtf2.print(dtf.parseLocalDateTime(job.end_time)));
            }
            set_the_total_time();//set the time in a pretty format
            txt_service_type.setText(job.description);
            txt_client_mobile.setText(job.artisan_mobile);

            //set the bills
            mTasksAdapter tasks_adapter;
            tasks_adapter = new mTasksAdapter(_job_id);//this automatically pulls the tasks of this job
            list_tasks.setAdapter(tasks_adapter);


        } catch (Exception ex) {
            Log.e(tag, ex.getLocalizedMessage());
        } finally {
            db.close();
        }
    }

    public static void setTotalPrice() {
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        lbl_total_price.setText(app.ctx.getString(R.string.total_price) + ": " + globals.formatCurrency(job.getTheTotalPrice()));
        if (job.getTheTotalPrice() > 0) {
            tbl_lay.setVisibility(View.VISIBLE);
        } else {
            tbl_lay.setVisibility(View.INVISIBLE);
        }
        db.close();
    }

    //set the total time of this job if running or complete
    public static void set_the_total_time() {
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        DateTimeFormatter dtf = ISODateTimeFormat.localDateOptionalTimeParser();
        LocalDateTime start_time = dtf.parseLocalDateTime(job.start_time);
        LocalDateTime end_time;

        if (job.end_time != null) {
            end_time = dtf.parseLocalDateTime(job.end_time);
        } else {
            end_time = LocalDateTime.now();
        }

        Period p = new Period(start_time, end_time);
        int days = p.getDays();
        int hours = p.getHours();
        int mins = p.getMinutes();
        txt_total_time.setText(
                app.ctx.getString(R.string.total_time) + "\n" + days + " " + app.ctx.getString(R.string.days)
                        + " " + hours + " " + app.ctx.getString(R.string.hrs)
                        + " " + mins + " " + app.ctx.getString(R.string.mins)
        );
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);//hide this keyboard
        setTotalPrice();
    }

    @Override
    protected void onStart() {
        super.onStart();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);//hide this keyboard
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.m_cancel:
                Intent cancel = new Intent(ViewJobActivity.this, CancelJobActivity.class);
                Realm db = globals.getDB();
                mClient client = db.where(mClient.class).findFirst();
                mJobs job  = db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst();
                cancel.putExtra("_job_id", _job_id);
                cancel.putExtra("artisan_app_id",job.artisan_app_id );
                cancel.putExtra("client_app_id", client.app_id);
                db.close();
                startActivityForResult(cancel,request_code_for_cancel_job);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst();
        if( job.job_status.equals(JobStatus.closed.toString()) || job.job_status.equals(JobStatus.cancelled.toString())) {
        }
        else
        {
            getMenuInflater().inflate(R.menu.view_job_detail_menu, menu);
        }
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    //
    public void openCashPaymentActivity(View v) {
        if (!check_this_job_has_bills()) {
            Snackbar.make(content_view, getString(R.string.this_job_has_no_bills_yet), Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent cp = new Intent(ViewJobActivity.this, CashPaymentActivity.class);
        cp.putExtra("_job_id", _job_id);
        startActivity(cp);
    }


    private boolean check_this_job_has_bills() {
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        if (job.tasks.size() == 0) {
            db.close();
            return false;
        }
        db.close();
        return true;
    }


    //open the dispute activity
    public void open_dispute_activity(View v) {
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        Intent dis = new Intent(ViewJobActivity.this, DisputeActivity.class);
        dis.putExtra("_job_id", _job_id);
        dis.putExtra("artisan_app_id", job.artisan_app_id);
        db.close();
        startActivity(dis);
        finish();

    }


    //open make the card payment
    public void open_make_card_payment(View v) {
        if (!check_this_job_has_bills()) {
            Snackbar.make(content_view, getString(R.string.this_job_has_no_bills_yet), Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent cp = new Intent(ViewJobActivity.this, CardPaymentActivity.class);
        cp.putExtra("_job_id", _job_id);
        startActivity(cp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == request_code_for_cancel_job) {
            finish();
            if (resultCode == RESULT_OK) {
                finish();//finish this activity too
            }
        }
    }


}//clas

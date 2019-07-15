package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.material.snackbar.Snackbar;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import globals.globals;
import io.realm.Realm;
import models.mArtisan.mArtisan;
import models.mJobs.mJobs;

public class CancelJobActivity extends AppCompatActivity {


    String tag = "CancelJobActivity";
    Toolbar mtoolbar;
    String _job_id;
    String client_app_id;
    String artisan_app_id;
    RadioButton rd_1, rd_2, rd_3;
    View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_job);
        contextView = (LinearLayout) findViewById(R.id.contextView);


        _job_id = getIntent().getStringExtra("_job_id");
        client_app_id = getIntent().getStringExtra("client_app_id");
        artisan_app_id = getIntent().getStringExtra("artisan_app_id");

        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.job_details));

        rd_1 = (RadioButton) findViewById(R.id.rd_1);
        rd_2 = (RadioButton) findViewById(R.id.rd_2);
        rd_3 = (RadioButton) findViewById(R.id.rd_3);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        if (job.end_time == null) {
            getMenuInflater().inflate(R.menu.view_job_detail_menu, menu);
        }//only show when job i still pending completion
        return true;
    }


    //why you cancelled
    public void submit_reason_for_cancelling(View v) {
        String reason = "";
        if (rd_1.isChecked()) reason = "took_too_long_to_arrive";
        if (rd_2.isChecked()) reason = "bad_service";
        if (rd_3.isChecked()) reason = "too_expensive";

        ProgressDialog pd = new ProgressDialog(CancelJobActivity.this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        Ion.with(CancelJobActivity.this)
                .load(globals.base_url + "/client_cancel_job")
                .setBodyParameter("_job_id", _job_id)
                .setBodyParameter("reason", reason)
                .setBodyParameter("artisan_app_id", artisan_app_id)
                .setBodyParameter("client_app_id", client_app_id)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pd.dismiss();
                        if (e != null) {
                            Snackbar.make(contextView, R.string.error_occured, Snackbar.LENGTH_SHORT).show();
                            Log.e(tag, e + " line 269");
                        } else {
                            Snackbar.make(contextView, R.string.error_occured, Snackbar.LENGTH_SHORT).show();
                            finish();//close this activity now since we have recorded this at the server
                        }
                    }
                });
    }

}

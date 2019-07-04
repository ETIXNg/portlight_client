package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jackandphantom.circularimageview.CircleImage;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;


import globals.*;
import io.realm.Realm;
import models.mJobs.mJobs;

public class DisputeActivity extends AppCompatActivity {

    EditText txt_other_reason;
    RadioButton rd_1;
    RadioButton rd_2;
    RadioButton rd_3;

    LinearLayout linlay;
    Toolbar mtoolbar;

    String _job_id;
    String artisan_app_id;
    CircleImage img_profile;
    TextInputLayout txt_label;


    String tag = "DisputeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);
        //
        txt_other_reason = (EditText) findViewById(R.id.txt_other_reason);
        txt_label = (TextInputLayout) findViewById(R.id.txt_label);
        //
        txt_label.setVisibility(View.GONE);

        //
        _job_id = getIntent().getStringExtra("_job_id");
        artisan_app_id = getIntent().getStringExtra("artisan_app_id");

        //
        rd_1 = (RadioButton) findViewById(R.id.rd_1);
        rd_2 = (RadioButton) findViewById(R.id.rd_2);
        rd_3 = (RadioButton) findViewById(R.id.rd_3);
        img_profile = (CircleImage) findViewById(R.id.img_profile);
        try {

            Glide.with(DisputeActivity.this)
                    .load(globals.base_url+"/fetch_artisan_profile_picture?artisan_app_id="+artisan_app_id)
                    //.centerCrop()
                    .placeholder(R.drawable.ic_worker)
                    //.dontAnimate()
                    .into(img_profile);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        linlay = (LinearLayout) findViewById(R.id.linlay);

        rd_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_hide_other_option();
            }
        });
        rd_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_hide_other_option();
            }
        });
        rd_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_hide_other_option();
            }
        });


        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.open_a_dispute));

        //load image into image view using glide


    }

    private void show_hide_other_option() {
        if (rd_3.isChecked()) {
            txt_label.setVisibility(View.VISIBLE);
        } else {
            txt_label.setVisibility(View.GONE);
        }
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


    //execute open dispute
    public void execute_open_dispute(View v) {
        String reason_for_dispute = "";
        if (rd_1.isChecked()) {
            reason_for_dispute = "pricing_too_expensive";
        }
        if (rd_2.isChecked()) {
            reason_for_dispute = "job_not_done_well";
        }
        if (rd_3.isChecked()) {
            reason_for_dispute = txt_other_reason.getText().toString();
        }
        if (reason_for_dispute.equals("")) {
            Snackbar.make(linlay, getString(R.string.indicate_a_reason_for_the_dispute), Snackbar.LENGTH_SHORT).show();
            return;
        }


        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        ProgressDialog pd = new ProgressDialog(DisputeActivity.this);
        pd.setMessage(getString(R.string.please_wait));
        pd.show();
        Ion.with(DisputeActivity.this)
                .load(globals.base_url + "/open_dispute")
                .setBodyParameter("_job_id", _job_id)
                .setBodyParameter("reason_for_dispute", reason_for_dispute)
                .setBodyParameter("artisan_app_id", job.artisan_app_id)
                .asString()
                .setCallback((e, result) -> {
                    pd.dismiss();
                    db.close();
                    if (e != null) {
                        Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();
                        return;
                    } else {
                        try {
                            JSONObject json = new JSONObject(result);
                            String res = json.getString("res");
                            String msg = json.getString("msg");
                            if (res.equals("ok")) {
                                Intent dispute_notification = new Intent(DisputeActivity.this, DisputeNotificationActivity.class);
                                startActivity(dispute_notification);
                            } else {
                                Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();

                            }
                        } catch (Exception ex) {
                            Log.e(tag, ex.getMessage());
                            Snackbar.make(linlay, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}

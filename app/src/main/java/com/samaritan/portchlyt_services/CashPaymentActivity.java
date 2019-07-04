package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONObject;

import globals.globals;
import globals.*;
import io.realm.Realm;
import models.mClient;
import models.mJobs.mJobs;



public class CashPaymentActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    static String _job_id;
    ImageView img_profile;
    TextView txt_total_amount;
    TextView txt_artisan_name;
    LinearLayout linlay;
    String tag="CashPayment.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);
        //
        _job_id = getIntent().getStringExtra("_job_id");
        img_profile = (ImageView)findViewById(R.id.img_profile);
        txt_total_amount = (TextView) findViewById(R.id.txt_total_amount);
        txt_artisan_name = (TextView) findViewById(R.id.txt_artisan_name);

        linlay=(LinearLayout)findViewById(R.id.linlay);


        //
        Realm db=globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst();


        txt_total_amount.setText( globals.formatCurrency( job.getTheTotalPrice() ) );
        txt_artisan_name.setText(job.artisan_name + " "+ job.artisan_mobile);

        //load image into imageview
        Glide.with(this)
                .load(globals.base_url+"/fetch_artisan_profile_picture?artisan_app_id="+job.artisan_app_id)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_worker)
                .into(img_profile);

        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.cash_payment));
        db.close();

    }//.create


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
        //getMenuInflater().inflate(R.menu.view_job_menu, menu);
        return true;
    }


    public void execute_make_cash_payment(View v)
    {
        //confirm with client one more time
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // execute it
                        Realm db=globals.getDB();
                        mJobs job=db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst();
                        mClient client = db.where(mClient.class).findFirst();
                        ProgressDialog pd=new ProgressDialog(CashPaymentActivity.this);
                        pd.setMessage(getString(R.string.please_wait));
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        Ion.with(CashPaymentActivity.this)
                                .load(globals.base_url+"/make_payment_for_artisan")
                                .setBodyParameter("_job_id", job._job_id)
                                .setBodyParameter("client_app_id", client.app_id)
                                .setBodyParameter("artisan_app_id",job.artisan_app_id )
                                .setBodyParameter("amount_payed",job.getTheTotalPrice()+"" )
                                .setBodyParameter("payment_type","cash" )
                                .asString()
                                .setCallback((e, result) -> {
                                    pd.dismiss();
                                    db.close();
                                    try
                                    {
                                        JSONObject json= new JSONObject(result);
                                        String res=json.getString("res");
                                        String msg=json.getString("msg");
                                        if(res.equals("ok"))
                                        {
                                            //
                                            Toast.makeText(CashPaymentActivity.this,getString(R.string.payment_recieved),Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else
                                        {
                                            Log.e(tag,"line 135 "+msg);
                                            Snackbar.make(linlay,getString(R.string.error_occured),Snackbar.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception ex){
                                        Snackbar.make(linlay,getString(R.string.error_occured),Snackbar.LENGTH_SHORT).show();
                                    }
                                    finally {
                                    }
                                });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //leave it
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(CashPaymentActivity.this);
        builder.setMessage(getString(R.string.are_you_sure)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }



}//.class

package com.sirachlabs.portchlyt_services;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import MainActivityTabs.SearchServicesFragment;
import globals.globals;
import models.mClient;

//this class simply shows the action to take in the event that no artisan ws found
public class NoArtisansFoundActivity extends AppCompatActivity {

    String request_id;
    String services;
    RadioButton rd_1;
    RadioButton rd_2;
    String tag="nafa";
    TextView txt_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //close the rippleBackground disk since only one request can be made at a time
        SearchServicesFragment.rippleBackground.stopRippleAnimation();


        setContentView(R.layout.activity_no_artisans_found);
        txt_services=(TextView)findViewById(R.id.txt_services);
        request_id=getIntent().getStringExtra("request_id");
        services=getIntent().getStringExtra("data");
        txt_services.setText(services);
        rd_1=(RadioButton)findViewById(R.id.rd_1);
        rd_2=(RadioButton)findViewById(R.id.rd_2);

        //play the notification tone
        MediaPlayer mp= MediaPlayer.create(this,R.raw.unconvinced);
        mp.start();
    }

    //submit the action since no artisans found
    public void SubmitAction(View v)
    {


        mClient client= app.db.mClientDao().get_client();
        String sdata="";
        String action_to_take="";
        if(rd_1.isChecked())action_to_take="notify_me_when_artisan_is_available";
        if(rd_2.isChecked())action_to_take="ignore";
        JSONObject json= new JSONObject();
        try
        {
            json.put("request_id",request_id);
            json.put("client_app_id",client.app_id);
            json.put("action_to_take",action_to_take);
            sdata = json.toString();

        }catch (Exception ex)
        {
            Log.e(tag,"line 55 "+ex.getLocalizedMessage());
            Toast.makeText(NoArtisansFoundActivity.this,getString(R.string.error_occured),Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(NoArtisansFoundActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.please_wait));
        pd.show();

        Ion.with(NoArtisansFoundActivity.this)
                .load(globals.base_url+"/NoArtisanFoundActionClientToTake")
                .setBodyParameter("data", sdata)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pd.dismiss();
                        if(e==null)
                        {
                            Toast.makeText(NoArtisansFoundActivity.this,getString(R.string.saved),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(NoArtisansFoundActivity.this,getString(R.string.error_occured),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }
}

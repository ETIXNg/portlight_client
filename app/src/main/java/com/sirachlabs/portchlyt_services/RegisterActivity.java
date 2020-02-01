package com.sirachlabs.portchlyt_services;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rilixtech.CountryCodePicker;

import org.json.JSONObject;
import org.w3c.dom.Text;

import globals.globals;
import models.appSettings;
import models.mArtisan.mLocation;
import models.mClient;

public class RegisterActivity extends AppCompatActivity {
    ProgressDialog pd;
    EditText txt_mobile;
    Toolbar mtoolbar;
    String tag="RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txt_mobile = (EditText) findViewById(R.id.txt_mobile);
        pd = new ProgressDialog(this);
        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.registration));
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

    public void Register(View v) {


        //clear previous data
        app.db.clearAllTables();

        //seed any db item here

        //create and insert appSettings
        appSettings aps = new appSettings();
        app.db.appSettingsDao().insert(aps);

        //create and insert the location table
        mLocation loc = new mLocation();
        loc.last_known_location = getString(R.string.we_need_your_location_to_provide_you_this_service);
        app.db.LocationDao().insert_one(loc);


        String mobile = txt_mobile.getText().toString();
        if (mobile.equals("")) {
            txt_mobile.setError(getResources().getString(R.string.cannot_be_blank));
            return;
        }

        mClient client = new mClient();
        client.mobile = mobile;
        client.app_id = aps.app_id;
        app.db.mClientDao().insert_one(client);

        String sdata = "";//data of the artisan to be posted
        try {
            sdata = new Gson().toJson((client));
        } catch (Exception ex) {
            Log.e("d", ex.getMessage());
        }

        try {
            pd.setMessage(getString(R.string.registering_please_wait));
            pd.show();
            Ion.with(RegisterActivity.this)
                    .load(globals.base_url + "/clientRegistration")
                    .setBodyParameter("data", sdata)//add the client as a parameter
                    .asString()
                    .setCallback((e, result) -> {
                        //Log.e(tag,sdata);
                            pd.hide();
                            if(e!=null){
                                Toast.makeText(RegisterActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(result==null)return;
                                try {
                                    if (result == null) return;//return if the result is null
                                    String res = "";
                                    String msg = "";
                                    String otp = "";
                                    String app_id = "";
                                    try {
                                        res = new JSONObject(result).getString("res");
                                    } catch (Exception ex) {
                                    }
                                    try {
                                        msg = new JSONObject(result).getString("msg");
                                    } catch (Exception ex) {
                                    }
                                    try {
                                        otp = new JSONObject(result).getString("otp");
                                    } catch (Exception ex) {

                                    }
                                    try {
                                        app_id = new JSONObject(result).getString("app_id");
                                    } catch (Exception ex) {

                                    }

                                    if (res.equals("ok")) {
                                        client.otp = otp;
                                        if(!TextUtils.isEmpty(app_id)) {//it may be null if the client is a new one
                                            client.app_id = app_id;//maintain the old app id
                                        }
                                        client.registered=true;
                                        //update the client
                                        app.db.mClientDao().update_one(client);
                                        Intent main = new Intent(RegisterActivity.this, MainActivity.class);
                                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        main.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(main);
                                    } else {
                                        //show the err measage
                                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception ex) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                    Log.d("d", ex.getMessage() + " line 161");
                                }
                    });
        } catch (Exception ex) {
            Log.e("d", ex.getMessage());
            Toast.makeText(RegisterActivity.this, "line 163 "+ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

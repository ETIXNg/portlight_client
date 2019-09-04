package com.sirachlabs.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.rilixtech.CountryCodePicker;

import org.json.JSONObject;

import globals.globals;
import models.mClient;


//this is not in use since we are not changing the mobile number
public class SettingsActivity extends AppCompatActivity {

    String tag="SettingsActivity";
    CountryCodePicker ccp;
    BootstrapEditText txt_mobile;
    Toolbar mtoolbar;
    LinearLayout context_layout;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.save_settings:
                save_settings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        txt_mobile = (BootstrapEditText) findViewById(R.id.txt_mobile);
        context_layout = (LinearLayout) findViewById(R.id.context_layout);
        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.settings));

    }


    public void save_settings()
    {
        ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        mClient client =  app.db.mClientDao().get_client();
        String sdata = new Gson().toJson(client);
        Ion.with(SettingsActivity.this)
                .load(globals.base_url+"/save_client_settings")
                .setBodyParameter("data",sdata)
                .asString()
                .setCallback((e, result) -> {

                    pd.hide();

                    if(e!=null)
                    {
                        Snackbar.make(context_layout,getString(R.string.error_occured),Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        JSONObject json = new JSONObject(result);
                        String res=  json.getString("res");
                        if(res.equals("ok"))
                        {
                            //update client
                            client.mobile_country_code = ccp.getSelectedCountryCodeWithPlus();
                            client.mobile = txt_mobile.getText().toString();
                            app.db.mClientDao().update_one(client);
                            Snackbar.make(context_layout, getString(R.string.saved), Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String error_msg = json.getString("msg");
                            Snackbar.make(context_layout, error_msg, Snackbar.LENGTH_SHORT).show();
                        }

                    }catch (Exception ex)
                    {
                        Log.e(tag,ex.getMessage());
                        Snackbar.make(context_layout, getString(R.string.error_occured), Snackbar.LENGTH_SHORT).show();
                    }



                });
    }


}

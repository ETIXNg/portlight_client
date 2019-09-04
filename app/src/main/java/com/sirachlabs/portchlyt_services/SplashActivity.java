package com.sirachlabs.portchlyt_services;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import models.mClient;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //check if this user is already registered yes or not
        mClient m = app.db.mClientDao().get_client();
        if (m != null)//this means the user is most probably registerd already
        {
            if (m.registered) {
                //since already registered we can skip this
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //main.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(main);
                finish();//clear this activity
            }
        }//else just continue and allow the user to register
    }

    public void Register(View v) {
        startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        Log.e("d", "clicked");
    }

    public void Signin(View v) {


    }
}

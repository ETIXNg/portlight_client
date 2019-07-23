package com.samaritan.portchlyt_services;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import io.realm.Realm;
import models.mClient;

public class SplashActivity extends AppCompatActivity {

    Realm db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = Realm.getDefaultInstance();
        //check if this user is already registered yes or not
        mClient m = db.where(mClient.class).findFirst();
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

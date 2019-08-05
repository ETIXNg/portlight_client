package com.samaritan.portchlyt_services;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.beardedhen.androidbootstrap.TypefaceProvider;

//import net.danlew.android.joda.JodaTimeAndroid;

import net.danlew.android.joda.JodaTimeAndroid;

import de.jonasrottmann.realmbrowser.RealmBrowser;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import models.appSettings;

public class app extends MultiDexApplication {

    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        Realm.init(this);
        MultiDex.install(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("porchlyt_services.realm")
                .schemaVersion(14)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);



        TypefaceProvider.registerDefaultIconSets();
        ctx = getApplicationContext();


        //create and insert the appsettings
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        appSettings s = new appSettings();
        db.insertOrUpdate(s);
        db.commitTransaction();
        db.close();

        //show the realm database
        //RealmBrowser.startRealmModelsActivity(this, realmConfig);

        //delete all realm data
        //Realm.deleteRealm(realmConfig);


    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

package com.sirachlabs.portchlyt_services;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import dao.LocationDao;
import dao.appSettingsDao;
import dao.mArtisanDao;
import dao.mClientDao;
import dao.mJobsDao;
import dao.mNotificationDao;
import dao.mTaskDao;
import models.appSettings;
import models.mArtisan.mLocation;
import models.mArtisan.mArtisan;
import models.mClient;
import models.mJobs.mJobs;
import models.mJobs.mTask;
import models.mNotification;

@Database(version = 11, entities = {
        mTask.class,
        mNotification.class,
        mClient.class,
        mArtisan.class,
        mJobs.class,
        appSettings.class,
        mLocation.class
        })

public abstract class AppDatabase extends RoomDatabase {
    abstract public mTaskDao taskDao();
    abstract public appSettingsDao appSettingsDao();
    abstract public LocationDao LocationDao();
    abstract public mArtisanDao mArtisanDao();
    abstract public mClientDao mClientDao();
    abstract public mJobsDao mJobsDao();
    abstract public mNotificationDao mNotificationDao();
}

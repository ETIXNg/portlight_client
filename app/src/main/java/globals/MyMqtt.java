package globals;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.samaritan.portchlyt_services.ArtisanRatingActivity;
import com.samaritan.portchlyt_services.NoArtisansFoundActivity;
import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.ViewJobActivity;
import com.samaritan.portchlyt_services.app;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;


import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import MainActivityTabs.JobsFragment;
import MainActivityTabs.SearchServicesFragment;
import io.realm.Realm;
import io.realm.RealmList;
import models.appSettings;
import models.mArtisan.mArtisan;
import models.mClient;
import models.mJobs.mJobs;
import models.mJobs.mTask;


//todo convert this class to a service so it can run async in the background always
public class MyMqtt {
    public static MqttAndroidClient mqttClient;
    public static Context ctx;
    static String clientId = "";//this is the client id for this specific device, this is the maintain the correct messages
    final String username = "uiemrvua";
    final String password = "cB51Tz-tin54";

    public static String tag = "mqtt";
    public static String mqtt_server = "porchlyt_mqtt_server";


    //init the mqtt service
    public static void init(Context context) {
        ctx = context;

        //get the correct client id for this specific djaevice
        Realm db = Realm.getDefaultInstance();
        mClient client = db.where(mClient.class).findFirst();
        appSettings aps = db.where(appSettings.class).findFirst();
        clientId = client.app_id;//use this topic for real time comms with the client app
        db.close();
        mqttClient = new MqttAndroidClient(app.ctx, globals.mqtt_server, clientId);


        mqttClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                subscribeToTopic(clientId, 0);//this subscription is for the communication between the server and this client use a different topic for those messages wich do not require realtime, since this one requires real time it must subscribe to qos 0
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.e(tag, "connection lost: " + throwable.getLocalizedMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //Log.e(tag, mqttMessage.toString() + " the message line 65");
                ///Toast.makeText(ctx,mqttMessage.toString(),Toast.LENGTH_SHORT).show();
                JSONObject json = null;
                String type = "";
                try {
                    json = new JSONObject(mqttMessage.toString());
                    type = json.getString("type");
                } catch (Exception ex) {
                    Log.e(tag, "line 71 " + ex.getMessage());
                    return;
                }

                //route the message to the correct handler


                if (type.equals("cash_payment_accepted_by_artisan")) {
                    //close this job
                    try {
                        String _job_id = json.getString("_job_id");
                        Realm db2 = globals.getDB();
                        mJobs job = db2.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
                        db2.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (job.end_time == null) {//make sure we dont change the date again
                                    job.end_time = LocalDateTime.now().toString();//set the end time
                                    JobsFragment.refreshJobsAdapter();
                                    Intent rating = new Intent(app.ctx, ArtisanRatingActivity.class);
                                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    rating.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    rating.putExtra("_job_id", _job_id);
                                    app.ctx.startActivity(rating);
                                }
                            }
                        });
                        db2.close();
                        Toast.makeText(app.ctx, app.ctx.getString(R.string.payment_recieved), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Log.e(tag,ex.getMessage());
                        Toast.makeText(app.ctx,ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                if (type.equals("client_job_bill_notification")) {

                    try {
                        String data = json.getString("data");
                        Realm db = Realm.getDefaultInstance();
                        mJobs job_from_artisan = new Gson().fromJson(data, mJobs.class);
                        mJobs job_from_db = db.where(mJobs.class).equalTo("_job_id", job_from_artisan._job_id).findFirst();
                        db.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                //clear from my list
                                job_from_db.tasks.clear();
                                //first delete all the existing ones
                                for (mTask t : job_from_db.tasks) {
                                    t.deleteFromRealm();///delete from realm all the old tasks
                                }
                                job_from_db.tasks.deleteAllFromRealm();
                                for (mTask t : job_from_artisan.tasks) {
                                    job_from_db.tasks.add(t);
                                }
                            }
                        });
                        db.close();
                        ViewJobActivity.getTheJob();
                        Toast.makeText(app.ctx, app.ctx.getString(R.string.bill_updated), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Log.e(tag, ex.getMessage());//only do the update provided this item is not null
                    }
                }

                if (type.equals("artisan_search_request_notification")) {
                    try {
                        String msg = json.getString("msg");
                        if (msg.equals("no_artisans_found")) {
                            Intent request = new Intent(app.ctx, NoArtisansFoundActivity.class);
                            request.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            request.putExtra("request_id", json.getString("request_id"));
                            request.putExtra("data", json.getString("data"));
                            app.ctx.startActivity(request);
                        }
                        if (msg.equals("search_is_complete")) {
                            //tell the client that his search is completed no more jobs remaining
                            SearchServicesFragment.SearchIsCompleted();
                        }

                        if (msg.equals("job_accepted")) {
                            String jobs_accepted = json.getString("jobs_accepted");
                            String job_data = json.getString("job_data");
                            mArtisan artisan = new Gson().fromJson(json.getString("artisan_json_data"), mArtisan.class);
                            int artisan_rating = (int)json.getDouble("artisan_rating");
                            SearchServicesFragment.DisplayAnArtisanThumbNail(artisan,artisan_rating);//show this thumbnail with the correct jobs

                            //also save this job to the db since it has been accepted by the artisan
                            //now save this job in the jobs place and open its activity straight away
                            Realm db = globals.getDB();
                            db.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        JSONObject job_json = new JSONObject(job_data);
                                        mJobs job = new mJobs();
                                        job.artisan_app_id = job_json.getString("artisan_app_id");
                                        job._job_id = job_json.getString("_job_id");//set the job id
                                        job.artisan_mobile = job_json.getString("artisan_mobile");
                                        job.geoLocationLatitude = job_json.getString("latitude");
                                        job.geoLocationLongitude = job_json.getString("longitude");
                                        job.start_time = LocalDateTime.now().toString();
                                        job.artisan_name = job_json.getString("artisan_name");//add the name of the artisan
                                        job.description = job_json.getString("requested_skills");//any notes the artian may want to note but initially indicate the skills
                                        db.insert(job);//save this request
                                        JobsFragment.refreshJobsAdapter();//display the job item in the jobs fragment
                                    } catch (Exception ex) {
                                        Log.e(tag, ex.getMessage());
                                    }
                                }
                            });
                            db.close();
                            //refresh the adapter
                            JobsFragment.refreshJobsAdapter();
                        }


                    } catch (Exception ex) {
                        Log.e(tag, "mqtt line 68 " + ex.getLocalizedMessage());
                    }
                }//.request_task_notification


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.e(tag, "message delivered");
            }
        });
        connect();//attempt to connect as soon as its created
    }


    public static void connect() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setMaxInflight(1);
        //mqttConnectOptions.setUserName(username);
        //mqttConnectOptions.setPassword(password.toCharArray());

        try {
            mqttClient.connect(mqttConnectOptions, app.ctx, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttClient.setBufferOpts(disconnectedBufferOptions);
                    Log.e(tag, "connection successfull");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w(tag, "Failed to connect to: " + globals.mqtt_server + " " + exception.toString());
                }
            });


        } catch (MqttException ex) {
            Log.e(tag, "line 83 " + ex.getMessage());
        }
    }


    //send a string message to a specific topic
    public static boolean publishStringMessage(String message, String topic) {
        try {
            MqttMessage m = new MqttMessage();
            m.setPayload(message.getBytes());
            m.setQos(1);
            m.setRetained(true);
            mqttClient.publish(topic, m);
            return true;
        } catch (Exception ex) {
            Log.e(tag, ex.getMessage());
            return false;
        }
    }


    public static void subscribeToTopic(final String topic, int qos) {
        try {
            mqttClient.subscribe(topic, qos, app.ctx, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(tag, "Subscribed! to " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(tag, "Subscribed fail! to " + topic);
                }

            });


        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }
}
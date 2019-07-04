package MainActivityTabs;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.SelectJobsActivity;
import com.samaritan.portchlyt_services.app;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import javax.xml.transform.Result;

import adapters.foundArtisansAdapter;
import globals.globals;
import io.realm.Realm;
import models.appSettings;
import models.mArtisan.mArtisan;
import models.mClient;

//todo ensure google play services is up to date and working
public class SearchServicesFragment extends Fragment implements OnMapReadyCallback {

    ImageView img_search; //center image
    public static RippleBackground rippleBackground;//the riple background animation
    public static ArrayList<String> jobsList;
    //
    static RelativeLayout relLay1, relLay2;
    static ConstraintLayout topView;
    //
    static Context ctx;
    public static MediaPlayer mp;

    //
    public static double wayLatitude = 0.0;//the coords of the person
    public static double wayLongitude = 0.0;//these can be computed at run time and are not stored, they can be changes by the user when he requests to chnage the location


    TextView txt_location;

    MapView mMapView;
    private GoogleMap googleMap;

    public static List<mArtisan> found_artisans;//the list of all the artisans found in this search
    static LinearLayout rel_results;

    String my_address = "";


    static View view;
    //location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    final int LOCATION_REQUEST_CODE = 1;//this it needed for the request permision response
    public static boolean useNewLocation;//set to true if the user chooses to use a different location
    public static boolean LocationEnabled;//is the location enabled yes/no

    static String tag = "SearchServicesFragment";

    Looper _looper;

    public SearchServicesFragment() {
    }

    public static SearchServicesFragment newInstance(String param1, String param2) {
        SearchServicesFragment fragment = new SearchServicesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobsList = new ArrayList<String>();
        ctx = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //
        view = inflater.inflate(R.layout.fragment_search_services, container, false);
        topView = (ConstraintLayout) view.findViewById(R.id.topView);

        //
        rel_results = (LinearLayout) view.findViewById(R.id.rel_results);
        rel_results.setVisibility(View.GONE);//initially nt visible
        relLay1 = (RelativeLayout) view.findViewById(R.id.relLay1);
        relLay2 = (RelativeLayout) view.findViewById(R.id.relLay2);
        relLay2.setVisibility(View.GONE);
        topView.invalidate();//refresh

        //
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//display map imediatly


        //
        rippleBackground = (RippleBackground) view.findViewById(R.id.rippleBG);
        img_search = (ImageView) view.findViewById(R.id.img_search);
        txt_location = (TextView) view.findViewById(R.id.txt_location);


        //
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(-34, 151);
                //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        //
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        //
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first check permision
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //permission granted
                    startActivity(new Intent(getActivity(), SelectJobsActivity.class));
                }
            }//.onclick
        });//img_search onclick


        //init location listners
        //todo change the request updates time to something more than 1 second
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1*1000);
        locationRequest.setFastestInterval(1*1000);


       init_location_listner();
        //
        relLay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                Log.e("l", "requested permission ");
                init_location_listner();
            }
        });

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //permission granted
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //function to do the web service call to search for the artisan
    public static void execSearchForAtisan() {
        //dont search unles you have the coordinates
        if (wayLatitude == 0 || wayLongitude == 0) {
            Snackbar.make(topView, ctx.getString(R.string.enable_location_settings_first), Snackbar.LENGTH_SHORT).show();
            relLay2.setVisibility(View.VISIBLE);
            topView.invalidate();
            return;
        }

        //
        found_artisans = new ArrayList<mArtisan>();///init this prior to each search begins
        rel_results.setVisibility(View.GONE);//hide it
        topView.invalidate();

        //
        if (jobsList.size() == 0) {
            Toast.makeText(ctx, ctx.getString(R.string.no_jobs_selected), Toast.LENGTH_SHORT).show();//leave as toast
            return;
        } else {
            JSONArray ja = new JSONArray();
            for (String string : jobsList) {
                ja.put(string);
            }
            String sdata = "";
            try {
                Realm db = globals.getDB();
                mClient client = db.where(mClient.class).findFirst();//there is only one client per phone
                appSettings aps = db.where(appSettings.class).findFirst();
                JSONObject json = new JSONObject();
                json.put("lat", wayLatitude);
                json.put("lon", wayLongitude);
                json.put("request_id", UUID.randomUUID().toString());
                json.put("app_id", client.app_id);
                json.put("services", ja.toString());
                sdata = json.toString();
                db.close();

                //
                rippleBackground.startRippleAnimation();

                //also play the sound to show the search is on going
                mp = MediaPlayer.create(ctx, R.raw.unsure);
                mp.setLooping(true);
                mp.start();

                //
                Ion.with(ctx)
                        .load(globals.base_url + "/findServiceArtisan")
                        .setBodyParameter("data", sdata)
                        .asString()
                        .withResponse()
                        .setCallback((e, result) -> {
                            if (e != null)//there was an error
                            {
                                mp.stop();//stop the media player
                                rippleBackground.stopRippleAnimation();
                                Toast.makeText(ctx, ctx.getString(R.string.error_occured_try_again), Toast.LENGTH_SHORT).show();
                                Log.e(tag, "line 427 " + e.getMessage());
                                return;//there was an error let it die
                            }
                        });
            } catch (Exception ex) {
                mp.stop();
                rippleBackground.stopRippleAnimation();
                Log.e(tag,"line 335 "+ex.getMessage());
                Snackbar.make(topView, ctx.getString(R.string.error_occured), Snackbar.LENGTH_LONG).show();
            }

        }//else
    }//execSearch


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mp != null && mp.isPlaying()) {
            mp.stop();//stop playing the music
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    //this is to show the client that his search is completed
    public static void SearchIsCompleted() {
        mp.stop();//stop the sound
        rippleBackground.stopRippleAnimation();//stop the animation
        //clear the jobs list
        jobsList.clear();
    }

    //this method is to display a popup with the artisans location and job
    public static void DisplayAnArtisanThumbNail(mArtisan artisan) {
        //stop the sound as soon as at least one artisan is found
        mp.stop();

        rel_results.setVisibility(View.VISIBLE);//show it
        RecyclerView lst_artisans_results = (RecyclerView) view.findViewById(R.id.lst_artisans_results);
        found_artisans.add(artisan);

        //indicate the numeber of artisans found
        TextView txt_results = (TextView) view.findViewById(R.id.txt_results);
        txt_results.setText(found_artisans.size() + " " + ctx.getString(R.string.artisans_found_nearby));


        foundArtisansAdapter fa_adapter = new foundArtisansAdapter(ctx, found_artisans);
        lst_artisans_results.setAdapter(fa_adapter);
    }


    private void init_map() {
        // For dropping a marker at a point on the Map
        LatLng my_position = new LatLng((wayLatitude), (wayLongitude));
        //set the map location
        CameraPosition cameraPosition = new CameraPosition.Builder().target(my_position).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void hide_rel_lay_2() {
        relLay2.setVisibility(View.GONE);
        topView.invalidate();
    }

    private void show_rel_lay_2() {
        relLay2.setVisibility(View.VISIBLE);
        topView.invalidate();
    }


    private void set_address(double lat, double lng) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(wayLatitude, wayLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    if (city.equals(null)) city = "";
                    if (state.equals(null)) state = "";
                    if (country.equals(null)) country = "";
                    if (knownName.equals(null))
                        knownName = "";//this to ensure that we dont pull null values in the address
                    my_address = country + ", " + city + ", " + state + ", " + knownName;

                } catch (Exception ex) {
                    Log.e(tag, "line 456 " + ex.getMessage());
                }

                //this will now run on the ui thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt_location.setText(my_address);//set the address
                    }
                });



            }

        });
    }//.set_address



    //
    @SuppressLint("MissingPermission")
    private void init_location_listner()
    {
        //
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    //Log.e("l","location result=null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        //
                        set_address(wayLatitude, wayLongitude);
                        init_map();//move map camera to the correct location
                        //Log.e("l",wayLatitude +" " +wayLongitude);
                    }
                }
            }
        };//location callback

        //
        if(_looper==null) {

            //Looper.prepare();
            _looper = Looper.myLooper();
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,_looper);

    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //this is for fine location
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                hide_rel_lay_2();
                init_location_listner();
            } else {
                show_rel_lay_2();
            }
        }
    }//.onrequest results


}//fragment

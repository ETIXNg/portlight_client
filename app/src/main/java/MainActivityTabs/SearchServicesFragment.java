package MainActivityTabs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.koushikdutta.ion.Ion;
import com.sirachlabs.portchlyt_services.R;
import com.sirachlabs.portchlyt_services.SettingsActivity;
import com.sirachlabs.portchlyt_services.app;
import com.skyfishjy.library.RippleBackground;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import adapters.foundArtisansAdapter;
import adapters.skillsAdapter;
import globals.globals;
import models.appSettings;
import models.mArtisan.mArtisan;
import models.mClient;

//todo ensure google play services is up to date and working
public class SearchServicesFragment extends Fragment implements OnMapReadyCallback {

    public static RippleBackground rippleBackground;//the riple background animation
    public static ArrayList<String> jobsList;
    //
    static ListView lst_skills;
    static skillsAdapter adp;

    //
    static RelativeLayout relLay1, relLay2;
    static LinearLayout rel_enabled;
    static RelativeLayout topView;
    RelativeLayout rel_cancel_request;
    LinearLayout settings_layout;
    //
    static Context ctx;
    public static MediaPlayer mp;

    //
    public static double wayLatitude = 0.0;//the coords of the person
    public static double wayLongitude = 0.0;//these can be computed at run time and are not stored, they can be changes by the user when he requests to chnage the location


    TextView txt_location;

    MapView mMapView;
    private static GoogleMap googleMap;
    static HashMap<String, Marker> map_artisans;//the markers for the map

    public static List<mArtisan> found_artisans;//the list of all the artisans found in this search
    public static List<Integer> found_artisans_rating;//the list of the ratings of the artisans
    static LinearLayout rel_results;

    String my_address = "";

    static Activity activity_context;

    boolean is_searching;//is searching or not


    static View view;
    //location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    final int LOCATION_REQUEST_CODE = 1;//this it needed for the request permision response
    public static boolean useNewLocation;//set to true if the user chooses to use a different location
    public static boolean LocationEnabled;//is the location enabled yes/no

    public static SlidingUpPanelLayout sliding_layout;

    static String tag = "SearchServicesFragment";


    //Button
    Button btn_find_now;

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
        topView = (RelativeLayout) view.findViewById(R.id.topView);
        rel_cancel_request = (RelativeLayout) view.findViewById(R.id.rel_cancel_request);
        rel_cancel_request.setVisibility(View.GONE);
        sliding_layout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        //
        rel_results = (LinearLayout) view.findViewById(R.id.rel_results);
        rel_enabled = (LinearLayout) view.findViewById(R.id.rel_enabled);
        settings_layout = (LinearLayout) view.findViewById(R.id.settings_layout);

        settings_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings  = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(settings);
            }
        });

        rel_results.setVisibility(View.GONE);//initially nt visible
        relLay1 = (RelativeLayout) view.findViewById(R.id.relLay1);
        relLay2 = (RelativeLayout) view.findViewById(R.id.relLay2);
        relLay2.setVisibility(View.GONE);
        topView.invalidate();//refresh
        map_artisans = new HashMap<>();

        activity_context = getActivity();

        btn_find_now = (Button) view.findViewById(R.id.btn_find_now);
        btn_find_now.setOnClickListener(new View.OnClickListener() {
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
                    execSearchForAtisan();
                }


            }
        });

        //
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//display map imediatly


        //
        rippleBackground = (RippleBackground) view.findViewById(R.id.rippleBG);
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

        //set up the grid
        try {
            lst_skills = (ListView) view.findViewById(R.id.lst_skills);
            String[] skills = getResources().getStringArray(R.array.job_categories);
            adp = new skillsAdapter(skills);
            lst_skills.setAdapter(adp);
            adp.notifyDataSetChanged();
        } catch (Exception ex) {
            Log.e("d", ex.getMessage());
        }


        //is this client enabled or not
        show_hide_enabled();

        //init location listners
        //todo change the request updates time to something more than 1 second
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(10 * 1000);


        //first time run... check permision
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //permission granted
            init_location_listner();
        }
        //
        relLay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //first check permision
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    //permission granted
                    init_location_listner();
                }
            }
        });

        //cancel the request
        rel_cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchIsCompleted();//closed the media player and also the ripple bg
                rel_results.setVisibility(View.GONE);
                is_searching = false;
                rel_cancel_request.setVisibility(View.GONE);
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
    public void execSearchForAtisan() {

        if (is_searching) {
            Toast.makeText(getActivity(), getString(R.string.already_searching), Toast.LENGTH_SHORT).show();
            return;//dont search again since already searching
        }

        if (!globals.is_client_enabled()) {
            Toast.makeText(
                    activity_context
                    , activity_context.getString(R.string.your_temporarily_banned_from_using_this_service_as_you_may_have_violated_our_terms_of_service)
                    , Toast.LENGTH_LONG).show();

            return;
        }


        //dont search unles you have the coordinates
        if (wayLatitude == 0 || wayLongitude == 0) {
            Snackbar.make(topView, ctx.getString(R.string.enable_location_settings_first), Snackbar.LENGTH_SHORT).show();
            relLay2.setVisibility(View.VISIBLE);
            topView.invalidate();
            return;
        }

        //
        found_artisans = new ArrayList<mArtisan>();///init this prior to each search begins
        found_artisans_rating = new ArrayList<Integer>();///init this prior to each search begins

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

                mClient client = app.db.mClientDao().get_client();
                appSettings aps = app.db.appSettingsDao().get_app_settings();
                JSONObject json = new JSONObject();
                json.put("lat", wayLatitude);
                json.put("lon", wayLongitude);
                json.put("request_id", UUID.randomUUID().toString());
                json.put("app_id", client.app_id);
                json.put("services", ja.toString());
                sdata = json.toString();

                //
                rippleBackground.startRippleAnimation();
                sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);//collapse the panel

                //also play the sound to show the search is on going
                mp = MediaPlayer.create(ctx, R.raw.unsure);
                mp.setLooping(true);
                mp.start();

                rel_cancel_request.setVisibility(View.VISIBLE);
                //
                is_searching = true;
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
                                Log.e(tag, "line 427 " + e);
                                return;//there was an error let it die
                            }
                        });
            } catch (Exception ex) {
                mp.stop();
                rel_cancel_request.setVisibility(View.GONE);
                rippleBackground.stopRippleAnimation();
                Log.e(tag, "line 335 " + ex.getMessage());
                Snackbar.make(topView, ctx.getString(R.string.error_occured), Snackbar.LENGTH_LONG).show();
                is_searching = false;
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
        //clear all the jobs check list to get ready for the next search
        clear_all_selected_jobs();
    }

    //this method is to display a popup with the artisans location and job
    public static void DisplayAnArtisanThumbNail(mArtisan artisan, int artisan_rating) {
        //stop the sound as soon as at least one artisan is found
        mp.stop();

        rel_results.setVisibility(View.VISIBLE);//show it
        RecyclerView lst_artisans_results = (RecyclerView) view.findViewById(R.id.lst_artisans_results);
        LinearLayoutManager lm = new LinearLayoutManager(activity_context, RecyclerView.HORIZONTAL, false);
        lst_artisans_results.setLayoutManager(lm);
        found_artisans.add(artisan);
        found_artisans_rating.add(artisan_rating);

        //indicate the numeber of artisans found
        TextView txt_results = (TextView) view.findViewById(R.id.txt_results);
        txt_results.setText(found_artisans.size() + " " + ctx.getString(R.string.artisans_found_nearby));


        foundArtisansAdapter fa_adapter = new foundArtisansAdapter(ctx, found_artisans, found_artisans_rating);
        fa_adapter.setHasStableIds(true);
        lst_artisans_results.setAdapter(fa_adapter);
        fa_adapter.notifyDataSetChanged();

        update_artisan_on_map_change_icon_to_selected(artisan);//display the selected artisan
        rippleBackground.stopRippleAnimation();//stop at the first artisan found

    }


    private static void clear_all_selected_jobs() {
        String[] skills = activity_context.getResources().getStringArray(R.array.job_categories);
        adp = new skillsAdapter(skills);
        lst_skills.setAdapter(adp);
        adp.notifyDataSetChanged();
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
                try {
                    my_address = getString(R.string.unknown_location);
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
                    if (my_address.equals("") || my_address.equals(" ") || TextUtils.isEmpty(my_address))
                        my_address = getString(R.string.unknown_location);

                } catch (Exception ex) {

                    Log.e(tag, "line 456 " + ex.getMessage());
                }

                try {
                    //this will now run on the ui thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                txt_location.setText(my_address);//set the address
                            } catch (Exception ex) {
                                Log.e(tag, "line 521 " + ex.getMessage());
                            }
                        }
                    });
                } catch (Exception ex) {

                }


            }

        });
    }//.set_address


    //
    @SuppressLint("MissingPermission")
    private void init_location_listner() {
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
        if (_looper == null) {

            //Looper.prepare();
            _looper = Looper.myLooper();
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, _looper);

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


    //remove the selected icon to show a now regular icon, when job is completed,cancelled, or closed
    public static void remove_selected_artisan_icon(String artisan_app_id) {
        Marker marker = map_artisans.get(artisan_app_id);//get the specific marker
        marker.remove();//remove from map
        map_artisans.remove(artisan_app_id);//remove from list

    }

    //this will change the icon color to show the selected guy
    public static void update_artisan_on_map_change_icon_to_selected(mArtisan artisan) {


        //replace marker
        if (map_artisans.containsKey(artisan.app_id)) {
            Marker marker = map_artisans.get(artisan.app_id);//get the specific marker
            marker.remove();//remove from map
            map_artisans.remove(artisan.app_id);//remove from list

            //add a new marker with the new icon
            marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                    .title(artisan.skills_)
                    //.snippet(skill)
                    .rotation((float) 3.5)
                    .icon(bitmapDescriptorFromVector(activity_context, R.drawable.ic_map_worker_icon_selected)));


            map_artisans.put(artisan.app_id, marker);//add back to list

        }


    }


    //set or update the artisan pointer on the screen
    public static void update_artisan_on_map(String artisan_app_id, String artisan_lat, String artisan_lng, String skill) {


        double latitude = Double.parseDouble(artisan_lat);
        double longitude = Double.parseDouble(artisan_lng);


        //update marker position if already present
        if (map_artisans.containsKey(artisan_app_id)) {
            Marker marker = map_artisans.get(artisan_app_id);
            marker.setPosition(new LatLng(latitude, longitude));
        }
        //else add it
        else {

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(skill)
                    //.snippet(skill)
                    .rotation((float) 3.5)
                    .icon(bitmapDescriptorFromVector(activity_context, R.drawable.ic_map_worker_icon)));


            map_artisans.put(artisan_app_id, marker);

        }


    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        try {

            //original height of the marker
            //vectorDrawable.getIntrinsicWidth(),
            //vectorDrawable.getIntrinsicHeight()

            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, 55, 55);
            Bitmap bitmap = Bitmap.createBitmap(55, 55, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception ex) {
            Toast.makeText(activity_context, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //show or hide the enabled
    public static void show_hide_enabled() {
        mClient client = app.db.mClientDao().get_client();
        if (!client.enabled) {
            rel_enabled.setVisibility(View.VISIBLE);
            MediaPlayer mp = MediaPlayer.create(activity_context, R.raw.plucky);
            mp.start();//add sound for the notification
        } else {
            rel_enabled.setVisibility(View.GONE);
        }
    }


}//fragment

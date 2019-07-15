package MainActivityTabs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.app;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


import adapters.ListOfArtisansPager.mList_of_Artisans_Adapter;
import models.ListOfArtisansModel;
import globals.*;

public class ListOfArtisansFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    SlidingPaneLayout sliding_panel;

    Spinner spinner_city;
    Spinner spinner_skill;
    RecyclerView recyclerView;
    mList_of_Artisans_Adapter artisans_adapter;
    static int page = 1;
    ImageView img_search;
    ProgressBar progress_bar;

    static Activity activity_context;
    RelativeLayout lin_bottom;
    Button btn_refresh_artisans;
    ImageView img_close_pane;


    private static final String tag = "ListOfArtisansFragment";
    private List<ListOfArtisansModel> artisans_list_data;
    private Handler handler;

    public ListOfArtisansFragment() {
    }

    public static ListOfArtisansFragment newInstance(String param1, String param2) {
        ListOfArtisansFragment fragment = new ListOfArtisansFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_of_artisans, container, false);
        spinner_city = (Spinner) v.findViewById(R.id.spinner_city);
        spinner_skill = (Spinner) v.findViewById(R.id.spinner_skill);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        artisans_list_data = new ArrayList<>();
        activity_context = getActivity();
        lin_bottom  = (RelativeLayout)v.findViewById(R.id.lin_bottom);
        lin_bottom .setVisibility(View.GONE);



        //
        sliding_panel=(SlidingPaneLayout)v.findViewById(R.id.sliding_panel);
        img_close_pane = (ImageView)v.findViewById(R.id.img_close_pane);
        img_close_pane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the panel
                sliding_panel.closePane();
            }
        });

        //
        progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        //
        img_search = (ImageView) v.findViewById(R.id.img_search);
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open the pane
                sliding_panel.openPane();

            }
        });

        //
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);


        //
        btn_refresh_artisans = (Button)v.findViewById(R.id.btn_refresh_artisans);
        btn_refresh_artisans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset the page to 1 and clear the current list
                page = 1;
                artisans_list_data.clear();
                sliding_panel.closePane();
                get_more_data();
            }
        });

        //
        get_more_data();
        artisans_adapter = new mList_of_Artisans_Adapter(getActivity(), artisans_list_data, recyclerView);
        artisans_adapter.setHasStableIds(true);
        recyclerView.setAdapter(artisans_adapter);

        artisans_adapter.setOnLoadMoreListener(new mList_of_Artisans_Adapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                artisans_list_data.add(null);
                artisans_adapter.notifyItemInserted(artisans_list_data.size() - 1);

                artisans_list_data.remove(artisans_list_data.size() - 1);//remove the progress dialog item
                get_more_data();//fetch more data
            }
        });


        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //obsolete method
    private void populate_spinner_city() {
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Abuja FCT");
        spinnerArray.add("Port Harcourt");
        spinnerArray.add("Lagos");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(adapter);
    }


    //only get more data and increment the page
    private void get_more_data() {



        progress_bar.setVisibility(View.VISIBLE);
        String city = spinner_city.getSelectedItem().toString();
        String skill = spinner_skill.getSelectedItem().toString();

        Ion.with(getActivity())
                .load(globals.base_url + "/fetch_the_artisans")
                .setBodyParameter("page", page + "")
                .setBodyParameter("city", city + "")
                .setBodyParameter("skill", skill + "")
                .asString()
                .withResponse()
                .setCallback((e, result) -> {

                    //delay dismissing the progress bar for a second
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress_bar.setVisibility(View.GONE);
                        }
                    }, 1000);


                    if (e == null) {
                        if (result == null) {
                            Log.e(tag, "line 136 result is null");
                            return;
                        } else {
                            Log.e(tag, "result " + result.getResult());
                            try {

                                artisans_adapter.notifyItemRemoved(artisans_list_data.size());//tell it that we have removed the progress loading item

                                JSONArray json_a = new JSONArray(result.getResult());
                                for (int i = 0; i < json_a.length(); i++) {
                                    ListOfArtisansModel artisan = new Gson().fromJson(json_a.get(i).toString(), ListOfArtisansModel.class);
                                    artisans_list_data.add(artisan);//add to data set
                                    artisans_adapter.notifyItemInserted(artisans_list_data.size());//notify the adapter
                                }
                                page++;
                                artisans_adapter.setLoaded();//set loaded


                            } catch (Exception ex) {
                                Log.e(tag, "line 148 " + ex.getLocalizedMessage());
                            }
                            if(artisans_list_data.size()>0)
                            {
                                lin_bottom.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                lin_bottom.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Log.e(tag, "line 138 " + e.getLocalizedMessage());
                    }
                });
    }//.get_more_data


    //how do you want to contact this artisan
    public static void show_artisan_contact_dialog(ListOfArtisansModel artisan) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(activity_context);
        pictureDialog.setTitle(activity_context.getResources().getString(R.string.contact) + " " + artisan.name);
        String[] pictureDialogItems = {
                activity_context.getResources().getString(R.string.call),
                activity_context.getResources().getString(R.string.sms)
        };

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:


                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", artisan.mobile, null));
                                activity_context.startActivity(call);


                                break;
                            case 1:
                                Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", artisan.mobile, null));
                                activity_context.startActivity(sms);
                                break;
                        }
                    }
                });
        try {
            pictureDialog.show();
        }catch (Exception ex)
        {
            Log.e(tag,"line 258 "+ex.getMessage());
        }
    }


}//fragment

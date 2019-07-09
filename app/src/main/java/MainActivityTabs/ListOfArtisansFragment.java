package MainActivityTabs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.samaritan.portchlyt_services.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


import adapters.ListOfArtisansPager.mList_of_Artisans_Adapter;
import models.ListOfArtisansModel;
import globals.*;

public class ListOfArtisansFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Spinner spinner_city;
    Spinner spinner_skill;
    RecyclerView recyclerView;
    mList_of_Artisans_Adapter artisans_adapter;
    static int page = 1;


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
        artisans_list_data= new ArrayList<>();

        //
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);

        //
        get_more_data();
        artisans_adapter = new mList_of_Artisans_Adapter(getActivity(), artisans_list_data, recyclerView);
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


    private void get_more_data() {
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
                    if (e == null) {
                        if (result == null) {
                            Log.e(tag, "line 136 result is null");
                            return;
                        } else {
                            Log.e(tag,"result "+result.getResult());
                            try {

                                artisans_adapter.notifyItemRemoved(artisans_list_data.size());//tell it that we have removed the progress loading item

                                JSONArray json_a = new JSONArray(result.getResult());
                                for (int i = 0; i < json_a.length(); i++) {
                                    ListOfArtisansModel artisan = new Gson().fromJson(json_a.get(i).toString(), ListOfArtisansModel.class);
                                    artisans_list_data.add(artisan);//add to data set
                                    artisans_adapter.notifyItemInserted(artisans_list_data.size());//notify the adapter
                                }

                                artisans_adapter.setLoaded();//set loaded


                            } catch (Exception ex) {
                                Log.e(tag, "line 148 " + ex.getLocalizedMessage());
                            }
                        }
                    } else {
                        Log.e(tag, "line 138 " + e.getLocalizedMessage());
                    }
                });
    }//.get_more_data


}//fragment

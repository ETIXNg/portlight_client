package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import MainActivityTabs.SearchServicesFragment;
import adapters.skillsAdapter;

public class SelectJobsActivity extends AppCompatActivity {
    ListView lst_skills;
    skillsAdapter adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_jobs);


        try {
            lst_skills = (ListView) findViewById(R.id.lst_skills);
            String[] skills = getResources().getStringArray(R.array.job_categories);
            adp = new skillsAdapter(skills);
            lst_skills.setAdapter(adp);
            adp.notifyDataSetChanged();
        }catch (Exception ex)
        {
            Log.e("d",ex.getMessage());
        }


    }

    //close this activity after searching for skilled artisans
    public void SearchArtisan(View v)
    {
        SearchServicesFragment.execSearchForAtisan();
        finish();//end this activity
    }
}

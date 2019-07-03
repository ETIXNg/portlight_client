package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.samaritan.portchlyt_services.R;
import com.samaritan.portchlyt_services.app;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import MainActivityTabs.SearchServicesFragment;

//this adapter is used by the SearchServicesFragment activity pop up on Searching for an artisan
//this adaptr is for selecting the skills/services needed
public class skillsAdapter extends BaseAdapter {

    String[] skills;

    public skillsAdapter(String[] l) {
        skills = l;
    }

    @Override
    public int getCount() {
        if (skills != null) return skills.length;
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (skills != null) return skills[position];
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_row_item, parent, false);

        //
        LinearLayout linlay=(LinearLayout)v.findViewById(R.id.linlay);
        final String skill = skills[position];
        //
        TextView txt_skill = (TextView) v.findViewById(R.id.txt_skill);
        txt_skill.setText(skill);

        final CheckBox chk_skill = (CheckBox) v.findViewById(R.id.chk_skill);

        //
        if (SearchServicesFragment.jobsList.contains(skill)) {
            chk_skill.setChecked(true);//set checked to be true if this skill is already selected bcoz it is contined already
        }

        linlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //either add the skill of remove the skill
                chk_skill.performClick();

                if (chk_skill.isChecked()) {
                    //add skill otherwise
                    //chk_skill.setChecked(true);
                    SearchServicesFragment.jobsList.add(skill);
                }
                else {
                    //chk_skill.setChecked(false);
                    SearchServicesFragment.jobsList.remove(skill);
                }
            }
        });

        return v;
    }
}

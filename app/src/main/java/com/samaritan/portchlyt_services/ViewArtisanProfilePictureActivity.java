package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import globals.*;


//view full profile picture
public class ViewArtisanProfilePictureActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    ImageView img_profile;
    String artisan_app_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_artisan_profile_picture);


        artisan_app_id = getIntent().getStringExtra("artisan_app_id");

        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.view_profile_picture));

        img_profile = (ImageView) findViewById(R.id.img_profile);

        //load artisan image

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.setBackgroundColor(R.color.primary);
        circularProgressDrawable.start();

        Glide.with(this)
                .load(globals.base_url + "/fetch_artisan_profile_picture?artisan_app_id=" + artisan_app_id)
                .centerCrop()
                //.placeholder(circularProgressDrawable)
                .into(img_profile);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}

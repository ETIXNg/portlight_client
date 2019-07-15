package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

//todo play ring tone notification once only
public class NotificationActivity extends AppCompatActivity {


    Toolbar mtoolbar;
    String message;

    TextView txt_notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        txt_notification=(TextView)findViewById(R.id.txt_notification);

        message = getIntent().getStringExtra("message");
        txt_notification.setText(message);

        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setTitle(getString(R.string.job_details));


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

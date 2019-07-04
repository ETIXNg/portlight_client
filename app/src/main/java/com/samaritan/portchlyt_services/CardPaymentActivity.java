package com.samaritan.portchlyt_services;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.UUID;

import io.realm.Realm;
import models.mClient;
import models.mJobs.mJobs;
import globals.*;

public class CardPaymentActivity extends AppCompatActivity {


    String _job_id;
    String tag = "CardPaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        _job_id = getIntent().getStringExtra("_job_id");

        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id", _job_id).findFirst();
        Double amount = job.getTheTotalPrice();
        db.close();

        String narration = "artisan_payment";
        String currency = "NGN";
        String country = "NG";
        String txRef = narration + "_" + UUID.randomUUID().toString();
        String publicKey = "FLWPUBK-002565847dd0b6199bbc831eee3f48fc-X";
        String encryptionKey = "00b54304c6eb7a9700ac39ff";
        String email = "email@gmail.com";
        String fName = "fName";
        String lName = "lName";



        try {

            new RavePayManager(CardPaymentActivity.this).setAmount(amount)
                    .setCountry(country)
                    .setCurrency(currency)
                    .setEmail(email)
                    .setfName(fName)
                    .setlName(lName)
                    .setNarration(narration)
                    .setPublicKey(publicKey)
                    .setEncryptionKey(encryptionKey)
                    .setTxRef(txRef)
                    .acceptAccountPayments(true)
                    .acceptCardPayments(true)
                    .acceptMpesaPayments(false)
                    .acceptAchPayments(false)
                    .acceptGHMobileMoneyPayments(false)
                    .acceptUgMobileMoneyPayments(false)
                    .onStagingEnv(true)
                    .allowSaveCardFeature(false)
                    //.setMeta(List < Meta >)
                    .withTheme(R.style.RaveFlutterWave)
                    .isPreAuth(false)//must be false no preauthing is needed
                    // .setSubAccounts(List < SubAccount >)
                    .shouldDisplayFee(true)
                    .initialize();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e(tag, ex.getLocalizedMessage());
        }


    }//.oncreate


    //run this on success
    private void run()
    {
        ProgressDialog pd = new ProgressDialog(CardPaymentActivity.this);
        pd.setMessage(getString(R.string.please_wait));
        pd.show();
        Realm db = globals.getDB();
        mJobs job = db.where(mJobs.class).equalTo("_job_id",_job_id).findFirst();
        mClient client = db.where(mClient.class).findFirst();
        Ion.with(CardPaymentActivity.this)
                .load(globals.base_url + "/make_payment_for_artisan")
                .setBodyParameter("_job_id", job._job_id)
                .setBodyParameter("client_app_id", client.app_id)
                .setBodyParameter("artisan_app_id",job.artisan_app_id )
                .setBodyParameter("amount_payed",job.getTheTotalPrice()+"" )
                .setBodyParameter("payment_type","card" )
                .asString()
                .setCallback((e, result) -> {
                    db.close();
                    pd.dismiss();
                    if (e == null) {
                        try {
                            JSONObject json = new JSONObject(result);
                            String res = json.getString("res");
                            String msg = json.getString("msg");
                            if (res.equals("ok")) {
                                Toast.makeText(CardPaymentActivity.this, getString(R.string.payment_recieved), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CardPaymentActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                                Log.e(tag, msg);
                                finish();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(CardPaymentActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                            Log.e(tag,ex.getMessage());
                            finish();
                        }
                    } else {
                        Toast.makeText(CardPaymentActivity.this, getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
                        Log.e(tag,"line 121");
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                //send to server
                run();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this,getString(R.string.error), Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }
    }


}

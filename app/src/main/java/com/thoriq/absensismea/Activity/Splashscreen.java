package com.thoriq.absensismea.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thoriq.absensismea.Config.Server;
import com.thoriq.absensismea.R;

 import java.io.IOException;

public class Splashscreen extends AppCompatActivity {

    private static int splashInterval = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ImageView imageView= (ImageView) findViewById(R.id.imageView);
        Glide.with(getApplicationContext()).load(Server.IMG_URL+"splash.png").into(imageView);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent i = new Intent(Splashscreen.this, NewGoogleSignIn.class);
                startActivity(i); // menghubungkan activity splashscren ke main activity dengan intent



                finish();
            }

        }, splashInterval);


    }

    //https://rest.nexmo.com/sms/json?api_key=9fa72601&api_secret=OuDaUMKr7ZJ2XUT6&to=+6285720921196&from=Thoriq%20Azis&text=hai_heloo
    public void sendSMS()
    {
        try
        {
            String message = "Hai";
            String nomber = "085720921196";
            SmsManager.getDefault().sendTextMessage(nomber,null,message,null,null);
            Toast.makeText(getApplicationContext(),"Terkirim",Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }

    }
    public void OnGPS()
    {
        String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps"))
        {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);

        }
    }
}


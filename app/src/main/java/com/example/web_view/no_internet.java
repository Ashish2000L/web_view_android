package com.example.web_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import static java.lang.Thread.sleep;

public class no_internet extends AppCompatActivity implements View.OnClickListener {

    Boolean status=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        findViewById(R.id.refresh).setOnClickListener(this);
        setTitle("NO INTERNET");
        //lottieannim=findViewById(R.id.animation_view);
        //lottieannim.setAnimation("no_internet.json");
        //lottieannim.loop(true);
        //lottieannim.playAnimation();
        //Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show();

    }

    public void checkConnection(){
                ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            Animatoo.animateSplit(no_internet.this);
            startActivity(new Intent(no_internet.this,web_view.class));

            finish();
            status=true;

        }
        else {
            assert mobileNetwork != null;
            if (mobileNetwork.isConnected()){
                Animatoo.animateSplit(no_internet.this);
                startActivity(new Intent(no_internet.this,web_view.class));

                finish();
                status=true;
            }
            else{
                Toast.makeText(this, "No Internet, Please check again", Toast.LENGTH_LONG).show();
                status=false;
            }
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh) {
            checkConnection();
        }
    }
}

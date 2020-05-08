package com.example.web_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ProgressBar progress;
    TextView text;
    Button btn;
    boolean connection=false;
    Intent intent,no_inter_intent;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress=findViewById(R.id.Progress);
        text=findViewById(R.id.text);
        btn=findViewById(R.id.button);

    try {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected())) {
            btn.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            text.setText("Welcome :)");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent=new Intent(MainActivity.this,web_view.class);
                    startActivity(intent);
                    finish();
                }
            });
            connection=true;
            //connection is avlilable
        }else{
            no_inter_intent=new Intent(MainActivity.this,no_internet.class);
            startActivity(no_inter_intent);
            finish();
        }
    }catch (Exception e){

         text.setText("Fail to load!");
         connection=false;




    }

    }
}

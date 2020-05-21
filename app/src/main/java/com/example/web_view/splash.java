package com.example.web_view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.util.Objects;

import static java.lang.Thread.sleep;
import static org.jsoup.Jsoup.*;

public class Splash extends AppCompatActivity {

    LinearLayout loading;
    Thread time;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);


        loading=findViewById(R.id.loading);
        text=findViewById(R.id.version);

        time=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        time.start();
        loading.setVisibility(View.VISIBLE);
        Thread to_web_view=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(8000);
                    Intent intent=new Intent(Splash.this,web_view.class);
                    Animatoo.animateFade(Splash.this);
                    //to resolve problem starting app each time from starting
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        to_web_view.start();
    }


}

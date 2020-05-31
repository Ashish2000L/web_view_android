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
import android.util.Log;
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
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
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

        final Intent intent=new Intent(Splash.this,web_view.class);
        Animatoo.animateFade(Splash.this);
        //to resolve problem starting app each time from starting


        final AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this);
        final AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.NOTIFICATION)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("http://free4all.ezyro.com/json_files/web_view.json")
                .showAppUpdated(false)
                .setTitleOnUpdateAvailable("Update available")
                .setContentOnUpdateAvailable("Check out the latest version available of my app!")
                .setTitleOnUpdateNotAvailable("Update not available")
                .setContentOnUpdateNotAvailable("no update available!!")
                .setButtonUpdate("Update now?")
                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        appUpdaterUtils.setUpdateFrom(UpdateFrom.JSON);
                        appUpdaterUtils.setUpdateJSON("http://free4all.ezyro.com/json_files/web_view.json");
                        appUpdaterUtils.withListener(new AppUpdaterUtils.UpdateListener() {
                                    @Override
                                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                        Log.d("Latest Version", update.getLatestVersion());
                                        Log.d("Latest Version Code", update.getLatestVersionCode().toString());
                                        Log.d("URL", update.getUrlToDownload().toString());
                                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailed(AppUpdaterError error) {
                                        Log.d("AppUpdater Error", "Something went wrong");
                                    }
                                });
                        appUpdaterUtils.start();
                    }
                })
                .setButtonDismiss("Maybe later")
                .setButtonDismissClickListener(null)
                .setButtonDoNotShowAgain(null)
                .setCancelable(false);
        appUpdater.start();


    }

}

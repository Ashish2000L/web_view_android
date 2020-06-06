package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import com.google.android.play.core.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.jsoup.Jsoup.*;

public class Splash extends AppCompatActivity {

    LinearLayout loading;
    Thread time;
    TextView text;
    int version=BuildConfig.VERSION_CODE;
    FirebaseRemoteConfig firebaseRemoteConfig;
    private static final String VersionCode="versioncode";
    private static final String Message="message";
    private static final String Ismessage="is_new_message";
    private  static final String updatedetails="updatedetails";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

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


        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        getdetails();
    }

//    private void fetchwelcome() {
////import com.google.android.play.core.tasks.OnCompleteListener;
//        boolean is_using_developerMode=firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
//        int catchExpiration;
//        if(is_using_developerMode) {
//
//            catchExpiration=0;
//
//        }else {
//            catchExpiration = 3600;
//        }
//        firebaseRemoteConfig.fetch(catchExpiration)
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Splash.this, "Fetch Succeeded",
//                                    Toast.LENGTH_SHORT).show();
//                            firebaseRemoteConfig.activateFetched();
//                        } else {
//                            Toast.makeText(Splash.this, "Fetch Failed",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//        // [END fetch_config_with_callback]
//    }
    private void getdetails()
    {
        boolean is_using_developerMode=firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if(is_using_developerMode) {

            catchExpiration=0;

        }else {
            catchExpiration = 3600;
        }
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if(task.isSuccessful())
                {
                   Toast.makeText(Splash.this, "Fetch Successful", Toast.LENGTH_SHORT).show();
                    firebaseRemoteConfig.activateFetched();
                }else{
                    Toast.makeText(Splash.this, "Fetch Failed",
                           Toast.LENGTH_SHORT).show();
                }
                displaywelcomemessage();
            }


        });
    }

    private void displaywelcomemessage() {

        String versioncode=firebaseRemoteConfig.getString(VersionCode);
        String message=firebaseRemoteConfig.getString(Message);
        boolean ismessage=firebaseRemoteConfig.getBoolean(Ismessage);
        final String update_details=firebaseRemoteConfig.getString(updatedetails);
        final Intent intent=new Intent(Splash.this,web_view.class);

        text.setText(versioncode);
        if(Integer.parseInt(versioncode)==version)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Available")
                    .setMessage(update_details.toString()+"\n please update to access latest features")
                    .setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setPositiveButton("Update now", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                            String fileName = "AppName.apk";
                            destination += fileName;
                            final Uri uri = Uri.parse("file://" + destination);

                            //Delete update file if exists
                            File file = new File(destination);
                            if (file.exists()) {
                                boolean result=file.delete();
                                if(result)
                                {
                                    Toast.makeText(Splash.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(Splash.this, "Unable to delete file", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(Splash.this, "no dupligate exist", Toast.LENGTH_SHORT).show();
                            }

                            final String url = "http://free4all.ezyro.com/apks/google_apk.apk";


                            Dexter.withActivity(Splash.this)
                                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                            UpdateApp atualizaApp = new UpdateApp();
                                            atualizaApp.setContext(getApplicationContext());
                                            atualizaApp.execute(url);
                                            //DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                            //request.setDescription(update_details);
                                            //request.setTitle("Google");
//
                                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                            //    request.allowScanningByMediaScanner();
                                            //    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                            //}
                                            //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "google.apk");
//
//
                                            //DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                            //assert manager != null;
                                            //manager.enqueue(request);
                                            //Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                            permissionToken.continuePermissionRequest();
                                        }
                                    }).check();

                            Toast.makeText(Splash.this, Environment.DIRECTORY_DOCUMENTS+"/google.apk", Toast.LENGTH_SHORT).show();

                        }
                    }).show();

        }

    }

    public class UpdateApp extends AsyncTask<String,Void,Void>{
        private Context context;
        public void setContext(Context contextf){
            context = contextf;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "google.apk");
                if(outputFile.exists()){
                    boolean result =outputFile.delete();
                    if(result)
                    {
                        Toast.makeText(context, "file deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show();
                    }
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/google.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
    }
}



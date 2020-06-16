package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.IpSecManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.util.Objects;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

//id for admob:- ca-app-pub-7816115484592490~6598652731
public class Splash extends AppCompatActivity {

    LinearLayout loading;
    Thread time;
    TextView text;
    int version = BuildConfig.VERSION_CODE;
    FirebaseRemoteConfig firebaseRemoteConfig;
    WebView webView;
    private static final String VersionCode = "versioncode";
    private static final String Message = "message";
    private static final String force_update = "force_update";
    private static final String updatedetails = "updatedetails";
    private static final String Url = "url";
    private  Uri path;
    private  Context context;
    private  DownloadManager downloadManager;
    long download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        webView = findViewById(R.id.lol_web_view);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        getdetails();

        loading = findViewById(R.id.loading);
        text = findViewById(R.id.version);

    }

    private void getdetails()
    {
        boolean is_using_developerMode = firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if (is_using_developerMode) {

            catchExpiration = 0;

        } else {
            catchExpiration = 3600;
        }
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if (task.isSuccessful()) {
                    check_for_update();
                } else {
                    Toast.makeText(Splash.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                    final Intent transfer= new Intent(Splash.this,web_view.class);
                    Thread new_intent=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sleep(5000);
                                transfer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                transfer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                transfer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    new_intent.start();

                    loading.setVisibility(View.VISIBLE);
                    Thread intent = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                sleep(3000);
                                startActivity(transfer);
                                finish();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    intent.start();
                }
            }
        });
    }

    //checking for updates
    private void check_for_update()
    {
        String versioncode = firebaseRemoteConfig.getString(VersionCode);
        final Intent intent = new Intent(Splash.this, web_view.class);
        if (Integer.parseInt(versioncode) == version) {
            time = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(5000);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            time.start();

            loading.setVisibility(View.VISIBLE);

            Thread new_intent = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            new_intent.start();
        } else {

            if (!firebaseRemoteConfig.getBoolean(force_update)) {
                displaywelcomemessagenotforce();
            } else if (firebaseRemoteConfig.getBoolean(force_update)) {
                updatebyforce();
            }

        }

    }

    // forced update for latest update
    private void updatebyforce()
    {

        String message = firebaseRemoteConfig.getString(Message);
        final String update_details = firebaseRemoteConfig.getString(updatedetails);
        final String new_Url = firebaseRemoteConfig.getString(Url).trim();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Available")
                    .setMessage(update_details)
                    .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(new_Url);
                            text.setText(new_Url);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setDownloadListener(new DownloadListener() {
                                @Override
                                public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {

                                    File file_new = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));

                                    if(file_new.exists())
                                    {
                                        if(!file_new.isDirectory())
                                        {
                                            if(file_new.delete())
                                            {
                                                text.setText("File deleted");
                                                Toast.makeText(Splash.this, "File Deleted", Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            text.setText("File is a directory");
                                            Toast.makeText(Splash.this, "File is a directory", Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        text.setText("file don't exist");
                                        Toast.makeText(Splash.this, "File doesn't exist", Toast.LENGTH_LONG).show();
                                    }

                                    Dexter.withActivity(Splash.this)
                                            .withPermission(WRITE_EXTERNAL_STORAGE)
                                            .withListener(new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                                    DownloadFile(url,userAgent,contentDisposition,mimetype,contentLength);
                                                }

                                                @Override
                                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                                }

                                                @Override
                                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                                    permissionToken.continuePermissionRequest();
                                                }
                                            }).check();
                                    webView.setVisibility(View.GONE);
                                }
                            });

                        }
                    }).show();


    }


    //dialog for latest update
    private void displaywelcomemessagenotforce()
    {

        String message = firebaseRemoteConfig.getString(Message);
        final String update_details = firebaseRemoteConfig.getString(updatedetails);
        final String new_Url = firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(Splash.this, web_view.class);


            //giving dialog for an available update
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Available");
            builder.setMessage(update_details);
            builder.setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(new_Url);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setDownloadListener(new DownloadListener() {
                        @Override
                        public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {

                            File file_new = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));

                            if(file_new.exists())
                            {
                                if(!file_new.isDirectory())
                                {
                                    if(file_new.delete())
                                    {
                                        text.setText("File deleted");
                                        Toast.makeText(Splash.this, "File Deleted", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    text.setText("File is a directory");
                                    Toast.makeText(Splash.this, "File is a directory", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                text.setText("file don't exist");
                                Toast.makeText(Splash.this, "File doesn't exist", Toast.LENGTH_LONG).show();
                            }

                            Dexter.withActivity(Splash.this)
                                    .withPermission(WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse response) {
                                            DownloadFile(url,userAgent,contentDisposition,mimetype,contentLength);
                                        }

                                        @Override
                                        public void onPermissionDenied(PermissionDeniedResponse response) {

                                        }

                                        @Override
                                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    }).check();

                            webView.setVisibility(View.GONE);
                        }
                    });

                }
            }).show();
        }

        //for starting downloading process
    public void DownloadFile(String url, String userAgent, final String contentDisposition, final String mimetype, final long contentLength )
    {

        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        destination += fileName;
        final Uri new_uri = Uri.parse("file://" + destination);
        text.setText("Downloading will start in a minute...");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType(mimetype);
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription(fileName);
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(new_uri);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        download = downloadManager.enqueue(request);

        Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_LONG).show();

        text.setText("Please tap on notificaiton when downloading is complete...");

        File file_dir = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));
        path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file_dir);

        BroadcastReceiver new_brpdcast = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                context.startActivity(pdfOpenintent);
                context.unregisterReceiver(this);
            }
        };
        registerReceiver(new_brpdcast, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }
}
package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import static java.lang.Thread.sleep;


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
    private Uri mDestinationUri;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);


        webView = findViewById(R.id.lol_web_view);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        loading = findViewById(R.id.loading);
        text = findViewById(R.id.version);

        time = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        time.start();
        loading.setVisibility(View.VISIBLE);

        final Intent intent = new Intent(Splash.this, web_view.class);
        //to resolve problem starting app each time from starting


        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        getdetails();
    }

    private void getdetails() {
        boolean is_using_developerMode = firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if (is_using_developerMode) {

            catchExpiration = 0;

        } else {
            catchExpiration = 3600;
        }
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(Splash.this, "Fetch Successful", Toast.LENGTH_SHORT).show();
                    firebaseRemoteConfig.activateFetched();
                } else {
                    Toast.makeText(Splash.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }
                if (!firebaseRemoteConfig.getBoolean(force_update)) {
                    displaywelcomemessagenotforce();
                } else if (firebaseRemoteConfig.getBoolean(force_update)) {
                    updatebyforce();
                }

            }


        });
    }

    private void updatebyforce() {

        String versioncode = firebaseRemoteConfig.getString(VersionCode);
        String message = firebaseRemoteConfig.getString(Message);
        final String update_details = firebaseRemoteConfig.getString(updatedetails);
        final String new_Url = firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(Splash.this, web_view.class);

        text.setText(versioncode);
        if (Integer.parseInt(versioncode) == version) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Animatoo.animateFade(Splash.this);
            startActivity(intent);
            finish();
        } else {
            final File newfile = new File(Environment.getExternalStorageDirectory(), "google_dicectory");
            File file_dir = new File(newfile.getPath(), "google.apk");
            if (file_dir.exists()) {
                text.setText("File found in the directory at" + file_dir.getPath());
                if (file_dir.delete()) {
                    Toast.makeText(this, "File Deleted Successfully  at " + file_dir.getPath(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "file can't be delted at " + file_dir.getPath(), Toast.LENGTH_SHORT).show();
                }
            } else {
                text.setText("no such file found!!");
            }
            if (!newfile.exists()) {
                if (!newfile.mkdirs()) {
                    Toast.makeText(Splash.this, "Unable to make directory", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Splash.this, "dir made at " + newfile.getPath(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Splash.this, "dir exist", Toast.LENGTH_SHORT).show();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Available")
                    .setMessage(update_details.toString() + "\n please update to access latest features")
                    .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(new_Url);
                            text.setText(new_Url);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setDownloadListener(new DownloadListener() {
                                @Override
                                public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, long contentLength) {

                                    Dexter.withActivity(Splash.this)
                                            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            .withListener(new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                                    text.setText("Downloading will start in a minute");
                                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                                    request.setMimeType(mimetype);
                                                    String cookie = CookieManager.getInstance().getCookie(url);
                                                    request.addRequestHeader("cookie", cookie);
                                                    request.addRequestHeader("User-Agent", userAgent);
                                                    request.setDescription("Downloading .....");
                                                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    request.allowScanningByMediaScanner();
                                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                                    assert downloadManager != null;
                                                    final long download = downloadManager.enqueue(request);
                                                    Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_SHORT).show();

                                                    //final Uri uri=Uri.parse(newfile.getPath()+"/google.apk");
                                                    //File file = new File(String.valueOf(uri));
                                                    //Uri path = Uri.fromFile(file);
                                                    //Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                                                    //pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    //pdfOpenintent.setDataAndType(path, "application/apk");
                                                    //try {
                                                    //    startActivity(pdfOpenintent);
                                                    //}
                                                    //catch (ActivityNotFoundException e) {
//
                                                    //    text.setText(e.getMessage());
                                                    //}
                                                    File new_file = new File(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    final Uri uri = Uri.parse(new_file.getPath());
                                                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                                        @Override
                                                        public void onReceive(Context context, Intent intent) {
                                                            Intent install = new Intent(Intent.ACTION_VIEW);
                                                            install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            install.setDataAndType(uri, downloadManager.getMimeTypeForDownloadedFile(download));
                                                            startActivity(install);
                                                            unregisterReceiver(this);
                                                            finish();
                                                        }
                                                    };
                                                    registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


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

    }


    private void displaywelcomemessagenotforce() {

        String versioncode = firebaseRemoteConfig.getString(VersionCode);
        String message = firebaseRemoteConfig.getString(Message);
        final String update_details = firebaseRemoteConfig.getString(updatedetails);
        final String new_Url = firebaseRemoteConfig.getString(Url).trim();
        final Intent intent = new Intent(Splash.this, web_view.class);

        //text.setText(versioncode);
        if (Integer.parseInt(versioncode) == version) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Available")
                    .setMessage(update_details.toString() + "\n please update to access latest features")
                    .setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //webView.setVisibility(View.VISIBLE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setPositiveButton("Update now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl(new_Url);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setDownloadListener(new DownloadListener() {
                                @Override
                                public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, long contentLength) {

                                    final File file_dir = new File(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                                    setDestinationInExternalFilesDir(Splash.this, Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));

                                    String file_name = URLUtil.guessFileName(url, contentDisposition, mimetype);
                                    final File file = new File(mDestinationUri.toString());
                                    if (file.exists()) {
                                        if (!file.isDirectory()) {
                                            if (file.delete()) {
                                                System.out.print("File deleted successfully");
                                            } else {
                                                System.out.print("Error deleting file");
                                            }
                                        } else {
                                            System.out.print("it is not a directory");
                                        }
                                    } else {
                                        System.out.print("File not exist");
                                    }
                                    Uri mime = Uri.parse(file_dir.getPath());
                                    Toast.makeText(Splash.this, file_name + "\n\n\n" + file_dir, Toast.LENGTH_LONG).show();
                                    if (file_dir.exists()) {
                                        text.setText("File found in the directory at" + file_dir.getPath());
                                        if (file_dir.delete()) {
                                            Toast.makeText(Splash.this, "File Deleted Successfully  at " + file_dir.getPath(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Splash.this, "file can't be delted at " + file_dir.getPath(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        text.setText("no such file found!!");
                                    }

                                    Dexter.withActivity(Splash.this)
                                            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            .withListener(new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                                    //final Uri uri=Uri.parse(new File(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype)).getPath());

                                                    text.setText(url);
                                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                                                    request.setMimeType(mimetype);
                                                    String cookie = CookieManager.getInstance().getCookie(url);
                                                    request.addRequestHeader("cookie", cookie);
                                                    request.addRequestHeader("User-Agent", userAgent);
                                                    request.setDescription("Downloading .....");
                                                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    request.allowScanningByMediaScanner();
                                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                    //String result=request.setDestinationInExternalFilesDir(Splash.this,Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype)).toString();
                                                    request.setDestinationUri(FileProvider.getUriForFile(Splash.this,Splash.this.getApplicationContext().getPackageName()+".provider",file_dir));
                                                    //request.setDestinationInExternalFilesDir(Splash.this, Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype));
                                                    final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                                    assert downloadManager != null;
                                                    final long download = downloadManager.enqueue(request);
                                                    Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_LONG).show();

                                                    Uri uri_for_file = downloadManager.getUriForDownloadedFile(download);
                                                    text.setText("Please tap on notificaiton when downloading is complete...");

                                                    //final Uri uri=Uri.parse(newfile.getPath()+"/google.apk");
                                                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                                        @Override
                                                        public void onReceive(Context context, Intent intent) {

                                                            //File file = new File(String.valueOf(uri));
                                                            //Uri path = Uri.fromFile(file);
                                                            Uri path = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",file_dir);
                                                            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                                                            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                            pdfOpenintent.setDataAndType(path, mimetype);
                                                            pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                            try {
                                                                context.startActivity(pdfOpenintent);

                                                            } catch (Exception e) {

                                                                text.setText(e.getMessage());
                                                            }

                                                            //Intent install=new Intent(Intent.ACTION_VIEW);
                                                            //install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            //install.setDataAndType(uri,downloadManager.getMimeTypeForDownloadedFile(download));
                                                            //startActivity(install);
                                                            unregisterReceiver(this);
                                                            finish();
                                                        }
                                                    };
                                                    registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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

                            //Dexter.withActivity(Splash.this)
                            //        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            //        .withListener(new PermissionListener() {
                            //            @Override
                            //            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            //                //UpdateApp atualizaApp = new UpdateApp();
                            //                //atualizaApp.setContext(getApplicationContext());
                            //                //atualizaApp.execute(new_Url);
                            //                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(new_Url));
                            //                request.setDescription(update_details);
                            //                request.setTitle("google.apk");
                            //                String cookie= CookieManager.getInstance().getCookie(new_Url);
                            //                request.addRequestHeader("cookie",cookie);
                            //                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            //                request.allowScanningByMediaScanner();
                            //                request.setDestinationInExternalPublicDir(newfile.getPath(),"google.apk");
//
                            //                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            //                assert manager != null;
                            //                final long download =manager.enqueue(request);
//
                            //                final Uri uri=Uri.parse(newfile.getPath()+"/google.apk");
                            //                BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
                            //                    @Override
                            //                    public void onReceive(Context context, Intent intent) {
                            //                        Intent install=new Intent(Intent.ACTION_VIEW);
                            //                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //                        install.setDataAndType(uri,manager.getMimeTypeForDownloadedFile(download));
//
                            //                        startActivity(install);
                            //                        unregisterReceiver(this);
                            //                        finish();
                            //                    }
                            //                };
                            //                registerReceiver(broadcastReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            //                Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_SHORT).show();
                            //                //new downloadfilefromurl().execute(new_Url,newfile.getPath());
                            //            }
//
                            //            @Override
                            //            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//
                            //            }
//
                            //            @Override
                            //            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//
                            //                permissionToken.continuePermissionRequest();
                            //            }
                            //        }).check();


                        }
                    }).show();
        }

    }

    public void setDestinationInExternalFilesDir(Context context, String dirType,
                                                 String subPath) {
        final File file = context.getExternalFilesDir(dirType);
        if (file == null) {
            throw new IllegalStateException("Failed to get external storage files directory");
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: " +
                        file.getAbsolutePath());
            }
        }
        setDestinationFromBase(file, subPath);

    }

    private void setDestinationFromBase(File base, String subPath) {
        if (subPath == null) {
            throw new NullPointerException("subPath cannot be null");
        }
        mDestinationUri = Uri.withAppendedPath(Uri.fromFile(base), subPath);
    }

    public class GenericFileProvider extends FileProvider {}
}



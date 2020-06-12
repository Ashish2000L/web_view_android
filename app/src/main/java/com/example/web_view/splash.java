package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import static android.Manifest.permission.INSTALL_PACKAGES;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.REQUEST_INSTALL_PACKAGES;
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
    private Uri mDestinationUri;
    private  Uri path;
    private  Context context;
    private  DownloadManager downloadManager;
    long download;

    int mypermissionforinstalingpackage=150;
    int mypermissionrequestread=101;

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
                                            .withPermission(WRITE_EXTERNAL_STORAGE)
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
            builder.setTitle("Update Available");
            builder.setMessage(update_details.toString() + "\n please update to access latest features");
            builder.setNegativeButton("Maybe later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //webView.setVisibility(View.VISIBLE);
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
                                    .withPermission(WRITE_EXTERNAL_STORAGE)
                                    .withListener(new PermissionListener() {
                                        @Override
                                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                            //final Uri uri=Uri.parse(new File(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype)).getPath());

                                            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                                            String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                                            destination += fileName;
                                            final Uri new_uri = Uri.parse("file://" + destination);
                                            text.setText(url);
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
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                            request.setDestinationUri(new_uri);
                                            //String result=request.setDestinationInExternalFilesDir(Splash.this,Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype)).toString();
                                            //request.setDestinationUri(FileProvider.getUriForFile(Splash.this,Splash.this.getApplicationContext().getPackageName()+".provider",file_dir));
                                            //request.setDestinationInExternalFilesDir(Splash.this, Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                                            //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype));
                                            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            assert downloadManager != null;
                                             download = downloadManager.enqueue(request);
                                            Toast.makeText(Splash.this, "Downloading....", Toast.LENGTH_LONG).show();

                                            final Uri uri_for_file = downloadManager.getUriForDownloadedFile(download);
                                            text.setText("Please tap on notificaiton when downloading is complete...");

                                            //final Uri uri=Uri.parse(newfile.getPath()+"/google.apk");
                                            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                                @Override
                                                public void onReceive(final Context context, Intent intent) {
                                                    File file_dir = new File("/storage/emulated/0/Download/", URLUtil.guessFileName(url, contentDisposition, mimetype));
                                                    path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file_dir);
                                                    //if (file_dir.setReadable(true, false)) {
                                                    //    Toast.makeText(context, "made readble", Toast.LENGTH_LONG).show();
                                                    //}

                                                    context.grantUriPermission(BuildConfig.APPLICATION_ID, path, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                                    if (ContextCompat.checkSelfPermission(Splash.this, INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions(Splash.this,new String[]{READ_EXTERNAL_STORAGE},mypermissionrequestread);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                            ActivityCompat.requestPermissions(Splash.this,new String[]{REQUEST_INSTALL_PACKAGES},mypermissionrequestread);
                                                        }
                                                        //Dexter.withActivity(Splash.this)
                                                        //        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                                        //        .withListener(new PermissionListener() {
                                                        //            @Override
                                                        //            public void onPermissionGranted(PermissionGrantedResponse response) {
                                                        //                final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                                                        //                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        //                pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                                                        //                context.startActivity(pdfOpenintent);
                                                        //            }
//
                                                        //            @Override
                                                        //            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                        //
                                                        //            }
//
                                                        //            @Override
                                                        //            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
                                                        //                token.continuePermissionRequest();
                                                        //            }
                                                        //        }).check();
                                                    }else{
                                                        final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                                                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
                                                        context.startActivity(pdfOpenintent);
                                                    }


                                                    //Intent install=new Intent(Intent.ACTION_VIEW);
                                                    //install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    //install.setDataAndType(uri,downloadManager.getMimeTypeForDownloadedFile(download));
                                                    //startActivity(install);
                                                    context.unregisterReceiver(this);
                                                    //finish();
                                                }
                                            };
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), READ_EXTERNAL_STORAGE,null);
                                            }
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

   @RequiresApi(api = Build.VERSION_CODES.M)
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
       if (requestCode == mypermissionrequestread) {
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               //null
           }else{
               finishAffinity();
           }
       }else if(requestCode==mypermissionforinstalingpackage){
           if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               final Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
               pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               pdfOpenintent.setDataAndType(path, downloadManager.getMimeTypeForDownloadedFile(download));
               context.startActivity(pdfOpenintent);
               //testrecever testrecever=new testrecever();
               //testrecever.onReceive(context,pdfOpenintent);
               //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               //    registerReceiver(testrecever,new IntentFilter(Intent.ACTION_INSTALL_PACKAGE),Manifest.permission.REQUEST_INSTALL_PACKAGES,null);
               //}
           }else{
               finishAffinity();
           }
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

    //public class GenericFileProvider extends FileProvider {}
}



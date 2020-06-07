package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

public class web_view extends AppCompatActivity {
    private WebView webView;
    private String webviewurl="webview_url";//https://newsverify197155133.wordpress.com/";
    private ProgressBar progressweb;
    private String weburl;
    ProgressDialog progressDialog;
    TextView text_no_internet;
    View background;
    Boolean status =false;
    LottieAnimationView anim_no_internet;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRemoteConfig firebaseRemoteConfig;
    Thread time;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.do_no_move, R.anim.do_no_move);
        setContentView(R.layout.activity_web_view);


        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        anim_no_internet=findViewById(R.id.no_internet);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        background = findViewById(R.id.background);
        text_no_internet=findViewById(R.id.no_internet_text);

        progressDialog=new ProgressDialog(this);
        progressweb=findViewById(R.id.progress);
        webView=findViewById(R.id.webview);

        weburl=getdetails();
        if(savedInstanceState !=null){
            webView.getSettings().setJavaScriptEnabled(true);
            progressDialog.setMessage("Loading please wait...");
            webView.restoreState(savedInstanceState);
            webView.setWebViewClient(new Browser());
            webView.setWebChromeClient(new MyWebClient());
            //webView.setWebChromeClient(new myChrome());
        }else{
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            WebSettings webSettings=webView.getSettings();
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);
            progressDialog.setMessage("Loading please wait...");
            webView.setWebViewClient(new Browser());
            webView.setWebChromeClient(new MyWebClient());
            //webView.setWebChromeClient(new myChrome());
            checkConnection();
        }

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //for swipe to reload option
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              refresh_check_Connection();

            }
        });

        //set colours to the swipe relode option
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.CYAN);


        //added download listner to make files download from browser
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, long contentLength) {

                Dexter.withActivity(web_view.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                DownloadManager.Request request= new DownloadManager.Request(Uri.parse(url));
                                request.setMimeType(mimetype);
                                String cookie= CookieManager.getInstance().getCookie(url);
                                request.addRequestHeader("cookie",cookie);
                                request.addRequestHeader("User-Agent",userAgent);
                                request.setDescription("Downloading .....");
                                request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimetype));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url,contentDisposition,mimetype));
                                DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                assert downloadManager != null;
                                downloadManager.enqueue(request);
                                Toast.makeText(web_view.this, "Downloading....", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        //for updating progress
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressweb.setVisibility(View.VISIBLE);
                progressDialog.show();

                progressweb.setProgress(newProgress);
                if(newProgress==100){
                    swipeRefreshLayout.setRefreshing(false);
                    progressweb.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    //to get the url to be seen by the user in webview from firebase
    private String getdetails()
    {
        boolean is_using_developerMode=firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if(is_using_developerMode) {

            catchExpiration=0;

        }else {
            catchExpiration = 3600;
        }
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(web_view.this, "Fetch Successful", Toast.LENGTH_SHORT).show();
                    firebaseRemoteConfig.activateFetched();
                }else{
                    Toast.makeText(web_view.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }

            }


        });
        return firebaseRemoteConfig.getString(webviewurl).trim();
    }

    //for checkting backpress condition
    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }else {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();//this will kill all the activity that are runnig
                        }
                    }).show();
        }
    }

    //for checking internet connection
    public void checkConnection(){
        progressDialog.show();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            webView.loadUrl(weburl);
            setTitle(getTitle());
            webView.setVisibility(View.VISIBLE);
            text_no_internet.setVisibility(View.INVISIBLE);
            anim_no_internet.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            status=true;

        }
        else {
            assert mobileNetwork != null;
            if (mobileNetwork.isConnected()){
                webView.loadUrl(weburl);
                setTitle(getTitle());
                webView.setVisibility(View.VISIBLE);
                text_no_internet.setVisibility(View.INVISIBLE);
                anim_no_internet.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                status=true;
            }
            else{
                if(webView.getTitle().equals("Webpage not available")){
                progressDialog.dismiss();
                webView.setVisibility(View.INVISIBLE);
                text_no_internet.setVisibility(View.VISIBLE);
                anim_no_internet.setVisibility(View.VISIBLE);
                status=false;
                }else{
                    progressDialog.dismiss();

                    webView.setVisibility(View.INVISIBLE);
                    text_no_internet.setVisibility(View.VISIBLE);
                    anim_no_internet.setVisibility(View.VISIBLE);
                    status=false;
                }
            }
        }
    }


    public void refresh_check_Connection(){
        progressDialog.show();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            webView.loadUrl(webView.getUrl());
            setTitle(getTitle());
            webView.setVisibility(View.VISIBLE);
            text_no_internet.setVisibility(View.INVISIBLE);
            anim_no_internet.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            status=true;

        }
        else {
            assert mobileNetwork != null;
            if (mobileNetwork.isConnected()){
                webView.loadUrl(webView.getUrl());
                webView.setVisibility(View.VISIBLE);
                text_no_internet.setVisibility(View.INVISIBLE);
                anim_no_internet.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
                status=true;
            }
            else{
                if(webView.getTitle().equals("Webpage not available")){
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    webView.setVisibility(View.INVISIBLE);
                    text_no_internet.setVisibility(View.VISIBLE);
                    anim_no_internet.setVisibility(View.VISIBLE);
                    status=false;
                }else{
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    webView.setVisibility(View.INVISIBLE);
                    text_no_internet.setVisibility(View.VISIBLE);
                    anim_no_internet.setVisibility(View.VISIBLE);
                    status=false;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

    }


    class Browser
            extends WebViewClient
    {
        Browser() {}

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
        {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient
            extends WebChromeClient
    {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (web_view.this == null) {
                return null;
            }
            return BitmapFactory.decodeResource(web_view.this.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)web_view.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            web_view.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            web_view.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = web_view.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = web_view.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)web_view.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            web_view.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

}

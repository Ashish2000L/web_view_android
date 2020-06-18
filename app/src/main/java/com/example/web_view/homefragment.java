package com.example.web_view;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.view.InputQueue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;
import java.util.concurrent.Executor;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class homefragment extends Fragment {

    private WebView webView;
    private String webviewurl="webview_url";//https://newsverify197155133.wordpress.com/";
    private ProgressBar progressweb;
    private String weburl;
    private ProgressDialog progressDialog;
    private TextView text_no_internet;
    //private View background;
    private Boolean status =false;
    private LottieAnimationView anim_no_internet;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseRemoteConfig firebaseRemoteConfig;
    Thread time;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    private static final String TAG = "web_view";


    public homefragment() {
        // Required empty public constructor
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_homefragment, container, false);
        firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        navigationView=view.findViewById(R.id.navmenu);
        drawerLayout=view.findViewById(R.id.drawer);
        anim_no_internet=view.findViewById(R.id.no_internet);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        //background = view.findViewById(R.id.background);
        text_no_internet=view.findViewById(R.id.no_internet_text);

        progressDialog=new ProgressDialog(getContext());
        progressweb=view.findViewById(R.id.progress);
        webView=view.findViewById(R.id.webview);

        webView.setVisibility(View.VISIBLE);

        weburl=getdetails();
        if(savedInstanceState !=null){
            webView.getSettings().setJavaScriptEnabled(true);
            progressDialog.setMessage("Loading please wait...");
            webView.restoreState(savedInstanceState);
            //webView.setWebChromeClient(new myChrome());
        }else{
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            WebSettings webSettings=webView.getSettings();
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);
            progressDialog.setMessage("Loading please wait...");
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

                Dexter.withActivity(getActivity())
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
                                DownloadManager downloadManager=(DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(Context.DOWNLOAD_SERVICE);
                                assert downloadManager != null;
                                downloadManager.enqueue(request);
                                Toast.makeText(getContext(), "Downloading....", Toast.LENGTH_SHORT).show();

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



        return view;
    }

    private String getdetails()
    {
        boolean is_using_developerMode=firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled();
        int catchExpiration;
        if(is_using_developerMode) {

            catchExpiration=0;

        }else {
            catchExpiration = 3600;
        }
        firebaseRemoteConfig.fetch(catchExpiration).addOnCompleteListener((Executor) this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Fetch Successful", Toast.LENGTH_SHORT).show();
                    firebaseRemoteConfig.activateFetched();
                }else{
                    Toast.makeText(getActivity(), "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }

            }


        });
        return firebaseRemoteConfig.getString(webviewurl).trim();
    }



    /*@Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {

            webView.goBack();



        }else {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity(Objects.requireNonNull(getActivity()));//this will kill all the activity that are runnig
                        }
                    }).show();
        }
    }*/

    //for checking internet connection
    public void checkConnection(){
        progressDialog.show();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            webView.loadUrl(weburl);
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
                Objects.requireNonNull(getActivity()).getSystemService (Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo wifi =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        assert wifi != null;
        if(wifi.isConnected()){
            webView.loadUrl(webView.getUrl());
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

    }


}

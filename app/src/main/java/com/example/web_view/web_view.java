package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import static java.lang.Thread.sleep;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

public class web_view extends AppCompatActivity {
    private WebView webView;
    private String weburl="https://www.google.com";//"https://newsverify197155133.wordpress.com/";
    private ProgressBar progressweb;
    ProgressDialog progressDialog;
    TextView text_no_internet;
    View background;
    Boolean status =false;
    LottieAnimationView anim_no_internet;
    SwipeRefreshLayout swipeRefreshLayout;
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


        anim_no_internet=findViewById(R.id.no_internet);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        background = findViewById(R.id.background);
        text_no_internet=findViewById(R.id.no_internet_text);

        progressDialog=new ProgressDialog(this);
        progressweb=findViewById(R.id.progress);
        webView=findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        progressDialog.setMessage("Loading please wait...");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        checkConnection();

        //for swipe to reload option
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              refresh_check_Connection();

            }
        });

        //set colours to the swipe relode option
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.CYAN);
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



        time=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    background.setVisibility(View.INVISIBLE);

                    sleep(3000);
                    if (savedInstanceState == null) {


                        final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

                        if (viewTreeObserver.isAlive()) {
                            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {
                                    circularRevealActivity();
                                    background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }

                            });
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        if(status)
        {
            time.start();
        }
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

    private void circularRevealActivity() {
        int cx = background.getRight() - getDips(44);
        int cy = background.getBottom() - getDips(44);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                background,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(3000);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();

    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
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
}

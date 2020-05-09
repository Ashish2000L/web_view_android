package com.example.web_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class web_view extends AppCompatActivity {
    private WebView webView;
    private String weburl="https://newsverify197155133.wordpress.com/";
    private ProgressBar progressweb;
    ProgressDialog progressDialog;
    TextView text_no_internet;
    LottieAnimationView no_internet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_web_view);

        text_no_internet=findViewById(R.id.no_internet_text);
        no_internet=findViewById(R.id.no_internet);
        progressDialog=new ProgressDialog(this);
        progressweb=findViewById(R.id.progress);
        webView=findViewById(R.id.webview);

        progressDialog.setMessage("Loading please wait...");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        checkConnection();

        //for updating progress
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressweb.setVisibility(View.VISIBLE);
                progressDialog.show();

                progressweb.setProgress(newProgress);
                if(newProgress==100){
                    progressweb.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    setTitle(getTitle());
                    //setTitleColor(getTitleColor());
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }
    //for checkting backpress condition
    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }else {
            super.onBackPressed();
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
            progressDialog.dismiss();

        }
        else {
            assert mobileNetwork != null;
            if (mobileNetwork.isConnected()){
                webView.loadUrl(weburl);
                setTitle(getTitle());
                webView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
            else{
                progressDialog.dismiss();
                webView.setVisibility(View.GONE);
                setTitle("NO INTERNET AVAILABLE");
                no_internet.setVisibility(View.VISIBLE);
                text_no_internet.setVisibility(View.VISIBLE);
            }
        }


    }
    //for reload menue on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_reload) {
            checkConnection();
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.web_view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;

public class web_view extends AppCompatActivity {
    private WebView webView;
    private String weburl="https://newsverify197155133.wordpress.com/";
    private ProgressBar progressweb;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_web_view);

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
        webView.loadUrl(weburl);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressweb.setVisibility(View.VISIBLE);
                progressDialog.show();
                progressweb.setProgress(newProgress);
                if(newProgress==100){
                    progressweb.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
        {
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

}

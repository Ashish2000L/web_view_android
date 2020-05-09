package com.example.web_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import java.util.Objects;

import static java.lang.Thread.sleep;

public class Splash extends AppCompatActivity {

    TextView text;
    ProgressBar progress;
    Boolean status=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        text=findViewById(R.id.welcome);
        progress=findViewById(R.id.progress);
        /*
        anim_splash= AnimationUtils.loadAnimation(this,R.anim.animation);
       anim=AnimationUtils.loadAnimation(this,R.anim.lift_from_bottom);
        anim_appear= AnimationUtils.loadAnimation(this,R.anim.appear);
        splashimg=findViewById(R.id.splash);
        splashimg.startAnimation(anim_splash);
       splashimg.animate().translationY(-2000).setDuration(2000).setStartDelay(4000);
        text.setVisibility(View.VISIBLE);
        text.startAnimation(anim_appear);
        text.animate().scaleXBy(2).setStartDelay(7000).setDuration(2000);
        text.animate().translationY(-2000).setDuration(3000).setStartDelay(10000);



        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(12000);
                    startActivity(new Intent(Splash.this,web_view.class));
                    finish();
                    //text.startAnimation(anim_appear);
                    //text.setVisibility(View.VISIBLE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();*/
        Thread to_web_view=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                    startActivity(new Intent(Splash.this,web_view.class));
                    //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    Animatoo.animateFade(Splash.this);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        to_web_view.start();

    }
}

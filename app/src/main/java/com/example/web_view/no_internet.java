package com.example.web_view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class no_internet extends AppCompatActivity {
    LottieAnimationView lottieannim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        //lottieannim=findViewById(R.id.animation_view);
        //lottieannim.setAnimation("no_internet.json");
        //lottieannim.loop(true);
        //lottieannim.playAnimation();
        Toast.makeText(this, "no internet", Toast.LENGTH_SHORT).show();
    }
}

package com.TimothyShuang.notesshare.notesshare_1_0_0;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class WelcomeActivity extends Activity {
    private static final String TAG = "Welcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageView logo = (ImageView)findViewById(R.id.iv_logo);
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }
        });
        logo.setAnimation(alphaAnimation);
    }

    private void redirectTo() {
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}

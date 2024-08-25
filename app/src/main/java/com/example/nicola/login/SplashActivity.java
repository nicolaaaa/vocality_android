package com.example.nicola.login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nicola.login.Login.LoginActivity;
import com.plattysoft.leonids.ParticleSystem;

import org.w3c.dom.Text;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    /**
     *
     * @      new ParticleSystem(this, 80, R.drawable.ic_menu_help, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.im2), 8);

        new ParticleSystem(this, 80, R.drawable.heart_icon, 10000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.im), 8);
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LinearLayout bg = findViewById(R.id.back);
        konf();
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    public void konf (){
        /**TextView txt = findViewById(R.id.txtView2);
        //mHandler.postDelayed(mLauncher, SPLASH_DELAY);
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, (float) 0.5,    Animation.RELATIVE_TO_SELF, (float) 0.5);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        txt.startAnimation(animation);**/

       /** new ParticleSystem(this, 50,R.drawable.heart_icon , 1000)
                .setSpeedRange(0.2f, 0.5f)
                .oneShot(txt, 50);**/

    }

    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    public void weiter(View view){
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
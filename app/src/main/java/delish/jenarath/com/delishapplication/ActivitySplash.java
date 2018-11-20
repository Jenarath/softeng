package delish.jenarath.com.delishapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by admin on 19/11/2018 AD.
 */

public class ActivitySplash extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /** Creates a count down timer, which will be expired after 5000 milliseconds */
        new CountDownTimer(3000, 1000) {

            /** This method will be invoked on finishing or expiring the timer */
            @Override
            public void onFinish() {
                /** Creates an intent to start new activity */
                Intent intent = new Intent(getBaseContext(), ActivityLoginFacebook.class);

                //memulai activity baru ketika waktu timer habis
                startActivity(intent);

                //menutup layar activity
                finish();

            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();

    }
}

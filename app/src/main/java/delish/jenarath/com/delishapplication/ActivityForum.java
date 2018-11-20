package delish.jenarath.com.delishapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by admin on 12/11/2018 AD.
 */

public class ActivityForum  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_forum);
        Button btnForum = (Button)findViewById(R.id.buttonCreate);
        Button btnBack = (Button)findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityForum.this,MainActivity.class);
                startActivity(i);
            }
        });

        btnForum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityForum.this,ActivityAddForum.class);
                startActivity(i);
            }
        });







    }



}
package delish.jenarath.com.delishapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 12/11/2018 AD.
 */

public class ActivityAddForum  extends AppCompatActivity{
    final String ServerURL = "http://128.199.170.20/softeng/add_forum.php";
    EditText inputTopic;
    EditText inputContent;
    Spinner input_type;

    String topic;
    String content;
    String type;
    InputStream is=null;
    String result = null;
    String line=null;

    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddForum.this);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_forum);

        inputTopic = (EditText) findViewById(R.id.Topic_A);
        inputContent = (EditText) findViewById(R.id.Forum_A);
        input_type = (Spinner) findViewById(R.id.TypeOfForum);
        Button btnAdd = (Button) findViewById(R.id.shareNow_A);
        Button btnBack = (Button)findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityAddForum.this,ActivityForum.class);
                startActivity(i);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = inputTopic.getText().toString();
                content = inputContent.getText().toString();
                type = input_type.getSelectedItem().toString();


                if(topic.equalsIgnoreCase("") || content.equalsIgnoreCase("") ){
                    adb.setTitle("Alert");
                    adb.setMessage("Please Insert Your Data");
                    adb.setPositiveButton("Back",null);
                    AlertDialog ad = adb.create();
                    ad.show();

                    //Toast.makeText(create_forum.this,"Please Insert Your Data", Toast.LENGTH_SHORT).show();
                }else{
                    new sendData().execute();
                }
            }
        });

    }
    public class sendData extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        final AlertDialog.Builder adb = new AlertDialog.Builder(ActivityAddForum.this);

        // show progress dialog
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            dialog= ProgressDialog.show(ActivityAddForum.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            // send data to server and store result to variable
            result = getRequest(topic, content,type);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            // if finish, dismis progress dialog and show toast message
            adb.setTitle("Alert");
            adb.setMessage("Your Forum has been Added");
            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(ActivityAddForum.this,ActivityForum.class);
                    startActivity(i);
                }
            });
            AlertDialog ad = adb.create();
            ad.show();

            //Toast.makeText(create_forum.this,"Add Data Successfully. ",Toast.LENGTH_SHORT).show();
            dialog.dismiss();




        }
    }


    // method to post data to server
    public String getRequest(String topic, String content,String type){
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(ServerURL);


        try{
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("topic", topic));
            nameValuePairs.add(new BasicNameValuePair("content", content));
            nameValuePairs.add(new BasicNameValuePair("type",type));

            request.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = client.execute(request);
            result = request(response);

        }catch(Exception ex){
            result = "Unable to connect.";
        }

        return result;
    }

    public String request(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();

        }catch(Exception ex){
            result = "Error";
        }
        return result;
    }

}

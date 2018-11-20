package delish.jenarath.com.delishapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

/**
 * Created by Jenarath on 14/11/2018 AD.
 */

public class ActivityMenuDetail extends AppCompatActivity {


    ImageView imgPrevieww;
    TextView txtFoodName, txtRecipe,txtIng;
    TextView txtAlert;
    String Image, FoodName, Recipe, Ingredients;
    long MenuID;
    String MenuDetailAPI,colors = "";
    boolean check = false;



    int IOConnect = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        Button btnBack = (Button)findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityMenuDetail.this,ActivityMenuList.class);
                startActivity(i);
            }
        });


        final ImageView fav = (ImageView) findViewById(R.id.fav) ;

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!check) {
                    check = true;
                    fav.setImageResource(R.drawable.ic_favorite_after);
                } else {
                    check = false;
                    fav.setImageResource(R.drawable.ic_favorite_before);
                }

            }

        });


//        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle("");

        imgPrevieww = (ImageView) findViewById(R.id.foodimg);
        txtFoodName = (TextView) findViewById(R.id.foodname); // food name
        txtRecipe = (TextView) findViewById(R.id.recipe); // food recipe
        txtIng = (TextView) findViewById(R.id.ingre); // food recipe



        Intent iGet = getIntent();

        MenuID = iGet.getLongExtra("menu_id", 0);
        System.out.print("MenuID  ====" + MenuID);


        // Menu detail API url
        MenuDetailAPI = "http://128.199.170.20/softeng/get_menu_detail.php"+"?menu_id="+ MenuID;


        // call asynctask class to request data from server
        new getDataTask().execute();


    }



    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void> {

        // show progressbar first
        getDataTask() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            System.out.print("FoodName =" +FoodName);
            System.out.print("Image =" +Image);
            System.out.print("\nRecipe =" +Recipe);

            if((FoodName!=null) && IOConnect == 0) {
//                coordinatorLayout.setVisibility(View.VISIBLE);

                Picasso.with(getApplicationContext()).load("http://128.199.170.20/softeng" + "/" + Image).into(imgPrevieww);

//                Picasso.with(getApplicationContext()).load("http://128.199.170.20/softeng/"+Image).into(imgPrevieww, new com.squareup.picasso.Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Bitmap bitmap = ((BitmapDrawable) imgPrevieww.getDrawable()).getBitmap();
//                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//                            @Override
//                            public void onGenerated(Palette palette) {
//                            }
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//
//                });

                txtFoodName.setText(FoodName);
                txtRecipe.setText(Recipe);
                txtIng.setText(Ingredients);
            }
            else {
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    // method to parse json data from server
    public void parseJSONData(){


        try {
            // request data from Category API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(MenuDetailAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            String str = "";
            while ((line = in.readLine()) != null){
                str += line;
            }

            // parse json data and store into arraylist variables
            JSONObject json = new JSONObject(str);
            JSONArray data = json.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                JSONObject menu = object.getJSONObject("Menu_detail");

                FoodName = menu.getString("FoodName");
                Recipe = menu.getString("Recipe");
                Ingredients = menu.getString("Ingredients");
                Image = menu.getString("Image");


            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            IOConnect = 1;
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // close database before back to previous page


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //imageLoader.clearCache();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

}

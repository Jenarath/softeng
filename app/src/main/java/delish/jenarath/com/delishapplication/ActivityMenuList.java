package delish.jenarath.com.delishapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Jenarath on 14/11/2018 AD.
 */

public class ActivityMenuList extends AppCompatActivity {

    GridView listMenu;
    ProgressBar prgLoading;
    TextView txtAlert;
    SwipeRefreshLayout swipeRefreshLayout = null;
    AdapterMenuList cla;
    public static ArrayList<Long> MenuID = new ArrayList<Long>();
    public static ArrayList<String> FoodName = new ArrayList<String>();
    public static ArrayList<String> Image = new ArrayList<String>();

    String MenuAPI;
    int IOConnect = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Button btnBack = (Button)findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityMenuList.this,MainActivity.class);
                startActivity(i);
            }
        });

        Button btnSearch = (Button)findViewById(R.id.buttonSearch);
        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityMenuList.this,ActivityCatagory.class);
                startActivity(i);
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listMenu = (GridView) findViewById(R.id.listCategory);
        txtAlert = (TextView) findViewById(R.id.txtAlert);

        cla = new AdapterMenuList(ActivityMenuList.this);

        // Menu API url
        MenuAPI = "http://128.199.170.20/softeng/get_all_menu.php";

        // call asynctask class to request data from server
        new getDataTask().execute();

        // event listener to handle list when clicked
        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu page
                Intent iMenuList = new Intent(ActivityMenuList.this, ActivityMenuList.class);
                iMenuList.putExtra("MenuID", MenuID.get(position));
                iMenuList.putExtra("FoodName", FoodName.get(position));
                startActivity(iMenuList);
            }
        });

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        IOConnect = 0;
                        listMenu.invalidateViews();
                        clearData();
                        new getDataTask().execute();
                    }
                }, 3000);
            }
        });

        listMenu.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (listMenu != null && listMenu.getChildCount() > 0) {
                    boolean firstItemVisible = listMenu.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = listMenu.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });

        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu page
                Intent iMenuList = new Intent(ActivityMenuList.this, ActivityMenuDetail.class);
                iMenuList.putExtra("menu_id", MenuID.get(position));
                startActivity(iMenuList);
            }
        });

    }



    // clear arraylist variables before used
    void clearData(){
        MenuID.clear();
        FoodName.clear();
        Image.clear();
    }

    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{

        // show progressbar first
        getDataTask(){
            if(!prgLoading.isShown()){
                prgLoading.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
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
            // TODO Auto-generated method stub
            // when finish parsing, hide progressbar
            prgLoading.setVisibility(View.GONE);

            // if internet connection and data available show data on list
            // otherwise, show alert text
            if((MenuID.size() > 0) && (IOConnect == 0)){
                listMenu.setVisibility(View.VISIBLE);
                listMenu.setAdapter(cla);
            }else{
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    // method to parse json data from server
    public void parseJSONData(){

        clearData();

        try {
            // request data from Category API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(MenuAPI);
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

                JSONObject category = object.getJSONObject("Menus");

                MenuID.add(Long.parseLong(category.getString("MenuID")));
                FoodName.add(category.getString("FoodName"));
                Image.add(category.getString("Image"));
                Log.d("listMenu name", Image.get(i));

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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        //cla.imageLoader.clearCache();
        listMenu.setAdapter(null);
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig)
    {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }



}
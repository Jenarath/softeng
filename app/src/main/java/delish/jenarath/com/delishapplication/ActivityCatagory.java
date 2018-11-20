package delish.jenarath.com.delishapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 17/11/2018 AD.
 */

public class ActivityCatagory extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listMenu;
    ProgressBar prgLoading;
    SwipeRefreshLayout swipeRefreshLayout = null;
    EditText edtKeyword;
    Button btnSearch;
    TextView txtAlert,txttest;
    Spinner spinner;
    private String URL_CATEGORIES = "http://128.199.170.20/softeng/get_menu_catagory.php";
    private String URL_MENU = "http://10.0.2.2/delish/search/getAllMenu.php";


    AdapterMenuCatagory mla;


    public static ArrayList<Long> MenuID = new ArrayList<Long>();
    public static ArrayList<String> FoodName = new ArrayList<String>();
    public static ArrayList<String> Image = new ArrayList<String>();


    String MenuAPI;
    String TaxCurrencyAPI;
    int IOConnect = 0;

    private ArrayList<Category> categoriesList;

    String Keyword;
    String CatagoryAPI = "http://128.199.170.20/softeng/get_all_catagory.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        listMenu = (ListView) findViewById(R.id.listMenu);
        edtKeyword = (EditText) findViewById(R.id.edtKeyword);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        spinner = (Spinner) findViewById(R.id.spinner);
        txttest = (TextView) findViewById(R.id.toolbar_title);

        categoriesList = new ArrayList<Category>();
        Category allCat = new Category(0,"all");
        categoriesList.add(allCat);

        spinner.setOnItemSelectedListener(this);


        Button btnBack = (Button)findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(ActivityCatagory.this,MainActivity.class);
                startActivity(i);
            }
        });

        mla = new AdapterMenuCatagory(ActivityCatagory.this);
        new GetCategories().execute();

        // event listener to handle search button when clicked
        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // get keyword and send it to server
                try {

                    System.out.print("KeywordKeyword = " +Keyword);
                    Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//                MenuAPI += "?keyword=" + Keyword;
                MenuAPI = "http://128.199.170.20/softeng/get_menu_catagory.php?keyword="+Keyword;
                IOConnect = 0;
                listMenu.invalidateViews();
                clearData();
                new getDataTask().execute();
            }
        });

        // event listener to handle list when clicked
        listMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                // go to menu detail page
                Intent iDetail = new Intent(ActivityCatagory.this, ActivityMenuDetail.class);
                iDetail.putExtra("menu_id", MenuID.get(position));
                startActivity(iDetail);
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

    }

    private void populateSpinner() {

        List<String> lables = new ArrayList<String>();

        System.out.print(" categoriesList " + categoriesList.size());

        for (int i = 0; i < categoriesList.size(); i++) {
            lables.add(categoriesList.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(spinnerAdapter);
    }


    // clear arraylist variables before used
    void clearData(){
        MenuID.clear();
        FoodName.clear();
        Image.clear();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        MenuAPI = "http://128.199.170.20/softeng/get_menu_catagory.php?category_id=" + categoriesList.get(i).getId();
        IOConnect = 0;
        listMenu.invalidateViews();
        new getDataTask().execute();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class GetCategories extends AsyncTask<Void, Void, Void> {

        GetCategories(){

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONCatagoryData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateSpinner();
        }

    }

    public class getDataTask extends AsyncTask<Void, Void, Void> {

        // show progressbar first
        getDataTask(){
//            if(!prgLoading.isShown()){
//                prgLoading.setVisibility(View.VISIBLE);
//                txtAlert.setVisibility(View.GONE);
//            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            // parse json data from server in background
            parseJSONData();
//            parseJSONCatagoryData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub

            if(MenuID.size() > 0){
                listMenu.setVisibility(View.VISIBLE);
                listMenu.setAdapter(mla);
            }
            else{
                txtAlert.setVisibility(View.VISIBLE);
            }

        }
    }

    // method to parse json data from server
    public void parseJSONCatagoryData(){


        try {
            // request data from Category API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(CatagoryAPI);
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

                JSONObject categoryy = object.getJSONObject("Category");
//
//                CatagoryID.add(Long.parseLong(categoryy.getString("CategoryID")));
//                catagoryName.add(categoryy.getString("categoryName"));

                Category cat = new Category(categoryy.getInt("CategoryID"),
                        categoryy.getString("categoryName"));
//                Log.d("listMenu name", .get(i));
                categoriesList.add(cat);


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

                JSONObject category = object.getJSONObject("Menu");

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
        //mla.imageLoader.clearCache();
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



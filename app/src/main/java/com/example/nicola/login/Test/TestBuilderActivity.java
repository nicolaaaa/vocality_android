package com.example.nicola.login.Test;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TestBuilderActivity extends AppCompatActivity {

    ArrayList<String> selected;
    int type = 0;
    boolean filter = false;
    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    private static String url_all_listen = "http://"+ LoginActivity.ip +"/android_vocabeln/get_all_listen.php";
    static String kursname;
    JSONArray products = null;
    ArrayList<HashMap<String, String>> productsList;
    ArrayList<String> List;
    private Random randomGenerator;
    ArrayList<HashMap<String, String>> vocList;
    ArrayList<HashMap<String, String>> testItems;
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();
    int number;
    boolean error=false;

    private static final String TAG_NAME = "name";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PID = "pid";
    private static final String TAG_LISTEN = "listen";
    private static final String TAG_KURS = "kurs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_test_builder);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_test_builder);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Test");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainSchulerActivity.class));
            }
        });


        List = new ArrayList<String>();
        selected = new ArrayList<String>();

        Intent intent = getIntent();
        kursname = Database.kurs;

        productsList = new ArrayList<HashMap<String, String>>();

        new TestBuilderActivity.LoadAllProducts().execute();

    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("new", "start loading products");
            pDialog = new ProgressDialog(TestBuilderActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            if (Database.getInstance().getList(kursname)==null) {

            Log.i("new", "start http_request");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = new JSONObject();

                    Log.i("new", "start listen_http_request");
                    try {
                        json = jParser.makeHttpRequest(url_all_listen, "GET", params);
                    }
                    catch (Exception e){
                        TestBuilderActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {

                                Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                                error=true;

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(TestBuilderActivity.this);
                                builder1.setMessage("There are some connection problems, please connect your phone to the internet or try again later!");
                                builder1.setTitle("Error");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                // Set title divider color
                                int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
                                View titleDivider = alert11.findViewById(titleDividerId);
                                if (titleDivider != null)
                                    titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                //Do your UI operations like dialog opening or Toast here
                            }
                        });
                    }
                    // Check your log cat for JSON reponse
                    Log.d("Listen: ", json.toString());

                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            // products found
                            // Getting Array of Products
                            products = json.getJSONArray(TAG_LISTEN);

                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_KURS).equals(kursname)) {


                                    // Storing each json item in variable
                                    String id = c.getString(TAG_PID);
                                    String name = c.getString(TAG_NAME);
                                    String kurs = c.getString(TAG_KURS);


                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value
                                    map.put(TAG_PID, id);
                                    map.put(TAG_NAME, name);
                                    map.put(TAG_KURS, kurs);

                                    // adding HashList to ArrayList
                                    productsList.add(map);
                                }
                            }
                        } else {


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                Database.getInstance().addList(kursname,productsList);
                Log.i("TestBuilder LOAD", "download");
            }else {
                productsList = Database.getInstance().getList(kursname);
                Log.i("TestBuilder LOAD", "aus db");
                Log.i("TestBuilder LOAD", productsList.toString());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    for (int i=0; i<productsList.size(); i++){
                        List.add(productsList.get(i).get(TAG_NAME));
                        Log.i("TestBuilder List",
                                List.toString());
                    }
                    final ArrayAdapter <String> adapter;
                    adapter = new ArrayAdapter <String>(TestBuilderActivity.this, android.R.layout.simple_list_item_multiple_choice, List);
                    final ListView listview = findViewById(R.id.vocList);
                    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listview.setAdapter(adapter);
                    Log.i("ListenAuswahl",List.toString());

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        ArrayList <Integer> counter = new ArrayList <>();

                        @Override
                        public void onItemClick(AdapterView <?> parent, final View view, int position, long id) {
                            int cntChoice = listview.getCount();

                            SparseBooleanArray sparseBooleanArray = listview.getCheckedItemPositions();

                            int i = 0;

                            while (i < cntChoice) {
                                if (sparseBooleanArray.get(i)) {
                                    if(!counter.contains(i)) {
                                        selected.add(productsList.get(i).get("pid"));
                                        counter.add(i);
                                        Log.i("selected",selected.toString());
                                    }

                                }
                                i++;
                            }
                        }
                    });
                }
            });
        }
    }


    public void startTest(View view){
        Spinner sp = findViewById(R.id.modelSpinner);
        number = Integer.parseInt(sp.getItemAtPosition(sp.getSelectedItemPosition()).toString());
        if (selected.isEmpty()){
            Toast toast = Toast.makeText(TestBuilderActivity.this,"please select a list",Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            vocList = new ArrayList <HashMap <String, String>>();
            testItems = new ArrayList <HashMap <String, String>>();
            randomGenerator = new Random();
            new Load().execute();
        }
    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            for (int i=0; i<selected.size();i++) {
                Database.liste = selected.get(i);
                try {
                    vocList.addAll(loader.loadData("vokabel", ""));
                }catch (Exception e){
                    TestBuilderActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                            error=true;

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(TestBuilderActivity.this);
                            builder1.setMessage("There are some connection problems, please connect your phone to the internet or try again later!");
                            builder1.setTitle("Error");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            // Set title divider color
                            int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
                            View titleDivider = alert11.findViewById(titleDividerId);
                            if (titleDivider != null)
                                titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            //Do your UI operations like dialog opening or Toast here
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error == false) {
                Log.i("vokabeln", vocList.toString());

                int index = 0;
                int min = java.lang.Math.min(number, vocList.size());
                for (int i = 0; i < min; i++) {
                    index = randomGenerator.nextInt(vocList.size());
                    testItems.add(vocList.get(index));
                    vocList.remove(index);
                }

                Log.i("vokabeln zu viel", vocList.toString());
                Log.i("testitems", testItems.toString());
                Intent intent = new Intent(TestBuilderActivity.this, NewTestActivity.class);
                intent.putExtra("items", testItems);
                startActivity(intent);

            }
        }
    }

    //Initiiere Help Button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Funktionsaufruf bei Klick auf Help Button
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.miCompose) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(TestBuilderActivity.this);
            builder1.setMessage("Schreibe die korrekte deutsche Übersetzung der Vokabeln auf. Achte hierbei auf Groß- und Kleinschreibung!");
            builder1.setTitle("Information");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            // Set title divider color
            int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = alert11.findViewById(titleDividerId);
            if (titleDivider != null)
                titleDivider.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        }
        return super.onOptionsItemSelected(item);
    }

}




package com.example.nicola.login.List;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;


public class GetVocabularyActivity extends AppCompatActivity {

    //JSON Node Names
    private static final String TAG_TUC = "tuc";
    private static final String TAG_PHRASE = "phrase";
    private static final String TAG_PHRASE_TEXT = "text";
    private static final String TAG_PHRASE_LANGUAGE = "language";
    private static final String TAG_MEANINGS= "meanings";
    private static final String TAG_MEANINGS_LANGUAGE = "language";
    private static final String TAG_MEANINGS_TEXT = "text";

    // Data JSONArray
    JSONArray users = null;
    JSONArray tip = null;

    private ProgressDialog pDialog;

    String word;
    ArrayAdapter<String> adapter;
    ArrayList<String> selected2,selected;
    ArrayList<String> listItems;

    //HashMap to keep your data
    ArrayList<HashMap<String, String>> userList, tipList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_get_vocabulary);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_get_vocabulary);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setTitle("Vokabeln suchen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),GetAllActivity.class);
                in.putExtra("type", "vokabel");
                startActivity(in);

            }
        });

        userList = new ArrayList<HashMap<String, String>>();
        tipList = new ArrayList<HashMap<String, String>>();
        Button button = findViewById(R.id.button3);
        button.setVisibility(View.VISIBLE);
        EditText editText = findViewById(R.id.wort);
        editText.setVisibility(View.VISIBLE);
        selected = new ArrayList<String>();
        selected2 = new ArrayList<String>();
        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(GetVocabularyActivity.this , android.R.layout.simple_list_item_multiple_choice, listItems);
    }

    protected void useAPI(View view) {
        EditText editText = findViewById(R.id.wort);
        word = editText.getText().toString();
        word=word.replaceAll(" ", "+");
        Log.i("GetVoc ",word);
        new API().execute(word);
    }

    protected void gibBack(View view) {
        ListView listview = findViewById(R.id.list2);
        int cntChoice = listview.getCount();
        SparseBooleanArray sparseBooleanArray = listview.getCheckedItemPositions();
        int i = 0;
        if (selected.isEmpty()){
            while (i < cntChoice) {
                if (sparseBooleanArray.get(i)) {
                    selected.add(listview.getItemAtPosition(i).toString());
                    Log.i("TIPPPPPPPPP", selected.toString());
                    Log.i("TIPPPPPPPPP", Integer.toString(i));
                }

                i++;
            }
            if (selected.isEmpty()) {
                Toast toast = Toast.makeText(GetVocabularyActivity.this, "please select a translation", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                Intent intent = new Intent();
                Log.i("se", selected.toString());
                Log.i("wo", word);
                intent.putExtra("message3", selected);
                intent.putExtra("message4", word);
                intent.putExtra("message5", selected2);
                setResult(RESULT_OK, intent);
                finish();
            }
        }else {
            while (i < cntChoice) {
                if (sparseBooleanArray.get(i)) {
                    selected2.add(listview.getItemAtPosition(i).toString());
                    Log.i("TIPPPPPPPPP", selected.toString());
                    Log.i("TIPPPPPPPPP", Integer.toString(i));
                }

                i++;

            }
            if (selected2.isEmpty()) {
                Toast toast = Toast.makeText(GetVocabularyActivity.this, "please select a translation", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                Intent intent = new Intent();
                Log.i("se", selected.toString());
                Log.i("wo", word);
                intent.putExtra("message3", selected);
                intent.putExtra("message4", word);
                intent.putExtra("message5", selected2);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    class API extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GetAll", "start loading products");
            pDialog = new ProgressDialog(GetVocabularyActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... urls) {
            String lang = "eng";
            String API_URL = "https://glosbe.com/gapi/translate?from="+lang+"&dest=de&format=json&phrase=";
            String API_KEY = "&pretty=true";
            word = urls[0];
            Log.i("INFO", API_URL + word + API_KEY);

            try {
                URL url = new URL(API_URL + word + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    //Log.i("INFO", stringBuilder.toString());
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            pDialog.dismiss();
            TextView textView = findViewById(R.id.translation);
            if (response == null) {
                response = "THERE WAS AN ERROR";
                Log.e("ERROR", "help");
                Button but = findViewById(R.id.ownButton);
                but.setVisibility(View.VISIBLE);
            }
           // Log.i("INFO", response);


            if (response != null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    //Get Json Array Node
                    users = jsonObj.getJSONArray(TAG_TUC);
                    //Log.i("MAP", users.toString());

                    // Looping through all data
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject u = users.getJSONObject(i);

                        if (u.has(TAG_PHRASE)) {

                            JSONObject phrase = u.getJSONObject(TAG_PHRASE);

                            String trans_text = phrase.getString(TAG_PHRASE_TEXT);
                            String trans_lang = phrase.getString(TAG_PHRASE_LANGUAGE);

                            if (!u.has(TAG_MEANINGS)) {
                            } else {
                                tip = u.getJSONArray(TAG_MEANINGS);
                                //Log.i("MEAN", tip.toString());
                                for (int j = 0; j < tip.length(); j++) {
                                    JSONObject t = tip.getJSONObject(j);

                                    HashMap<String, String> meaning = new HashMap<String, String>();

                                    String tip_lang = t.getString(TAG_MEANINGS_LANGUAGE);
                                    String tip_text = t.getString(TAG_MEANINGS_TEXT);
                                    //Log.i("MEAN", tip_text.toString());

                                    meaning.put(TAG_MEANINGS_TEXT, tip_text);
                                    meaning.put(TAG_MEANINGS_LANGUAGE, tip_lang);

                                    Log.i("TIP", meaning.toString());
                                    tipList.add(meaning);
                                }
                            }
                            // Temporary HashMap for single data
                            HashMap<String, String> user = new HashMap<String, String>();

                            // Adding each child node to Hashmap key -> value
                            user.put(TAG_PHRASE_TEXT, trans_text);
                            user.put(TAG_PHRASE_LANGUAGE, trans_lang);


                            Log.i("MAP", user.toString());
                            //System.out.println(user);
                            //Adding user to userList
                            userList.add(user);
                        }
                    }
                    if (userList.size() == 0) {
                        textView.setText("no result");
                        Button but = findViewById(R.id.ownButton);
                        but.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView.setText("wählen Sie passende Ergebnisse aus");


                        for (int i = 0; i < userList.size(); ++i) {
                            listItems.add(userList.get(i).get("text"));
                        }

                        final ListView listview = findViewById(R.id.list2);
                        listview.setVisibility(View.VISIBLE);
                        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        adapter.notifyDataSetChanged();
                        listview.setAdapter(adapter);

                        Button buton = findViewById(R.id.OKButton);
                        buton.setVisibility(View.VISIBLE);


                    }
                    Button but = findViewById(R.id.ownButton);
                    but.setVisibility(View.VISIBLE);
                    hideKeyboard(GetVocabularyActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("no results EXC");
                }

            }


        }
    }

    public void example(View view){
        ListView listview = findViewById(R.id.list2);
        int cntChoice = listview.getCount();
        SparseBooleanArray sparseBooleanArray = listview.getCheckedItemPositions();
        int i = 0;
        while (i < cntChoice) {
            if (sparseBooleanArray.get(i)) {
                selected.add(listview.getItemAtPosition(i).toString());
                Log.i("TIPPPPPPPPP", selected.toString());
                Log.i("TIPPPPPPPPP", Integer.toString(i));
            }

            i++;
        }
        if (selected.isEmpty()){
            Toast toast = Toast.makeText(GetVocabularyActivity.this,"please select a translation",Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            new ExAPI().execute();
        }
    }

    public void ownTranslation(View view){
        EditText editText = findViewById(R.id.wort);
        word = editText.getText().toString();
        setContentView(R.layout.activity_new);

        Button btnCreateProduct = findViewById(R.id.btnCreateProduct);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                EditText inputName = findViewById(R.id.inputName);
                selected.clear();
                selected.add(inputName.getText().toString());
                Intent intent = new Intent();
                Log.i("se",selected.toString());
                word = word.replace("\\+"," ");
                Log.i("wo",word);
                intent.putExtra("message3", selected);
                intent.putExtra("message4", word);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class ExAPI extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GetAll", "start loading products");
            pDialog = new ProgressDialog(GetVocabularyActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... urls) {
            String lang = Database.language;
            String API_URL = "https://glosbe.com/gapi/tm?from="+lang+"&dest=de&format=json&phrase=";
            String API_KEY = "&page=1&pretty=true";
            Log.i("INFO", API_URL + word + API_KEY);

            try {
                URL url = new URL(API_URL + word + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            pDialog.dismiss();
            TextView textView = findViewById(R.id.translation);
            userList.clear();
            if (response == null) {
                response = "THERE WAS AN ERROR";
                Log.e("ERROR", "help");
                Button but = findViewById(R.id.ownButton);
                but.setVisibility(View.VISIBLE);
            }
            // Log.i("INFO", response);


            if (response != null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    //Get Json Array Node
                    users = jsonObj.getJSONArray("examples");

                    // Looping through all data
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject u = users.getJSONObject(i);

                        if (u.has("first")) {

                            String trans_text = u.getString("first");

                            // Temporary HashMap for single data
                            HashMap<String, String> user = new HashMap<String, String>();

                            // Adding each child node to Hashmap key -> value
                            user.put(TAG_PHRASE_TEXT, trans_text);

                            Log.i("MAP", user.toString());
                            //System.out.println(user);
                            //Adding user to userList
                            userList.add(user);
                        }
                    }
                    if (userList.size() == 0) {
                        textView.setText("no result");
                        Button but = findViewById(R.id.ownButton);
                        but.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView.setText("wählen Sie passende Ergebnisse aus");
                        listItems.clear();

                        Log.i("Get ListItems",listItems.toString());

                        for (int i = 0; i < userList.size(); ++i) {
                            listItems.add(userList.get(i).get("text"));
                        }
                        final ListView listview = findViewById(R.id.list2);

                        adapter.notifyDataSetChanged();
                        Button button = findViewById(R.id.OKButton);
                        button.setVisibility(View.VISIBLE);
                        Log.i("Get ListItems",listItems.toString());
                        listview.setAdapter(adapter);


                    }
                    Button but = findViewById(R.id.ownButton);
                    but.setVisibility(View.GONE);
                    hideKeyboard(GetVocabularyActivity.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("no results EXC");
                }

            }


        }
    }

    //Initiiere Help Button
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Beim eingeben einer englischen Vokabel werden dir vom Online Übersetzer 'Glosbe.de' "+
                    "einige Übersetzungen vorgeschlagen. Wähle passende davon aus oder gib eine eigene Übersetzung an, falls nichts passt.");
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


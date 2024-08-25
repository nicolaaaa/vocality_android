package com.example.nicola.login.Texte;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TextActivity extends AppCompatActivity {

    ArrayList <HashMap <String, String>> wortList;
    private Random randomGenerator;
    ArrayList <HashMap <String, String>> testItems;
    ArrayList <String> listItems;
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();

    int vorhanden = 0;
    JSONParser jsonParser = new JSONParser();
    int mode;
    boolean error=false;

    ArrayList <HashMap <String, String>> productsList;
    private static String url_create_frage = "http://" + LoginActivity.ip + "/android_vocabeln/create_frage.php";
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_fragen);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_fragen);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        randomGenerator = new Random();

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar t = getSupportActionBar();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TextMenuActivity.class));
            }
        });
        listItems = new ArrayList <String>();
        testItems = new ArrayList <HashMap <String, String>>();

        //Gebe Tetmode höhere Chanc als anderen Mode's
        int abw = randomGenerator.nextInt(2);
        Log.i("abwechs", Integer.toString(abw));
        if (abw == 0)
            mode = 0;
        else
            mode = randomGenerator.nextInt(3);
        Log.i("mode", Integer.toString(mode));

        //normaler Text schreiben modus
        if (mode == 0){
            TextView txt = findViewById(R.id.infoText);
            txt.setText(Html.fromHtml("Schreiben sie einen kurzen <b> Text </b> (max. 300 Zeichen) und benutzen Sie dabei möglichst viele dieser Wörter!"));
            getSupportActionBar().setTitle("Texte schreiben");
            new Load().execute();
        }
        //Fragen schreiben Modus
        else if (mode == 1){
            TextView txt = findViewById(R.id.infoText);
            txt.setText(Html.fromHtml("Formulieren Sie ein <b> Frage </b>(max. 300 Zeichen) und benutzen Sie dabei möglichst viele dieser Wörter!"));
            getSupportActionBar().setTitle("Frage formulieren");
            new Load().execute();
        }
        //Beschreiben Sie wort modus
        else if (mode == 2) {
            TextView txt = findViewById(R.id.infoText);
            txt.setText("Versuchen Sie, die Bedeutung des angegebenen Wortes auf Englisch zu Beschreiben oder geben Sie ein Beispiel an umd diese Bedeutung zu verdeutlichen (max. 300 Zeichen)!");
            getSupportActionBar().setTitle("Wort beschreiben");
            new Load().execute();
        }
    }

    class Load extends AsyncTask <String, String, String> {

        protected String doInBackground(String... args) {
            Database.liste = "kurs";
            try {
                wortList = loader.loadData("vokabel", "kurs");
            }
            catch (Exception e){
                TextActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TextActivity.this);
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(error=false) {
                Log.i("vokabeln", wortList.toString());
                int min;
                int index = 0;
                if (mode == 2) {
                    min = java.lang.Math.min(1, wortList.size());
                } else
                    min = java.lang.Math.min(3, wortList.size());
                for (int i = 0; i < min; i++) {
                    index = randomGenerator.nextInt(wortList.size());
                    testItems.add(wortList.get(index));
                    wortList.remove(index);
                }

                Log.i("vokabeln zu viel", wortList.toString());
                Log.i("testitems", testItems.toString());

                for (int i = 0; i < testItems.size(); ++i) {
                    listItems.add(testItems.get(i).get("word"));
                }

                final ArrayAdapter <String> adapter;

                adapter = new ArrayAdapter <String>(TextActivity.this, android.R.layout.simple_list_item_1, listItems);

                final ListView listview = findViewById(R.id.list3);
                listview.setAdapter(adapter);
            }
            error=false;

        }
    }

    public void check (View view){
        Button ok = findViewById(R.id.okButton);
        Button back = findViewById(R.id.backButton);
        Button weiter = findViewById(R.id.button3);
        TextView text = findViewById(R.id.checkFrage);
        TextView info = findViewById(R.id.infoText);
        ListView list = findViewById(R.id.list3);
        EditText ein = findViewById(R.id.frage);
        String in = ein.getText().toString();
        ok.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        weiter.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        ein.setVisibility(View.GONE);
        text.setText(in);
    }

    public void back (View view){
        Button ok = findViewById(R.id.okButton);
        Button back = findViewById(R.id.backButton);
        Button weiter = findViewById(R.id.button3);
        TextView text = findViewById(R.id.checkFrage);
        TextView info = findViewById(R.id.infoText);
        ListView list = findViewById(R.id.list3);
        EditText ein = findViewById(R.id.frage);
        ok.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        weiter.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
        ein.setVisibility(View.VISIBLE);
    }



    public void saveFrage(View view) {
        EditText eingabe = findViewById(R.id.frage);
        String ein = eingabe.getText().toString();
        Log.i("Fragen", ein);
        if (mode == 0) {
            if (ein.contains(".")) {
                for (int i = 0; i < testItems.size(); i++) {
                    Log.i("Frage Worte", testItems.get(i).get("word"));
                    if (testItems.get(i).get("word").contains(" ")) {
                        Log.i("Wort mit Leerzeichen", testItems.get(i).get("word"));
                        String result[] = testItems.get(i).get("word").split(" ");
                        int j, x=0, v=0;
                        for (j = 0; j < result.length; j++) {
                            Log.i("split ", result[j]);
                            v+=1;
                            if (ein.contains(result[j])) {
                                Log.i("split found", result[j]);
                                x += 1;
                                Log.i("x = ", Integer.toString(x));
                            }
                        }
                        Log.i("v = ", Integer.toString(v));
                        if (x == v){
                            Log.i("Frage benutze Worte", testItems.get(i).get("word"));
                            vorhanden += 1;
                            Log.i("benutze Worte Punkte", Integer.toString(vorhanden));
                        }
                    }
                    else {
                        if (ein.contains(testItems.get(i).get("word").substring(0,testItems.get(i).get("word").length()-1))) {
                            Log.i("Frage benutze Worte", testItems.get(i).get("word").substring(0,testItems.get(i).get("word").length()-1));
                            vorhanden += 1;
                            Log.i("benutze Worte Punkte", Integer.toString(vorhanden));
                        }
                    }
                }
                Toast toast = Toast.makeText(this, Integer.toString(vorhanden) + " von " + Integer.toString(testItems.size()) + " Wörter benutzt", Toast.LENGTH_SHORT);
                toast.show();
                new Save().execute();
            } else {
                Toast toast = Toast.makeText(this, "Beenden sie ihren satz mit einem Punkt", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
        else if (mode == 1) {
            if (ein.contains("?")) {
                for (int i = 0; i < testItems.size(); i++) {
                    Log.i("Frage Worte", testItems.get(i).get("word"));
                    if (testItems.get(i).get("word").contains(" ")){
                        Log.i("Wort mit Leerzeichen", testItems.get(i).get("word"));
                        String result[] =testItems.get(i).get("word").split(" ");
                        int j, x=0, v=0;
                        for (j = 0; j < result.length; j++) {
                            Log.i("split ", result[j]);
                            v+=1;
                            if (ein.contains(result[j])) {
                                Log.i("split found", result[j]);
                                x += 1;
                                Log.i("x = ", Integer.toString(x));
                            }
                    }
                    Log.i("v = ", Integer.toString(v));
                    if (x == v){
                        Log.i("Frage benutze Worte", testItems.get(i).get("word"));
                        vorhanden += 1;
                        Log.i("benutze Worte Punkte", Integer.toString(vorhanden));
                        }
                    }
                    else {
                        if (ein.contains(testItems.get(i).get("word").substring(0,testItems.get(i).get("word").length()-1))) {
                        Log.i("Frage benutze Worte", testItems.get(i).get("word").substring(0,testItems.get(i).get("word").length()-1));
                        vorhanden += 1;
                        Log.i("benutze Worte Punkte", Integer.toString(vorhanden));
                        }
                    }
                }
                Toast toast = Toast.makeText(this, Integer.toString(vorhanden) + " von " + Integer.toString(testItems.size()) + " Wörter benutzt", Toast.LENGTH_SHORT);
                toast.show();
                new Save().execute();
            } else {
                Toast toast = Toast.makeText(this, "Beenden Sie Ihre Frage mit einem Fragezeichen!", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
        else if (mode == 2) {
            if (ein.contains(".")) {
                new Save().execute();
            } else {
                Toast toast = Toast.makeText(this, "Beenden Sie Ihren Satz mit einem Punkt!", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
    }

    public class Save extends AsyncTask <String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TextActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            JSONObject json = new JSONObject();
            List <NameValuePair> params = new ArrayList <NameValuePair>();
            EditText eingabe = findViewById(R.id.frage);
            String user = Database.userMail;
            String ein = eingabe.getText().toString();


            // Building Parameters
            params.add(new BasicNameValuePair("user", user));
            params.add(new BasicNameValuePair("satz", ein));
            params.add(new BasicNameValuePair("extra", Integer.toString(vorhanden)));
            params.add(new BasicNameValuePair("Textart", Integer.toString(mode)));
            Log.i("Create Response", params.toString());


            // getting JSON Object
            // Note that create product url accepts POST method
            try {
                json = jsonParser.makeHttpRequest(url_create_frage,
                        "POST", params);
            }catch (Exception e){}


            Log.i("Create Response", url_create_frage);
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                Log.i("SUCCESS", Integer.toString(success));


                if (success == 1) {
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            Intent in = new Intent(getApplicationContext(), AnswerActivity.class);
            startActivity(in);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(TextActivity.this);
            builder1.setMessage("Bearbeite die oben angegebene Aufgabe!" +
                    "Du erhälts anschließend Punkte, sobald ein anderer User deine Antwort bewertet hat. " +
                    "Abholbar sind diese unter 'Meine Texte' im Bereich 'Punkte abholen'. " +
                    "Die Punktzahl ist abhängig davon, ob dein Text korrekt ist, wie viele der angegebene Wörter du benutzt hast" +
                    ", wie kreativ und komplex deine ANtwort war und wie viele Wörter dein Text insgesamt lang war.");
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
package com.example.nicola.login.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;

    static String liste;
    static String language;
    static String type;
    static String kurs;
    static String email;
    static String password;
    static String ty;
    static String nam;



    // url to create new product
    private static String url_create_vocabel = "http://"+ LoginActivity.ip+"/android_vocabeln/create_vocabel.php";
    private static String url_create_liste = "http://"+ LoginActivity.ip+"/android_vocabeln/create_liste.php";
    private static String url_create_kurs = "http://"+ LoginActivity.ip+"/android_vocabeln/create_kurs.php";
    private static String url_create_student = "http://"+ LoginActivity.ip+"/android_vocabeln/create_student.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    static String word;
    static ArrayList<String> translation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_new);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_new);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        // Edit Text
        inputName = findViewById(R.id.inputName);
        Intent intent= getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("liste")){
            kurs = Database.kurs;
        } else if (type.equals("vokabel")){
            liste = Database.liste;
            language = "eng";
            intent = new Intent(this, GetVocabularyActivity.class);
            startActivityForResult(intent, 555);
        }
        if (!(type.equals("student"))) {
            setContentView(R.layout.activity_new);
        }

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New " + type);

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainLehrerActivity.class));
            }
        });

        if(type.equals("kurs")){
            Spinner sp = findViewById(R.id.langSpinner);
            TextView tv = findViewById(R.id.langText);
            tv.setVisibility(View.VISIBLE);
            sp.setVisibility(View.VISIBLE);
        }

        // Create button
        Button btnCreateProduct = findViewById(R.id.btnCreateProduct);

        // button click event
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewProduct().execute();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        word = data.getStringExtra("message4");
        translation = data.getStringArrayListExtra("message3");
        new CreateNewProduct().execute();
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            JSONObject json = new JSONObject();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String name;
            inputName = findViewById(R.id.inputName);
            Spinner sp = findViewById(R.id.langSpinner);

            switch (type) {
                case "kurs":

                    name = inputName.getText().toString();
                    String lang = sp.getItemAtPosition(sp.getSelectedItemPosition()).toString();

                    // Building Parameters
                    params.add(new BasicNameValuePair("name", name));
                    params.add(new BasicNameValuePair("language", lang));
                    params.add(new BasicNameValuePair("lehrer", Database.userMail));
                    Log.i("Create Response", params.toString());

                    // getting JSON Object
                    try {
                        json = jsonParser.makeHttpRequest(url_create_kurs,
                                "POST", params);
                    }
                    catch (Exception e){}
                    Log.i("Create Response", url_create_kurs);
                    // check for success tag
                    try {
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));


                        if (success == 1) {
                            Database.getInstance().deleteKurs();
                            // successfully created product
                            Intent i = new Intent(getApplicationContext(), GetAllActivity.class);
                            i.putExtra("type",type);
                            startActivity(i);

                            // closing this screen
                            finish();
                        } else {
                            // failed to create product
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case "liste":
                    name = inputName.getText().toString();

                    // Building Parameters
                    params.add(new BasicNameValuePair("name", name));
                    params.add(new BasicNameValuePair("kurs", kurs));
                    Log.i("Create Response", params.toString());


                    // getting JSON Object
                    // Note that create product url accepts POST method
                    try {
                        json = jsonParser.makeHttpRequest(url_create_liste,
                                "POST", params);
                    }
                    catch (Exception e){}
                    Log.i("Create Response", url_create_liste);

                    try {
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));


                        if (success == 1) {
                            Database.getInstance().deleteList(kurs);
                            // successfully created product
                            Intent i = new Intent(getApplicationContext(), GetAllActivity.class);
                            i.putExtra("type",type);
                            i.putExtra("var",kurs);
                            startActivity(i);

                            // closing this screen
                            finish();
                        } else {
                            // failed to create product
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case "vokabel":

                    String trans = translation.get(0);
                    for (int i = 1; i<translation.size();i++){
                        trans += ",";
                        trans += translation.get(i);
                    }

                    word=word.replaceAll("\\+", " ");


                // Building Parameters
                params.add(new BasicNameValuePair("word", word));
                params.add(new BasicNameValuePair("translation", trans));
                params.add(new BasicNameValuePair("description", Database.userKurs));
                params.add(new BasicNameValuePair("language", language));
                params.add(new BasicNameValuePair("liste", liste));
                Log.i("Create Response", params.toString());


                // getting JSON Object
                // Note that create product url accepts POST method
                try{
                    json = jsonParser.makeHttpRequest(url_create_vocabel,
                            "POST", params);
                }catch (Exception e){}
                Log.i("Create Response", url_create_vocabel);

                // check log cat fro response
                //  Log.i("Create Response", json.toString());


                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    Log.i("SUCCESS", Integer.toString(success));


                    if (success == 1) {
                        Database.getInstance().deleteVoc(liste);
                        // successfully created product
                        Intent i = new Intent(getApplicationContext(), GetAllActivity.class);
                        i.putExtra("type", type);
                        i.putExtra("var", liste);
                        startActivity(i);

                        // closing this screen
                        finish();
                    } else {
                        // failed to create product
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
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
            builder1.setMessage("Erstelle hier eine neue Vokabelliste für die Schüler deines Kurses!");
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

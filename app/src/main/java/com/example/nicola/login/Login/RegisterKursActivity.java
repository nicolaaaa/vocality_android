package com.example.nicola.login.Login;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterKursActivity extends AppCompatActivity{

    ArrayList<HashMap<String, String>> testItems;
    ArrayList<String> listItems;
    com.example.nicola.login.UpdateProducts updater = new com.example.nicola.login.UpdateProducts();
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";

    String user;
    String regKurs;
    String type;
    boolean error=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_register_kurs);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_register_kurs);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        user = Database.userMail;

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        //Überprüfe, ob registrierender User Lehrer oder Schüler
        //Bei Schüler, fordere zum einschreiben in einen der bereits in Datenbank vorhandenen Kurse auf
        if (type.equals("student")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Schreibe dich in einen Kurs ein!");

            //Bei Klick auf Home Button gehe zurück zu registrierung
            myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            });

            listItems = new ArrayList <String>();
            testItems = new ArrayList <HashMap <String, String>>();

            //Ändere Layout von Textfeld und Button auf Listview
            ListView lv = findViewById(R.id.list);
            lv.setVisibility(View.VISIBLE);
            EditText txt = findViewById(R.id.editText);
            txt.setVisibility(View.GONE);
            Button btn = findViewById(R.id.button);
            btn.setVisibility(View.GONE);

            //Lade vorhandene Kurse aus Datenbank
            new Load().execute();

            //Bei Klick auf einen Kurs, schreibe Schüler für diesen ein -> Update Schüler Information
            lv = findViewById(R.id.list);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView <?> parent, View view,
                                        int position, long id) {

                    ListView lv = findViewById(R.id.list);
                    regKurs = lv.getItemAtPosition(position).toString();
                    Log.i("onItmeI", user);
                    Log.i("onItmeI", regKurs);

                    new Update().execute();

                }
            });
        }
        else{if(type.equals("lehrer")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Erstelle einen Kurs!");

            myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            });

            //Ändere Layout von Listview auf Textfeld und Button
            ListView lv = findViewById(R.id.list);
            lv.setVisibility(View.GONE);
            EditText txt = findViewById(R.id.editText);
            txt.setVisibility(View.VISIBLE);
            Button btn = findViewById(R.id.button);
            btn.setVisibility(View.VISIBLE);
        }
        }
    }

    public void Create(View view) {
        new Update().execute();
    }

        class Update extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            //Bei Schuler füge als Kurs ausgewählten hinzu
            if (type.equals("student")) {
                ArrayList <String> reg = new ArrayList <>();
                reg.add(regKurs);
                try {
                    updater.update("student", "kurs", user, reg);
                }
                catch (Exception e){
                    RegisterKursActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                            error=true;

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterKursActivity.this);
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
                if(error==false){
                Database.userKurs = regKurs;
                Intent intent = new Intent(RegisterKursActivity.this, MainSchulerActivity.class);
                startActivity(intent);
                }
            }
            //Bei Lehrer füge neu erstellten hinzu
            else{
                EditText txt = findViewById(R.id.editText);
                regKurs = txt.getText().toString();
                ArrayList <String> reg = new ArrayList <>();
                reg.add(regKurs);
                try {
                    updater.update("lehrer", "kurs", user, reg);
                }
                catch (Exception e){
                    RegisterKursActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                            error=true;

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterKursActivity.this);
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
                if(error==false) {
                    Database.userKurs = regKurs;
                    Intent intent = new Intent(RegisterKursActivity.this, MainLehrerActivity.class);
                    startActivity(intent);
                }
            }
            return null;
        }
    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            try {
                testItems = loader.loadData("kurs", "");
            }
            catch (Exception e){
                RegisterKursActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterKursActivity.this);
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

        //Nach Laden Stelle KursListe durch ListAdapter dar
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error==false) {
                Log.i("KURSE", testItems.toString());

                //Zeige nur Namen der Kurse an
                for (int i = 0; i < testItems.size(); ++i) {
                    listItems.add(testItems.get(i).get("name"));
                }
                final ArrayAdapter <String> adapter;
                adapter = new ArrayAdapter <String>(RegisterKursActivity.this, android.R.layout.simple_list_item_1, listItems);

                final ListView listview = findViewById(R.id.list);
                listview.setAdapter(adapter);
            }
            error=false;
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
            builder1.setMessage(
                    "Hier kannst du dich in einen Kurs einschreiben oder falls du Lehrer bist, "+
                    "einen neuen erstellen. Die Wahl deines Kurses bestimmt die Vokablelisten mit denen du arbeitest." +
                            " Falls du keinen Kurs hast schreibe dich z.B. in englisch101 ein :)");
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

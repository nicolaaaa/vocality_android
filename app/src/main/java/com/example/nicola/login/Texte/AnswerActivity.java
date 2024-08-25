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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AnswerActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> testItems;
    ArrayList<String> listItems;
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();
    com.example.nicola.login.UpdateProducts updater = new com.example.nicola.login.UpdateProducts();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";


    static String satz, origUser;
    String user;
    String korrekt;
    String worte;
    float kreativ;
    float komplex;
    String answer;
    String besser = "";
    boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_answer);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_answer);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }


        //Kein Zurück Pfeil auf Toolbar, User soll gezwungen werden pro geschriebenen Text einen zu bewerten
        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Texte bewerten");


        listItems = new ArrayList<String>();
        testItems = new ArrayList<HashMap<String, String>>();
        new Load().execute();

    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            Database.fragen="all";
            try {
                testItems = loader.loadData("frage", "all");
            }
            catch (Exception e){
                AnswerActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AnswerActivity.this);
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
            if (error == false) {
                Log.i("Fragen", testItems.toString());
                if (testItems.isEmpty()) {
                    back();
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ListView lv = findViewById(R.id.list4);

                            ListAdapter adapter = new SimpleAdapter(
                                    AnswerActivity.this, testItems,
                                    R.layout.list_item, new String[]{
                                    "mode", "user", "satz"},
                                    new int[]{R.id.pid, R.id.kurs, R.id.name});
                            // updating listview
                            lv.setAdapter(adapter);


                        }
                    });
                    notEmpty();
                }
            }
        }
    }

    public void back(){
        Intent in = new Intent(this, TextMenuActivity.class);
        startActivity(in);
        finish();
    }

    public void notEmpty(){

        ListView lv = findViewById(R.id.list4);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String mode = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                satz = ((TextView) view.findViewById(R.id.name)).getText().toString();
                origUser = ((TextView) view.findViewById(R.id.kurs)).getText().toString();

                if(Database.theme.equals("color")) {
                    setTheme(R.style.AppTheme_My);
                    setContentView(R.layout.frage_korrekt);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.sun_clouds));
                }
                else {
                    setTheme(R.style.AppTheme_MyDark);
                    setContentView(R.layout.frage_korrekt);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.unnamed));
                }

                if (mode.equals("0")) {
                    getSupportActionBar().setTitle("Bewerten Sie diesen Satz");
                    TextView rev = findViewById(R.id.review);
                    rev.setText("Ist der Satz grammatikalisch korrekt?");
                    TextView rev2 = findViewById(R.id.review2);
                    rev2.setText("Wurden die Vokabeln richtig gebraucht?");
                }
                if (mode.equals("1")) {
                    getSupportActionBar().setTitle("Bewerten Sie diese Frage");
                    TextView rev = findViewById(R.id.review);
                    rev.setText("Ist es eine grammatikalisch korrekte Frage?");
                    TextView rev2 = findViewById(R.id.review2);
                    rev2.setText("Wurden die Vokabeln richtig gebraucht?");
                }
                if (mode.equals("2")) {
                    getSupportActionBar().setTitle("Bewerten Sie diese Beschreibung");
                    TextView rev = findViewById(R.id.review);
                    rev.setText("Erkennen Sie das zu beschreibende Wort?");
                    TextView rev2 = findViewById(R.id.review2);
                    rev2.setText("Ist die Beschreibung/das Beispiel in Englisch?");
                }

                TextView txt = findViewById(R.id.wahlFrage);
                txt.setText(satz);

            }
        });
    }

    public void finished(View view){
        boolean check=true;
        boolean verbessern=false;
        EditText ed = findViewById(R.id.comment);
        RadioButton yes1 = findViewById(R.id.yesButton1);
        RadioButton yes2 = findViewById(R.id.yesButton2);
        RadioButton no1 = findViewById(R.id.noButton1);
        RadioButton no2 = findViewById(R.id.noButton2);
        RatingBar komp = findViewById(R.id.komplexRating);
        RatingBar krea = findViewById(R.id.kreativRating);
        answer = ed.getText().toString();
        if (yes1.isChecked())
            korrekt="true";
        else if(no1.isChecked()) {
            korrekt = "false";
            verbessern = true;
        }
        else {
            check = false;
            Toast toast = Toast.makeText(this, "please choose something", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (yes2.isChecked())
            worte="true";
        else if (no2.isChecked()) {
            worte = "false";
            verbessern = true;
        }
        else {
            check=false;
            Toast toast = Toast.makeText(this, "please choose something", Toast.LENGTH_SHORT);
            toast.show();
        }
        komplex=komp.getRating();
        if(komplex==0.0) {
            check = false;
            Toast toast = Toast.makeText(this, "please choose something", Toast.LENGTH_SHORT);
            toast.show();
        }
        kreativ=krea.getRating();
        if(kreativ==0.0) {
            check = false;
            Toast toast = Toast.makeText(this, "please choose something", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (check) {
            if (!verbessern) {
                new Answer().execute();
            }
            else{
                if(Database.theme.equals("color")) {
                    setTheme(R.style.AppTheme_My);
                    setContentView(R.layout.answer);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.sun_clouds));
                }
                else {
                    setTheme(R.style.AppTheme_MyDark);
                    setContentView(R.layout.answer);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.unnamed));
                }
                TextView txt = findViewById(R.id.falscherText);
                txt.setText(satz);
            }
        }
    }

    public void finished2 (View view){
        EditText txt = findViewById(R.id.verbesser);
        besser = txt.getText().toString();
        new Answer().execute();
    }

    class Answer extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            user = Database.userMail;
            ArrayList<String> var = new ArrayList <>();
            var.add(user);
            var.add(korrekt);
            var.add(worte);
            var.add(Float.toString(komplex));
            var.add(Float.toString(kreativ));
            var.add(answer);
            var.add(besser);
            Log.i("Answer Satz",satz);
            try {
                updater.update("frage", "all", satz, var);
            }             catch (Exception e){
                    AnswerActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                            error=true;

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(AnswerActivity.this);
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
            ArrayList<String> var2 = new ArrayList <>();
            var2.add("text");
            try {


                updater.update("student", "rep2", origUser, var2);
            }
            catch (Exception e){
                AnswerActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AnswerActivity.this);
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

        protected void onPostExecute(String s){
            if (error==false) {
                Intent inte = new Intent(AnswerActivity.this, TextMenuActivity.class);
                startActivity(inte);
            }
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Bewerte einen der gegebenen Sätze anderer User" +
                    "Nachdem der User deine Bewertung seines Satzes gesehen hat, " +
                    "gibt er an, ob das Feedback sinnvoll war oder nicht. " +
                    "Zu viele schlechte Bewertungen schaden außerdem deiner Reputation und "+
                    "können im Schlimmstfall zur Löschung deines Acounts führen."+
                    "Probiere also möglichst gutes Feedback zu geben." +
                    "Deine Punkte bekommst du anschließend unter 'Meine Reviews'. ");
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

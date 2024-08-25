package com.example.nicola.login.Texte;

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
import android.widget.LinearLayout;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;

import java.util.ArrayList;
import java.util.HashMap;

public class TextMenuActivity extends AppCompatActivity {

    ArrayList<String> listItems;
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";
    boolean error=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.fragen_menu);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.fragen_menu);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Texte");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainSchulerActivity.class));
            }
        });

        new Load().execute();
    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            try {
                productsList = loader.loadData("student", "me");
            }
            catch (Exception e){
                TextMenuActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(TextMenuActivity.this);
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
                Log.i("aktualisiertes Ich", productsList.toString());
                Log.i("aktualisiertes Rep", productsList.get(0).get("rep"));
                Button bt3 = findViewById(R.id.statistikButton);
                if (!productsList.get(0).get("rep").equals("text") && !productsList.get(0).get("rep").equals("feed")) {
                    bt3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else
                    bt3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.heart_icon, 0);
                Database.rep = productsList.get(0).get("rep");
            }
            error=false;
        }

    }

    public void getFragen(View view) {
        Intent in = new Intent(this, AnswerActivity.class);
        startActivity(in);
    }

    public void myFragen(View view) {
        Button bt1 = findViewById(R.id.testButton);
        Button bt2 = findViewById(R.id.listButton);
        Button bt3 = findViewById(R.id.statistikButton);
        Button btt1 = findViewById(R.id.frageBtn);
        Button btt2 = findViewById(R.id.revBtn);
        bt1.setVisibility(View.GONE);
        bt2.setVisibility(View.GONE);
        bt3.setVisibility(View.GONE);
        btt1.setVisibility(View.VISIBLE);
        btt2.setVisibility(View.VISIBLE);
        if (Database.rep.equals("text")){
            btt1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.heart_icon,0);
            btt2.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        if (Database.rep.equals("feed")){
            btt2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.heart_icon,0);
            btt1.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        if (Database.rep.equals("both")){
            btt2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.heart_icon,0);
            btt1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.heart_icon,0);
        }
    }

    public void newFrage(View view) {
        Intent in = new Intent(this, TextActivity.class);
        startActivity(in);
    }

    public void fragen(View view) {
        Intent in = new Intent(this, MeineTexteActivity.class);
        startActivity(in);
    }

    public void review(View view) {
        Intent in = new Intent(this, MeineReviews.class);
        startActivity(in);
    }
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
            builder1.setMessage("Schreibe entweder einen neuen Text oder hole dir Punkte für deine " +
                    "bereits abgegebenen Texte und Bewertungen." +
                    "Ob du neue Punkte in einem der beiden Bereiche hast siehst du an einem " +
                    "kleinen Herz das neben 'Punkte abholen' erscheint <3 ");
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

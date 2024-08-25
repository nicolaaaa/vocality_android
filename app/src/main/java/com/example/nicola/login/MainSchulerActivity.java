package com.example.nicola.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nicola.login.Texte.TextMenuActivity;
import com.example.nicola.login.List.GetAllActivity;
import com.example.nicola.login.Test.TestBuilderActivity;

public class MainSchulerActivity extends AppCompatActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.schuler_menu);
            LinearLayout bg = findViewById(R.id.mainLayout);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.schuler_menu);
            LinearLayout bg = findViewById(R.id.mainLayout);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }
        //Layout auswählen




        Log.i("empty list",Boolean.toString(Database.getInstance().getKurse()==null));

        //Bei Testdurchlauf setze Kurs auf englisch101
        if (Database.userKurs==null)
            Database.userKurs="englisch101";
        Database.kurs = Database.userKurs;

        //Gehe sicher, dass UserType = Schuler (aufgrund typwechsel bestenliste)
        Database.userType = "Schuler";

        //kreiere neue listen Objekte in Datenbank
        Database.getInstance().create();

        //Initiiere Toolbar
        Toolbar to = findViewById(R.id.toolbar);
        setSupportActionBar(to);
        ActionBar t = getSupportActionBar();
        name = Database.userName;
        t.setTitle("Hallo " + name );

        Log.i("Theme ",Database.theme);

        //Initiere Theme Switch Schalter
        Switch sw = findViewById(R.id.modeSwitch2);
        //Falls kein Testlauf -> Theme Steuerung
        if (Database.test==false){
            if(Database.theme.equals("color")) {
                sw.setText("Colorfull Theme");
                Log.i("Theme Schuler","color");
            }
            else{
                sw.setText("Dark Theme");
                Log.i("Theme Schuler","dark");
            }
        }
        //Bei Testlauf -> Mode Steuerung
        else {
            sw.setText("Student Mode");
        }
        registerForContextMenu(sw);
        sw.setChecked(true);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()  {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Falls Testlauf aktiviert wird, wechsel in Lehrer Mode
                if (Database.test==true) {
                    Database.userType = "Lehrer";
                    Intent in = new Intent(getApplicationContext(), MainLehrerActivity.class);


                    startActivity(in);
                }
                else{

                    if(Database.theme.equals("color")) {
                        Log.i("Theme","color change dark");
                        Database.theme="dark";
                        Intent in = new Intent(MainSchulerActivity.this,MainSchulerActivity.class);
                        startActivity(in);
                    }
                    else if(Database.theme.equals("dark")) {
                        Log.i("Theme","color change color");
                        Database.theme="color";
                        Intent in = new Intent(MainSchulerActivity.this,MainSchulerActivity.class);
                        startActivity(in);
                    }
                }

            }
        });

        //Initiiere Levelbar
        ProgressBar exp = findViewById(R.id.progress_bar);
        int lv=Database.getInstance().getLevel();
        //Lade maxExp (Abhängig vom Jeweiligen Level) für richtige Skalierung der Exp Anzeige
        int maxExp =Database.getInstance().getMaxExp(lv);
        double Exp=Database.getInstance().getExp();
        Log.i("Main Schuler LV",Integer.toString(lv));
        Log.i("Main Schuler EXP",Double.toString(Exp));
        Log.i("Main Schuler MAX EXP",Integer.toString(maxExp));
        Log.i("Main Schuler Progress",Double.toString(Exp/maxExp*100));
        TextView ex = findViewById(R.id.expText);
        ex.setText("Level: "+ Integer.toString(lv)+ "   Exp: "+Integer.toString((int)Exp)+" von "+Integer.toString(maxExp));
        TextView lvl = findViewById(R.id.Lvl);
        lvl.setText(Integer.toString(lv));
        exp.setProgress((int)(Exp/maxExp*100));
        checkBadges();
    }

    public void checkBadges (){
    }

    //Bei gedrückt halten switch -> Starten Testlauf -> wechsel zu Lehrer Mode
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Database.test=true;
    }

    //Funktionsaufruf bei Klick auf Vokabel Liste Button
    //Übergeben von Parametern type und liste um GetAllActivity Funktion richtig anzuweisen
    public void getLists(View view){
        Intent intent = new Intent(this, GetAllActivity.class);
        Log.i("DB-name",com.example.nicola.login.Database.userName);
        Log.i("DB-mail",com.example.nicola.login.Database.userMail);
        Log.i("DB-type",com.example.nicola.login.Database.userType);
        Log.i("DB-kurs",com.example.nicola.login.Database.kurs);
        intent.putExtra("type", "liste");
        startActivity(intent);
    }

    //Funktionsaufruf bei Klick auf Test Button
    public void getTest(View view){
        Intent intent = new Intent(this, TestBuilderActivity.class);
        startActivity(intent);

    }

    //Funktionsaufruf bei Klick auf Statistik Button
    //Übergeben von Parametern type und liste um GetAllActivity Funktion richtig anzuweisen
    public void getStatistik(View view){
        Intent intent = new Intent(this, Statistik.class);
        startActivity(intent);
    }

    //Funktionsaufruf bei Klick auf Text Schreiben Button
    public void fragen(View view){
        Intent intent = new Intent(this, TextMenuActivity.class);
        startActivity(intent);
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainSchulerActivity.this);
            builder1.setMessage("Willkommen! Wähle aus was du tun möchtest!");
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

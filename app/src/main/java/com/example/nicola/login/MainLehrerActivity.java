package com.example.nicola.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.nicola.login.List.GetAllActivity;
import com.example.nicola.login.Login.LoginActivity;

public class MainLehrerActivity extends AppCompatActivity {

    String name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.lehrer_menu);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.lehrer_menu);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        //Gehe sicher, dass UserType = Lehrer (aufgrund typwechsel bestenliste)
        Database.userType = "Lehrer";

        //kreiere neue listen Objekte in Datenbank
        Database.getInstance().create();

        //Bei Testdurchlauf setze Kurs auf englisch101
        if (Database.userKurs==null) {
            Database.userKurs = "englisch101";
        }
        Database.kurs = Database.userKurs;

        //Initiiere Toolbar
        Toolbar to = findViewById(R.id.toolbar);
        setSupportActionBar(to);
        name = Database.userName;
        getSupportActionBar().setTitle("Hello " + name );

        //Initiere Theme Switch Schalter
        final Switch sw = findViewById(R.id.modeSwitch2);
        //Falls kein Testlauf -> Theme Steuerung
        if (Database.test==false){
            if(Database.theme.equals("color")) {
                sw.setText("Colorfull Theme");
                Log.i("Theme Lehrer","color");
            }
            else{
                sw.setText("Dark Theme");
                Log.i("Theme Lehrer","dark");
            }
        }
        //Bei Testlauf -> Mode Steuerung
        else {
            sw.setText("Lehrer Mode");
        }
        registerForContextMenu(sw);
        sw.setChecked(true);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()  {

            //Falls Testlauf aktiviert wird, wechsel in Lehrer Mode
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Database.test==true) {
                    Database.userType = "Schuler";
                    Intent in = new Intent(getApplicationContext(), MainSchulerActivity.class);
                    startActivity(in);
                }
                else{
                    if(Database.theme.equals("color")) {
                        setTheme(R.style.AppTheme_Red);
                        Database.theme="dark";
                        Intent in = new Intent(MainLehrerActivity.this,MainLehrerActivity.class);
                        startActivity(in);
                    }
                    else if(Database.theme.equals("dark")) {
                        setTheme(R.style.AppTheme_Green);
                        Database.theme="color";
                        Intent in = new Intent(MainLehrerActivity.this,MainLehrerActivity.class);
                        startActivity(in);
                    }
                }
            }
        });

    }

    //Bei gedrückt halten switch -> Starten Testlauf -> wechsel zu Schuler Mode
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Database.test=true;
    }

    //Funktionsaufruf bei Klick auf Vokabel Liste Button
    //Übergeben von Parametern type und liste um GetAllActivity Funktion richtig anzuweisen
    public void getKurse(View view){
        Intent intent = new Intent(this, GetAllActivity.class);
        Log.i("MainLehrer DB-name",com.example.nicola.login.Database.userName);
        Log.i("MainLehrer DB-type",com.example.nicola.login.Database.userType);
        Database.language = "unknown";
        intent.putExtra("type", "liste");
        startActivity(intent);
    }

    //Funktionsaufruf bei Klick auf Student Liste Button
    //Übergeben von Parametern type und liste um GetAllActivity Funktion richtig anzuweisen
    public void getStudents(View view) {
        Intent intent = new Intent(this, GetAllActivity.class);
        //Database.language übergibt, dass Anfrage von Lehrer und nicht Student kommt
        Database.language = "students";
        intent.putExtra("type", "student");
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainLehrerActivity.this);
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

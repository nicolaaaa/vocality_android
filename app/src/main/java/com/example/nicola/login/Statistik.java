package com.example.nicola.login;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nicola.login.List.GetAllActivity;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Statistik extends AppCompatActivity {

    double durchschnittKomp = Database.userKom/Database.userZahl;
    double durchschnittKre = Database.userKre/Database.userZahl;
    double durchschnittPunkt = Database.userPunk/Database.userZahl;
    double FehlerquoteGram = Database.userFehl/Database.userZahl;
    double FehlerquoteVok = Database.userFehl2/Database.userZahl;
    double Feedbackquote = Database.userFeed;

    double Reputation = (Feedbackquote*(0.0067*durchschnittPunkt+0.6)*(0.02*min(20,Database.userZahl)+0.6))/50;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_statistik);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_statistik);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }


        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Statistik");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainSchulerActivity.class));
            }
        });

        //Initiiere Levelbar
        ProgressBar exp = findViewById(R.id.progress_bar);
        int lv=Database.getInstance().getLevel();
        //Lade maxExp (Abhängig vom Jeweiligen Level) für richtige Skalierung der Exp Anzeige
        int maxExp =Database.getInstance().getMaxExp(lv);
        double Exp=Database.getInstance().getExp();
        TextView ex = findViewById(R.id.expText);
        ex.setText("Level: "+ Integer.toString(lv)+ "   Exp: "+Integer.toString((int)Exp)+" von "+Integer.toString(maxExp));
        TextView ex2 = findViewById(R.id.Lvl);
        ex2.setText(Integer.toString(lv));
        exp.setProgress((int)(Exp/maxExp*100));

        LinearLayout ln = findViewById(R.id.form);
        LinearLayout ln2 = findViewById(R.id.lvlBar);
        LinearLayout ln3 = findViewById(R.id.linearBadge);
        ln.setVisibility(View.GONE);
        ln2.setVisibility(View.GONE);
        ln3.setVisibility(View.GONE);
        TextView txt= findViewById(R.id.expText);
        txt.setVisibility(View.GONE);
        Button all = findViewById(R.id.allButton);
        Button my = findViewById(R.id.myButton);
        Button bad = findViewById(R.id.badgeButton);
        all.setVisibility(View.VISIBLE);
        my.setVisibility(View.VISIBLE);
        bad.setVisibility(View.VISIBLE);
    }

    public void getList (View view){
        Intent intent = new Intent(this, GetAllActivity.class);
        intent.putExtra("type", "student");
        startActivity(intent);
    }


    public void getBadge (View view) {
        LinearLayout ln3 = findViewById(R.id.linearBadge);
        ln3.setVisibility(View.VISIBLE);
        Button all = findViewById(R.id.allButton);
        Button my = findViewById(R.id.myButton);
        Button bad = findViewById(R.id.badgeButton);
        all.setVisibility(View.GONE);
        my.setVisibility(View.GONE);
        bad.setVisibility(View.GONE);
        ImageView b1 = findViewById(R.id.badge1);
        ImageView b2 = findViewById(R.id.badge2);
        ImageView b3 = findViewById(R.id.badge3);
        ImageView b4 = findViewById(R.id.badge4);
        ImageView b5 = findViewById(R.id.badge5);
        ImageView b6 = findViewById(R.id.badge6);
        ImageView b7 = findViewById(R.id.badge7);
        ImageView b8 = findViewById(R.id.badge8);
        ImageView b9 = findViewById(R.id.badge9);

        if (Database.userZahl<10){
            b1.setBackground(getApplicationContext().getDrawable(R.drawable.badge_star_bw));
        }
        if (Database.userFeed<10){
            b6.setBackground(getApplicationContext().getDrawable(R.drawable.badge_good_guy_bw));
        }
        if (durchschnittPunkt<60){
            b7.setBackground(getApplicationContext().getDrawable(R.drawable.badge_hot_bw));
        }
        if (durchschnittKomp<4){
            b8.setBackground(getApplicationContext().getDrawable(R.drawable.heart_icon_big_bw));
        }
        if (durchschnittKre<4){
            b9.setBackground(getApplicationContext().getDrawable(R.drawable.badge_whatever_bw));
        }
        if (Reputation<0.8){
            b3.setBackground(getApplicationContext().getDrawable(R.drawable.badge_police_bw));
        }
        if (FehlerquoteGram>0.2 || FehlerquoteVok>0.2){
            b5.setBackground(getApplicationContext().getDrawable(R.drawable.badge_bad_bw));
        }
        if (Reputation<0.5){
            b2.setBackground(getApplicationContext().getDrawable(R.drawable.badge_winner_bw));
        }
        if (Database.getInstance().getLevel()<10){
            b4.setBackground(getApplicationContext().getDrawable(R.drawable.badge_caesar_bw));
        }
    }

    public void getMy (View view){
        LinearLayout ln = findViewById(R.id.form);
        LinearLayout ln2 = findViewById(R.id.lvlBar);
        ln.setVisibility(View.VISIBLE);
        ln2.setVisibility(View.VISIBLE);
        Button all = findViewById(R.id.allButton);
        Button my = findViewById(R.id.myButton);
        Button bad = findViewById(R.id.badgeButton);
        all.setVisibility(View.GONE);
        my.setVisibility(View.GONE);
        bad.setVisibility(View.GONE);

        if (Reputation>1)
            Reputation=1;

        TextView exp = findViewById(R.id.exp);
        TextView punkt = findViewById(R.id.punkte);
        TextView gram = findViewById(R.id.fehler);
        TextView vok = findViewById(R.id.fehler2);
        TextView rep = findViewById(R.id.rep);
        TextView feed = findViewById(R.id.feed);

        RatingBar ko = findViewById(R.id.komplexRating);
        RatingBar kr = findViewById(R.id.kreativRating);

        ko.setRating((float)durchschnittKomp);
        kr.setRating((float)durchschnittKre);

        String sourceString;

        sourceString = "Gesamt Exp: <b> "+String.format("%.2f", Database.userScore)+" </b>";
        exp.setText(Html.fromHtml(sourceString));
        sourceString = "Durschnittliche Punkte pro Abgabe: <b> "+String.format("%.2f", durchschnittPunkt)+" </b>";
        punkt.setText(Html.fromHtml(sourceString));
        sourceString = "Fehlerquote Grammatikfehler: <b> "+String.format("%.2f", FehlerquoteGram)+" </b>";
        gram.setText(Html.fromHtml(sourceString));
        sourceString = "Fehlerquote Vokabelfehler: <b> "+String.format("%.2f", FehlerquoteVok)+" </b>";
        vok.setText(Html.fromHtml(sourceString));
        sourceString = "Feedbackpunkte: <b> "+String.format("%.2f", Feedbackquote)+" </b>";
        feed.setText(Html.fromHtml(sourceString));

        if (Reputation<0)
            rep.setTextColor(Color.RED);
        if (Reputation>0)
            rep.setTextColor(Color.GREEN);
        sourceString = "Reputation: <b> "+String.format("%.2f", Reputation)+" </b>";
        rep.setText(Html.fromHtml(sourceString));


        if (Reputation<-0.7){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Statistik.this);
            builder1.setMessage("Deine Reputation ist gefährlich schlecht. Wenn du diese nicht verbessern kannst, müssen wir deinen Account aufgrund zu vieler schlechter Bewertungen leider sperren");
            builder1.setIcon(R.drawable.badge2);
            builder1.setTitle("Sehr schlechte Reputation!");
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
        else if (Reputation<0){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(Statistik.this);
            builder1.setMessage("Deine Reputation liegt im unteren Bereich, achte darauf hilfreiche Feedbacks zu geben um diese wieder zu erhöhen");
            builder1.setIcon(R.drawable.badge2);
            builder1.setTitle("Schlechte Reputation!");
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
    }


public  void send(View view){
    Intent intent = new Intent(this, MainSchulerActivity.class);
    startActivity(intent);
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
            builder1.setMessage("Hier kannst du sehen, wie erfolgreich du im Vergleich zu den Anderen in deinem Kurs bist" +
                    " oder dir Informationene zu deinen Stärken und Schwächen holen." +
                    "Deine Reputation kannst du hier auch einsehen, sie gibt an, wie vertrauenswürdig deine Feedback Bewertungen sind " +
                    "und führt bei zu schlechten Werten erst zu kleineren Bestrafungen und später zum Löschen deines Accounts");
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
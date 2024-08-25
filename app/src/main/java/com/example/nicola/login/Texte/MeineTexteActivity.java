package com.example.nicola.login.Texte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.R;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;

public class MeineTexteActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> testItems;
    ArrayList<String> listItems;
    com.example.nicola.login.LoadAllProducts loader = new com.example.nicola.login.LoadAllProducts();
    com.example.nicola.login.UpdateProducts updater = new com.example.nicola.login.UpdateProducts();

    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    private ProgressDialog pDialog;
    private static final String TAG_SUCCESS = "success";


    static String satz;
    ArrayList<String> feedback;
    String user;
    String korrekt;
    String answer;
    boolean GramKorr,VokKorr;
    float Komplex,Kreativ;
    int BenutzteVok;
    boolean lv=false;
    int old,oldLv;
    boolean check;
    String antUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_meine_fragen);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_meine_fragen);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Meine Texte");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TextMenuActivity.class));
            }
        });

        feedback = new ArrayList <>();
        listItems = new ArrayList<String>();
        testItems = new ArrayList<HashMap<String, String>>();
        new Load().execute();

        ListView lv = findViewById(R.id.list4);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ListView lv = findViewById(R.id.list4);
                HashMap<String,String> auswahl = new HashMap<>();
                auswahl=(HashMap)lv.getItemAtPosition(position);
                Log.i("onItmeI", auswahl.toString());

                satz = auswahl.get("satz");
                Log.i("MeineFrage Satz",satz);

                if(Database.theme.equals("color")) {
                    setTheme(R.style.AppTheme_My);
                    setContentView(R.layout.fragen_review);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.sun_clouds));
                }
                else {
                    setTheme(R.style.AppTheme_MyDark);
                    setContentView(R.layout.fragen_review);
                    LinearLayout bg = findViewById(R.id.mainLehrer);
                    bg.setBackground(getDrawable(R.drawable.unnamed));
                }
                TextView txt = findViewById(R.id.wahlFrage);
                txt.setText(satz);
                TextView re = findViewById(R.id.review);
                TextView re2 = findViewById(R.id.review2);
                RatingBar ko = findViewById(R.id.komplexRating);
                RatingBar kr = findViewById(R.id.kreativRating);
                antUser=auswahl.get("antUser");
                if (auswahl.get("gramatik").equals("true")) {
                    String sourceString = "Grammatikalisch korrekt: <b> true </b>";
                    re.setText(Html.fromHtml(sourceString));
                    GramKorr=true;

                }
                else  {
                    String sourceString = "Grammatikalisch korrekt: <b> false </b>";
                    re.setText(Html.fromHtml(sourceString));
                    Database.userFehl += 1;
                    GramKorr=false;
                }
                if (auswahl.get("worte").equals("true")) {
                    String sourceString = "Vokabeln korrekt: <b> true </b>";
                    re2.setText(Html.fromHtml(sourceString));
                    VokKorr=true;

                }
                else  {
                    String sourceString = "Vokabelh korrekt: <b> false </b>";
                    re2.setText(Html.fromHtml(sourceString));
                    Database.userFehl2 += 1;
                    VokKorr=false;
                }
                ko.setRating(Float.parseFloat(auswahl.get("komplex")));
                kr.setRating(Float.parseFloat(auswahl.get("kreativ")));
                Database.userKre += Float.parseFloat(auswahl.get("kreativ"));
                Database.userKom += Float.parseFloat(auswahl.get("komplex"));

                BenutzteVok=Integer.parseInt(auswahl.get("extra"));
                Komplex = Float.parseFloat(auswahl.get("komplex"));
                Kreativ = Float.parseFloat(auswahl.get("kreativ"));
            }
        });
    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            Database.fragen="fr";
            try {
                testItems = loader.loadData("frage", "fr");
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Fragen",testItems.toString());

            runOnUiThread(new Runnable() {
                public void run() {
                    ListView lv = findViewById(R.id.list4);

                    ListAdapter adapter = new SimpleAdapter(
                            MeineTexteActivity.this, testItems,
                            R.layout.list_item, new String[]{
                            "pid","user","satz"},
                            new int[]{R.id.pid,R.id.kurs,R.id.name});
                    // updating listview
                    lv.setAdapter(adapter);


                }
            });
        }
    }


    public void send(View view){
        RadioButton r1 = findViewById(R.id.yesButton);
        RadioButton r2 = findViewById(R.id.noButton);

        check = true;
        if (r1.isChecked()){
            feedback.add("true");
        } else if (r2.isChecked()){
            feedback.add("false");
        } else {
            check = false;
            Toast toast = Toast.makeText(this, "please choose something", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (check) {
            Button b = findViewById(R.id.badBtn);
            b.setVisibility(View.INVISIBLE);
            TextView re = findViewById(R.id.review);
            TextView re2 = findViewById(R.id.review2);
            RatingBar ko = findViewById(R.id.komplexRating);
            RatingBar kr = findViewById(R.id.kreativRating);
            TextView t1 = findViewById(R.id.txt1);
            TextView t2 = findViewById(R.id.txt2);
            TextView t3 = findViewById(R.id.txt3);
            re.setVisibility(View.GONE);
            re2.setVisibility(View.GONE);
            ko.setVisibility(View.GONE);
            kr.setVisibility(View.GONE);
            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);
            t3.setVisibility(View.GONE);
            r1.setVisibility(View.GONE);
            r2.setVisibility(View.GONE);

            Button c = findViewById(R.id.backButton);
            c.setVisibility(View.VISIBLE);
            old = (int)(Database.getInstance().getExp()/Database.getInstance().getMaxExp(Database.getInstance().getLevel())*100);
            oldLv = Database.getInstance().getLevel();
            int points = 0;

            points = PunkteBerechnung();

            Database.userScore = Database.userScore + points;
            Database.userPunk += points;
            Toast toa = Toast.makeText(this, " +" + Integer.toString(points) + " exp", Toast.LENGTH_SHORT);
            toa.show();

            new SetScore().execute();
            LinearLayout exp = findViewById(R.id.lvlBar);
            exp.setVisibility(View.VISIBLE);
            TextView ex = findViewById(R.id.expTextRev);
            ex.setVisibility(View.VISIBLE);
            int lv=Database.getInstance().getLevel();
            int maxExp =Database.getInstance().getMaxExp(lv);
            double Exp=Database.getInstance().getExp();
            int progress = (int)(Exp/maxExp*100);
            ex.setText("Level: "+ Integer.toString(lv)+ "   Exp: "+Integer.toString((int)Exp)+" von "+Integer.toString(maxExp));
            TextView ex2 = findViewById(R.id.Lvl);
            ex2.setText(Integer.toString(lv));
            Log.i("Main Schuler LV",Integer.toString(progress));
            startAnimation(progress);

        }

    }

    private void startAnimation(int pro){
        ProgressBar mProgressBar = findViewById(R.id.progress_barRev);
        final int progress=pro;
        if (oldLv<Database.getInstance().getLevel()){
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", old, 100);
            progressAnimator.setDuration(1000);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LvUp(progress);
                }
            });
            progressAnimator.start();
        }else {
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", old, pro);
            progressAnimator.setDuration(2000);
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.start();
        }
    }

    private void LvUp(int pro){
        Toast toa = Toast.makeText(this, "Level Up !", Toast.LENGTH_SHORT);
        toa.show();
        ProgressBar mProgressBar = findViewById(R.id.progress_barRev);
        new ParticleSystem(this, 50,R.drawable.heart_icon , 1000)
                .setSpeedRange(0.2f, 0.5f)
                .oneShot(mProgressBar, 50);
        ObjectAnimator progAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, pro);
        progAnimator.setDuration(1000);
        progAnimator.setInterpolator(new LinearInterpolator());
        progAnimator.start();
    }

    class SetScore extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            Database.userZahl += 1;
            int score =(int) Database.userScore;
            int rep =(int) Database.userRep;
            int komp =(int) Database.userKom;
            int kre =(int) Database.userKre;
            int punkt =(int) Database.userPunk;
            int gram =(int) Database.userFehl;
            int vok =(int) Database.userFehl2;
            int feed =(int) Database.userFeed;
            int zahl =(int) Database.userZahl;
            ArrayList<String> ne = new ArrayList <>();
            ne.add(Integer.toString(score));
            ne.add(Integer.toString(rep));
            ne.add(Integer.toString(komp));
            ne.add(Integer.toString(kre));
            ne.add(Integer.toString(punkt));
            ne.add(Integer.toString(gram));
            ne.add(Integer.toString(vok));
            ne.add(Integer.toString(feed));
            ne.add(Integer.toString(zahl));
            try {
                updater.update("student","stats",Database.userMail, ne);
                ArrayList<String> var2 = new ArrayList <>();
                var2.add("feed");
                updater.update("student","rep2",antUser,var2);
                updater.update("frage","feed",satz, feedback);
                ArrayList<String> var3 = new ArrayList <>();
                if (Database.rep.equals("both")){
                    var3.add("feed");
                    Database.rep="feed";
                }
                else {
                    var3.add("0");
                    Database.rep="0";
                }
                    updater.update("student","rep",Database.userMail,var3);
            }catch (Exception e){}

            return null;
        }
    }
    public int PunkteBerechnung(){
        int points;
        int bonus;
        if (GramKorr || VokKorr) {
            bonus = 20;
        }
        if (GramKorr && VokKorr) {
            bonus = 80;
        }
        else bonus = 10;
        points = Math.round(Komplex) + Math.round(Kreativ) + BenutzteVok*50 + bonus + satz.length()*5;
    return points;
    }

    public void back(View view){
        Intent in = new Intent(MeineTexteActivity.this,TextMenuActivity.class);
        startActivity(in);
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
            builder1.setMessage("Hole hier die Punkte für deine Texte ab!" +
                    "Die Bewertung setzt sich dabei folgendermaßen zusammen: Korrekte Grammatik und Vokabeleinsatz, sowie " +
                    "die Anzahl der verwendeten Wörter werden am höchsten gewichtet, danach kommen dann Kreativität, Komplexität und die " +
                    "Länge deines Textes insgesamt.");
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

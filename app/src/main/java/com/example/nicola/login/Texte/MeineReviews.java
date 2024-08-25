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
import com.example.nicola.login.List.DeleteActivity;
import com.example.nicola.login.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MeineReviews extends AppCompatActivity {
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
    boolean extra1,extra2;
    float extra3,extra4;
    boolean lv=false;
    int old,oldLv;
    boolean check;

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
        getSupportActionBar().setTitle("Meine Reviews");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TextMenuActivity.class));
            }
        });

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
                Log.i("Review Satz",satz);
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

                RadioButton r1 = findViewById(R.id.yesButton);
                RadioButton r2 = findViewById(R.id.noButton);
                TextView re = findViewById(R.id.review);
                TextView re2 = findViewById(R.id.review2);
                RatingBar ko = findViewById(R.id.komplexRating);
                RatingBar kr = findViewById(R.id.kreativRating);
                TextView t1 = findViewById(R.id.txt1);
                TextView t2 = findViewById(R.id.txt2);
                TextView t3 = findViewById(R.id.txt3);
                re2.setVisibility(View.GONE);
                ko.setVisibility(View.GONE);
                kr.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);
                t3.setVisibility(View.GONE);
                r1.setVisibility(View.GONE);
                r2.setVisibility(View.GONE);

                if (auswahl.get("feed gut").equals("true")) {
                    Database.userFeed += 1;
                    String sourceString = "Meine Review wurde bewertet als: <b> Hilfreich </b>";
                    re.setText(Html.fromHtml(sourceString));
                    extra1=true;

                }
                else  {
                    String sourceString = "Meine Review wurde bewertet als: <b> Nicht hilfreich </b>";
                    re.setText(Html.fromHtml(sourceString));
                    extra1=false;
                }
            }
        });
    }

    class Load extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            Database.fragen="rev";
            Database.fragen2=Database.userMail;
            try {
                testItems = loader.loadData("frage", "rev");
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
                            MeineReviews.this, testItems,
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
            Button g = findViewById(R.id.backButton);
            g.setVisibility(View.VISIBLE);
        Button b = findViewById(R.id.badBtn);
        b.setVisibility(View.GONE);
            old = (int)(Database.getInstance().getExp()/Database.getInstance().getMaxExp(Database.getInstance().getLevel())*100);
            oldLv = Database.getInstance().getLevel();
            int points = 0;

            if (extra1)
                points=50;
            else
                points=25;
            Database.userScore = Database.userScore + points;
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
        ObjectAnimator progAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, pro);
        progAnimator.setDuration(1000);
        progAnimator.setInterpolator(new LinearInterpolator());
        progAnimator.start();
    }

    class SetScore extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            int score =(int) Database.userScore;
            int zahl =(int) Database.userZahl;
            int feed =(int) Database.userFeed;
            ArrayList<String> ne = new ArrayList <>();
            ne.add(Integer.toString(score));
            ne.add(Integer.toString(zahl));
            ne.add(Integer.toString(feed));
            try {
                updater.update("student","score_feed",Database.userMail, ne);
            }catch (Exception e){}

            ArrayList<String> var3 = new ArrayList <>();
            if (Database.rep.equals("both")){
                var3.add("text");
                Database.rep="text";
            }
            else {
                var3.add("0");
                Database.rep="0";
            }
            try {
                updater.update("student","rep",Database.userMail,var3);
            }catch (Exception e){}
            return null;
        }
    }

    public void back(View view){
        Intent in = new Intent(this,DeleteActivity.class);
        in.putExtra("var",satz);
        in.putExtra("type","frage");
        startActivityForResult(in,100);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Intent in = new Intent(this,TextMenuActivity.class);
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
            builder1.setMessage("Hole hier die Punkte für deine Bewertungen ab!" +
                    "Die Bewertung setzt sich dabei folgendermaßen zusammen: " +
                    "Für jedes mit 'hilfreich' bewertete Feedback bekommst du 50 Punkte und " +
                    "für jedes 'nicht hilfreiche' 10 Punkte. Außerdem wird deine Reputation zum positiven" +
                    " bzw. negativen Verändert. Um dir diese anzugucken, gehe einfach auf deine Statistik.");
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
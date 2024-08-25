package com.example.nicola.login.Test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.nicola.login.Database;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;
import com.example.nicola.login.UpdateProducts;

import java.util.ArrayList;
import java.util.HashMap;


public class NewTestActivity extends AppCompatActivity {

    String type;
    ArrayList<String> lists;
    int i;
    static int old, oldLv;
    ArrayList<HashMap<String, String>> testItems;
    int richtige;
    UpdateProducts updater = new UpdateProducts();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_new_test);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_new_test);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }

        android.support.v7.widget.Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        richtige=0;

        Intent intent = getIntent();
        testItems = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("items");

        Log.i("testitems",testItems.toString());
        i=0;
        abfrage(testItems.get(i).get("word"));
    }

    public void abfrage(String vok){
        TextView text = findViewById(R.id.Vokabel);
        text.setText(vok);
    }

    public void checkResult(View view){
        EditText text = findViewById(R.id.transText);
        String transi = testItems.get(i).get("translation");
        String[] parts = transi.split(",");
        Log.i("vorgabe", testItems.get(i).get("translation"));
        Log.i("eingabe", text.getText().toString());
        Toast toast;
        boolean counter = false;
        for (int i = 0;i<parts.length;i++){
            Log.i("vorgabe part", parts[i]);
            if (text.getText().toString().equals(parts[i])) {
                counter = true;
            }
        }
        if (counter){
            Log.i("ergebniss", "richtig");
            toast = Toast.makeText(this, "richtig", Toast.LENGTH_SHORT);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(getResources().getColor(R.color.right));
            toast.show();
            richtige = richtige + 1;
        } else{
            Log.i("ergebniss", "falsch");
            toast = Toast.makeText(this, "falsch", Toast.LENGTH_SHORT);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(getResources().getColor(R.color.wrong));
            toast.show();
        }

        i=i+1;
        if(i>=testItems.size()){
            TextView res = findViewById(R.id.resultView);
            TextView trans = findViewById(R.id.transText);
            TextView v = findViewById(R.id.Vokabel);
            Button bt = findViewById(R.id.Check);
            Button ok = findViewById(R.id.resultOK);
            res.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);
            trans.setVisibility(View.INVISIBLE);
            v.setVisibility(View.INVISIBLE);
            bt.setVisibility(View.INVISIBLE);
            toast.cancel();
            res.setText("Ergebniss: "+Integer.toString(richtige)+" aus "+Integer.toString(testItems.size())+" richtg");

            old = (int)(Database.getInstance().getExp()/Database.getInstance().getMaxExp(Database.getInstance().getLevel())*100);
            oldLv = Database.getInstance().getLevel();

            double extra = 100*richtige/testItems.size()+20;
            Database.userScore = Database.userScore+extra;
            Toast toa = Toast.makeText(NewTestActivity.this, " +"+Double.toString(extra)+"exp", Toast.LENGTH_SHORT);
            toa.show();

            new SetScore().execute();
            ProgressBar exp = findViewById(R.id.progress_barTest);
            exp.setVisibility(View.VISIBLE);
            TextView ex = findViewById(R.id.expTextTest);
            ex.setVisibility(View.VISIBLE);
            int lv=Database.getInstance().getLevel();
            int maxExp =Database.getInstance().getMaxExp(lv);
            double Exp=Database.getInstance().getExp();
            int progress = (int)(Exp/maxExp*100);
            ex.setText("Level: "+ Integer.toString(lv)+ "   Exp: "+Integer.toString((int)Exp)+" von "+Integer.toString(maxExp));
            startAnimation(progress);
        }
        else {
            text.setText("");
            abfrage(testItems.get(i).get("word"));
        }
    }

    private void startAnimation(int pro){
        ProgressBar mProgressBar = findViewById(R.id.progress_barTest);
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
        ProgressBar mProgressBar = findViewById(R.id.progress_barTest);
        ObjectAnimator progAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, pro);
        progAnimator.setDuration(1000);
        progAnimator.setInterpolator(new LinearInterpolator());
        progAnimator.start();
    }

    public void finished (View view){
        Intent in = new Intent(this, MainSchulerActivity.class);
        startActivity(in);
    }

    class SetScore extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
            int score =(int) Database.userScore;
            ArrayList<String>ne = new ArrayList <>();
            ne.add(Integer.toString(score));
            try {
                updater.update("student","score",Database.userMail, ne);
            }catch (Exception e){}
            return null;
        }
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(NewTestActivity.this);
            builder1.setMessage("Starte hier einen Vokabeltest um deine Kenntnisse zu überprüfen!");
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

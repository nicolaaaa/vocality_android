package com.example.nicola.login.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.nicola.login.Database;
import com.example.nicola.login.LoadAllProducts;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;
import com.example.nicola.login.SplashActivity;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    boolean error=false;

    public static String ip = "ec2-34-203-202-226.compute-1.amazonaws.com";

    LoadAllProducts loader = new LoadAllProducts();
    ArrayList<HashMap<String, String>> productsList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color") || Database.theme==null) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_login);
            LinearLayout bg = findViewById(R.id.mainLogin);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_login);
            LinearLayout bg = findViewById(R.id.mainLogin);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }
        //Initialisiere Toolbar
        Toolbar to = findViewById(R.id.toolbar);
        setSupportActionBar(to);
        ActionBar t = getSupportActionBar();
        t.setTitle("Welcome to Vocality");

        //Initialisiere Theme Switch Schalter
        Switch sw = findViewById(R.id.modeSwitch2);
        if (Database.test==false){
            //Falls kein Testlauf -> Theme Steuerung
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
        else sw.setText("Student Mode");
        registerForContextMenu(sw);
        sw.setChecked(true);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()  {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(Database.theme.equals("color")) {
                        Log.i("Theme","color change dark");
                        Database.theme="dark";
                        Intent in = new Intent(LoginActivity.this,LoginActivity.class);
                        startActivity(in);
                    }
                    else if(Database.theme.equals("dark")) {
                        Log.i("Theme","color change color");
                        Database.theme="color";
                        Intent in = new Intent(LoginActivity.this,LoginActivity.class);
                        startActivity(in);
                    }
                }
        });

        Database.fragen="";

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        //Bei Klick des Sign In Buttons probiere Einzuloggen
        //Aufruf Funktion attempLogin()
        final Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //Bei gedrückt halten auf Theme Switch -> Begin Testlauf
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Database.userType = "Schuler";
        Database.userName = "Testuser";
        Database.userKurs = "englisch101";
        Database.userMail = "testmail";
        Database.userScore = 220;
        Database.test = false;
        Intent intent = new Intent(getApplicationContext(), MainSchulerActivity.class);
        startActivity(intent);

    }

    //Prüfe User Eingaben um Login durchzuführen
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    //Zu überprüfende Kriterien für Email Adresse
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //Zu überprüfende Kriterien für Passwort
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    //Zeige Progress Bar während UserDaten aus Datenbank geholt werden
    private void showProgress(final boolean show) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
    }

    //Hole UserDaten aus Datenbank
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                productsList = loader.loadData("student", "all");
            }
            catch (Exception e){
                LoginActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                error=true;

                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
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
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

                mAuthTask = null;
                showProgress(false);
            if (error == false) {
                int count = 0;

                for (int i = 0; i < productsList.size(); i++) {
                    //Überprüfe, ob UserName in Datenbank existiert
                    if (productsList.get(i).get("email").equals(mEmail)) {
                        count++;
                        Log.i("hh", Integer.toString(count));

                        //Überprüfe, ob richtiges Passwort für UserName eingegeben wurde
                        if (productsList.get(i).get("password").equals(mPassword)) {
                            LogIn(i);
                            break;
                        }
                        //Falls Passwort falsch gebe entsprechenden Fehler aus
                        else {
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                            Log.i("hhhhh", "pass");
                        }
                    }
                }
                //Falls kein User mit diesem Namen in Datenbank gefunden wurde gebe entsprechenden Fehler aus
                if (count == 0) {
                    mEmailView.setError(getString(R.string.error_no_account));
                    mEmailView.requestFocus();
                    Log.i("hhhhh", "acco");
                    Log.i("hhhhh", Integer.toString(count));
                }
            }
                error=false;

            }

            protected void onCancelled () {
                    mAuthTask = null;
                    showProgress(false);
            }
    }

    //Bei Klick auf Registrieren Button weiter zur Registrierungsseite
    public void newAccount(View view){
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }

    public void LogIn (int i){
        //Überprüfe, ob einloggender User als Lehrer oder Schüler registriert ist
        //Lade die entsprechenden Daten aus Datenbank
        //Logge User ein -> Gehe entweder ins Lehrer- oder Scüler-StartMenu
        if (productsList.get(i).get("type").equals("Schuler")){

            Database.userType = productsList.get(i).get("type");
            Database.userName = productsList.get(i).get("name");
            Database.userKurs = productsList.get(i).get("kurs");
            Database.userMail = productsList.get(i).get("email");
            Database.userScore = Double.parseDouble(productsList.get(i).get("score"));
            Database.userKom = Double.parseDouble(productsList.get(i).get("komp"));
            Database.userKre = Double.parseDouble(productsList.get(i).get("kre"));
            Database.userFehl = Double.parseDouble(productsList.get(i).get("gram"));
            Database.userFehl2 = Double.parseDouble(productsList.get(i).get("vok"));
            Database.userPunk = Double.parseDouble(productsList.get(i).get("punkt"));
            Database.userFeed = Double.parseDouble(productsList.get(i).get("feed"));
            Database.userZahl = Double.parseDouble(productsList.get(i).get("zahl"));
            Database.test = false;

            Intent intent = new Intent(getApplicationContext(), MainSchulerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
        if (productsList.get(i).get("type").equals("Lehrer")){
            Database.userType = productsList.get(i).get("type");
            Database.userName = productsList.get(i).get("name");
            Database.userKurs = productsList.get(i).get("kurs");
            Database.userMail = productsList.get(i).get("email");
            Database.test = false;


            Intent intent = new Intent(getApplicationContext(), MainLehrerActivity.class);
            startActivity(intent);
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
            builder1.setMessage("Willkommen bei Vocality! Falls du noch keinen Account hast registriere dich. "+
                            "Andernfalls kannst du dich hier mit deinen Daten einloggen");
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


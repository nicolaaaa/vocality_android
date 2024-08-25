package com.example.nicola.login.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mNameView;
    private View mProgressView;
    private View mLoginFormView;

    static String email;
    static String password;
    static String ty;
    static String nam;

    boolean error=false;
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    private static String url_create_student = "http://"+LoginActivity.ip+"/android_vocabeln/create_student.php";

    private ProgressDialog pDialog;

    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            setContentView(R.layout.activity_register);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            setContentView(R.layout.activity_register);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }


        Toolbar myChildToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Registrierung");

        myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mNameView = findViewById(R.id.name);

        RadioGroup sw = findViewById(R.id.RadioGr);
        sw.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()  {
            @Override
            public void onCheckedChanged(RadioGroup buttonView, int isChecked) {
                hideKeyboard(RegisterActivity.this);

            }
        });

        Button mEmailSignInButton = findViewById(R.id.register_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Vocality ist eine App die dir durch das Verfassen von Texten helfen soll " +
                "Vokabeln zu lernen, deinen Wortschatz zu eweitern und sicherer im Bilden von Sätzen zu werden. " +
                "Falls du die App zum aufbessern deiner Vokabeln nutzen willst,"+
                "melde dich als Schüler an. Als Lehrer kannst du mit deinem Account neue Vokabellisten erstellen, "+
                "welche andere Schüler dann durch Einschreiben in deinen Kurs nutzen können.");
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
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String type=null;

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

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

        RadioButton rb = findViewById(R.id.RadioLehrer);
        RadioButton rbb = findViewById(R.id.RadioStudent);
        if (rb.isChecked()) {
            type="Lehrer";
        }
        else if (rbb.isChecked()) {
            type="Schuler";
        }
        else {
            Toast.makeText(getApplicationContext(), "please select if you're a teacher or student", Toast.LENGTH_SHORT).show();
            TextView i = findViewById(R.id.Info);
            focusView = i;
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
            mAuthTask = new UserLoginTask(name, email, password, type);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mType;
        private final String mName;

        UserLoginTask(String name, String email, String password, String type) {
            mEmail = email;
            mPassword = password;
            mType = type;
            mName = name;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            JSONObject json = new JSONObject();
            List<NameValuePair> params = new ArrayList<NameValuePair>();


                    // Building Parameters
                    params.add(new BasicNameValuePair("email", mEmail));
                    params.add(new BasicNameValuePair("password", mPassword));
                    params.add(new BasicNameValuePair("name", mName));
                    params.add(new BasicNameValuePair("score", "0"));
                    params.add(new BasicNameValuePair("type", mType));
                    Log.i("Create Response", params.toString());


                    // getting JSON Object
                    // Note that create product url accepts POST method
            try {

                json = jsonParser.makeHttpRequest(url_create_student,
                        "POST", params);
            }
            catch (Exception e){
                RegisterActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {

                        Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        error=true;

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
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
                    Log.i("Create Response", url_create_student);
                    // check for success tag
                    try {
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));


                        if (success == 1) {
                            finish();
                        } else {
                            // failed to create product
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                return null;

            }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            mAuthTask = null;
            showProgress(false);
            if (error==false){

            RadioButton rb = findViewById(R.id.RadioLehrer);
            RadioButton rbb = findViewById(R.id.RadioStudent);
            Database.userType = mType;
            Database.userName = mName;
            Database.userMail = mEmail;

            if (rb.isChecked()) {
                Intent in = new Intent(getApplicationContext(), RegisterKursActivity.class);
                in.putExtra("type", "lehrer");
                startActivity(in);
                }
                else if (rbb.isChecked()) {
                    Intent in = new Intent(getApplicationContext(), RegisterKursActivity.class);
                    in.putExtra("type", "student");
                    startActivity(in);
                }
        }
            error=false;


        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            builder1.setMessage("Vocality ist eine App die dir durch das Verfassen von Texten helfen soll " +
                    "Vokabeln zu lernen, deinen Wortschatz zu eweitern und sicherer im Bilden von Sätzen zu werden. " +
                    "Falls du die App zum aufbessern deiner Vokabeln nutzen willst,"+
                    "melde dich als Schüler an. Als Lehrer kannst du mit deinem Account neue Vokabellisten erstellen, "+
                    "welche andere Schüler dann durch Einschreiben in deinen Kurs nutzen können.");
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





package com.example.nicola.login.List;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.nicola.login.Database;
import com.example.nicola.login.JSONParser;
import com.example.nicola.login.Login.LoginActivity;

public class DeleteActivity extends Activity {


    String pid;
    String type;
    String listid;
    ArrayList<String> childrenList;
    ArrayList<String> childrenVok;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // url to delete product
    private static final String url_delete_liste = "http://"+ LoginActivity.ip+"/android_vocabeln/delete_liste.php";
    private static final String url_delete_kurs = "http://"+ LoginActivity.ip+"/android_vocabeln/delete_kurs.php";
    private static final String url_delete_vokabel = "http://"+ LoginActivity.ip+"/android_vocabeln/delete_vocabel.php";
    private static final String url_delete_frage = "http://"+ LoginActivity.ip+"/android_vocabeln/delete_frage.php";

    private static String url_all_vocabeln = "http://"+ LoginActivity.ip+"/android_vocabeln/get_all_vocabeln.php";
    private static String url_all_listen = "http://"+ LoginActivity.ip+"/android_vocabeln/get_all_listen.php";


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_VOCABELN = "vocabeln";
    private static final String TAG_PID = "pid";
    private static final String TAG_LISTEN = "listen";
    private static final String TAG_LISTE = "liste";
    private static final String TAG_KURS = "kurs";

    JSONArray products = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra("var");
        type = i.getStringExtra("type");
        Log.i("DEL", pid);
        listid = pid;


        // Getting complete product details in background thread
        new DeleteProduct().execute();
    }


    class DeleteProduct extends AsyncTask <String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteActivity.this);
            pDialog.setMessage("Deleting Product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;

            try {
                JSONObject json = new JSONObject();


                if (type.equals("liste")) {
                    // Building Parameters
                    List <NameValuePair> params = new ArrayList <NameValuePair>();
                    params.add(new BasicNameValuePair("pid", pid));


                        // getting product details by making HTTP request
                        json = jsonParser.makeHttpRequest(
                                url_delete_liste, "POST", params);

                    childrenVok = new ArrayList <>();
                    params = new ArrayList <NameValuePair>();

                    json = jsonParser.makeHttpRequest(url_all_vocabeln, "GET", params);
                    Log.d("All Products: ", json.toString());

                    try {
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            products = json.getJSONArray(TAG_VOCABELN);
                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_LISTE).equals(pid)) {
                                    // Storing each json item in variable
                                    childrenVok.add(c.getString(TAG_PID));
                                    Log.d("Products: ", childrenVok.toString());
                                    Log.d("Products: ", pid);
                                }
                            }
                            Database.getInstance().deleteList(pid);
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    type = "vokabel";
                    for (int m=0; m<childrenVok.size(); m++){
                        pid=childrenVok.get(m);
                        Log.i("Products: ", pid);
                        params = new ArrayList <NameValuePair>();
                        params.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        json = jsonParser.makeHttpRequest(
                                url_delete_vokabel, "POST", params);
                    }
                    Database.getInstance().deleteVoc(pid);
                }
                else {
                    if (type.equals("kurs")) {
                        List <NameValuePair> params = new ArrayList <NameValuePair>();
                        params.add(new BasicNameValuePair("name", pid));
                        Log.i("SUCCESS", params.toString());

                        // getting product details by making HTTP request
                        json = jsonParser.makeHttpRequest(
                                url_delete_kurs, "POST", params);

                        childrenList = new ArrayList <>();
                        params = new ArrayList <NameValuePair>();

                        json = jsonParser.makeHttpRequest(url_all_listen, "GET", params);
                        Log.d("All Products: ", json.toString());

                        try {
                            success = json.getInt(TAG_SUCCESS);
                            if (success == 1) {
                                products = json.getJSONArray(TAG_LISTEN);
                                // looping through All Products
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject c = products.getJSONObject(i);
                                    if (c.getString(TAG_KURS).equals(pid)) {
                                        // Storing each json item in variable
                                        childrenList.add(c.getString(TAG_PID));
                                        Log.d("Products: ", childrenList.toString());
                                        Log.d("Products: ", pid);
                                    }
                                }
                            } else {
                            }
                            Database.getInstance().deleteKurs();
                            Database.getInstance().deleteList(pid);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        type = "liste";
                        for (int m=0; m<childrenList.size(); m++){
                            pid=childrenList.get(m);
                            Log.d("Products: ", pid);
                            params = new ArrayList <NameValuePair>();
                            params.add(new BasicNameValuePair("pid", pid));

                            // getting product details by making HTTP request
                            json = jsonParser.makeHttpRequest(
                                    url_delete_liste, "POST", params);

                            childrenVok = new ArrayList <>();
                            params = new ArrayList <NameValuePair>();

                            json = jsonParser.makeHttpRequest(url_all_vocabeln, "GET", params);
                            Log.d("All Products: ", json.toString());

                            try {
                                success = json.getInt(TAG_SUCCESS);
                                if (success == 1) {
                                    products = json.getJSONArray(TAG_VOCABELN);
                                    // looping through All Products
                                    for (int i = 0; i < products.length(); i++) {
                                        JSONObject c = products.getJSONObject(i);
                                        if (c.getString(TAG_LISTE).equals(pid)) {
                                            // Storing each json item in variable
                                            childrenVok.add(c.getString(TAG_PID));
                                            Log.d("Products: ", childrenVok.toString());
                                            Log.d("Products: ", pid);
                                        }
                                    }

                                } else {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            type = "vokabel";
                            for (int n=0; n<childrenVok.size(); n++){
                                pid=childrenVok.get(n);
                                Log.d("Products: ", pid);
                                params = new ArrayList <NameValuePair>();
                                params.add(new BasicNameValuePair("pid", pid));

                                // getting product details by making HTTP request
                                json = jsonParser.makeHttpRequest(
                                        url_delete_vokabel, "POST", params);

                            }

                            Database.getInstance().deleteVoc(pid);

                        }



                    }else {
                        if (type.equals("vokabel")) {
                            List <NameValuePair> params = new ArrayList <NameValuePair>();
                            params.add(new BasicNameValuePair("pid", pid));
                            Log.i("SUCCESS", params.toString());

                            // getting product details by making HTTP request
                            json = jsonParser.makeHttpRequest(
                                    url_delete_vokabel, "POST", params);
                            Database.getInstance().deleteVoc(pid);


                        }
                    else {
                            if (type.equals("frage")) {
                                List <NameValuePair> params = new ArrayList <NameValuePair>();
                                params.add(new BasicNameValuePair("satz", pid));
                                Log.i("SUCCESS", params.toString());

                                // getting product details by making HTTP request
                                json = jsonParser.makeHttpRequest(
                                        url_delete_frage, "POST", params);



                            }
                        }
                    }
                }


                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
        }

    }

}
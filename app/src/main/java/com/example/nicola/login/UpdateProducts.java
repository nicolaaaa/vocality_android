package com.example.nicola.login;

import android.content.Intent;
import android.util.Log;

import com.example.nicola.login.Login.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UpdateProducts {

    public UpdateProducts() {

    }

    // Creating JSON Parser ob
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    static String type;
    static String objekt;
    static String goal;
    static String var;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static String update_students_kurs = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_kurs.php";
    private static String update_students_rep = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_rep.php";
    private static String update_students_rep2 = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_rep2.php";
    private static String update_students_score = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_score.php";
    private static String update_frage_all = "http://"+ LoginActivity.ip+"/android_vocabeln/update_fragen_all.php";
    private static String update_frage_feed = "http://"+ LoginActivity.ip+"/android_vocabeln/update_fragen_feed.php";
    private static String update_students_score_feed = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_score_feed.php";
    private static String update_students_stats = "http://"+ LoginActivity.ip+"/android_vocabeln/update_students_stats.php";


    // products JSONArray
    JSONArray products = null;

    public  boolean update (String type, String objekt, String goal, ArrayList<String> var)throws Exception{
        productsList = new ArrayList<HashMap<String, String>>();

        Log.i("update", "start http_request");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = new JSONObject();
        Log.i("Update type", type);


        switch (type) {
            case "student":
                // getting JSON string from URL
                Log.i("Update goal", goal);
                Log.i("Update var", var.toString());

                if (objekt.equals("kurs")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("kurs", var.get(0)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_students_kurs, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (objekt.equals("rep")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("new", var.get(0)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_students_rep, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (objekt.equals("score")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("score", var.get(0)));
                    params.add(new BasicNameValuePair("zahl", Double.toString(Database.userZahl + 1)));
                    Log.i("Update params", params.toString());
                    json = jParser.makeHttpRequest(update_students_score, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (objekt.equals("rep2")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("new", var.get(0)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_students_rep2, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (objekt.equals("score_feed")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("score", var.get(0)));
                    params.add(new BasicNameValuePair("zahl", var.get(1)));
                    params.add(new BasicNameValuePair("feed", var.get(2)));
                    Log.i("Update params", params.toString());
                    json = jParser.makeHttpRequest(update_students_score_feed, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (objekt.equals("stats")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("score", var.get(0)));
                    params.add(new BasicNameValuePair("rep", var.get(1)));
                    params.add(new BasicNameValuePair("komp", var.get(2)));
                    params.add(new BasicNameValuePair("kre", var.get(3)));
                    params.add(new BasicNameValuePair("punkt", var.get(4)));
                    params.add(new BasicNameValuePair("gram", var.get(5)));
                    params.add(new BasicNameValuePair("vok", var.get(6)));
                    params.add(new BasicNameValuePair("feed", var.get(7)));
                    params.add(new BasicNameValuePair("zahl", var.get(8)));
                    Log.i("Update params", params.toString());
                    json = jParser.makeHttpRequest(update_students_stats, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case "lehrer":
                // getting JSON string from URL
                Log.i("Update goal", goal);
                Log.i("Update var", var.toString());

                if (objekt.equals("kurs")) {
                    params.add(new BasicNameValuePair("email", goal));
                    params.add(new BasicNameValuePair("kurs", var.get(0)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_students_kurs, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "frage":
                // getting JSON string from URL
                Log.i("Update goal", goal);
                Log.i("Update var", var.toString());

                if (objekt.equals("all")) {
                    params.add(new BasicNameValuePair("satz", goal));
                    params.add(new BasicNameValuePair("antUser", var.get(0)));
                    params.add(new BasicNameValuePair("korrekt", var.get(1)));
                    params.add(new BasicNameValuePair("worte", var.get(2)));
                    params.add(new BasicNameValuePair("komplex", var.get(3)));
                    params.add(new BasicNameValuePair("kreativ", var.get(4)));
                    params.add(new BasicNameValuePair("antwort", var.get(5)));
                    params.add(new BasicNameValuePair("besser", var.get(6)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_frage_all, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (objekt.equals("feed")) {
                    params.add(new BasicNameValuePair("satz", goal));
                    params.add(new BasicNameValuePair("feed", var.get(0)));
                    Log.i("params", params.toString());
                    json = jParser.makeHttpRequest(update_frage_feed, "POST", params);


                    try {
                        // Checking for SUCCESS TAG
                        int success = json.getInt(TAG_SUCCESS);
                        Log.i("SUCCESS", Integer.toString(success));

                        if (success == 1) {
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }


        return true;

    }

}

package com.example.nicola.login;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.nicola.login.Login.LoginActivity;


public class LoadAllProducts {

    public LoadAllProducts() {

    }

    // Creating JSON Parser ob
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;
    static String listid;
    static String kursname;


    // JSON Node names
    private static final String TAG_KURSE = "kurse";
    private static final String TAG_NAME = "name";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LEHRER = "lehrer";
    private static final String TAG_VOCABELN = "vocabeln";
    private static final String TAG_PID = "pid";
    private static final String TAG_WORD = "word";
    private static final String TAG_TRANSLATION = "translation";
    private static final String TAG_LANGUAGE = "language";
    private static final String TAG_LISTE = "liste";
    private static final String TAG_LISTEN = "listen";
    private static final String TAG_KURS = "kurs";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_STUDENTS = "students";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FRAGEN = "fragen";
    private static final String TAG_SATZ = "satz";
    private static final String TAG_USER = "user";
    private static final String TAG_KORREKT = "korrekt";
    private static final String TAG_WORTE = "worte";
    private static final String TAG_KOMPLEX = "komplex";
    private static final String TAG_KREATIV = "kreativ";
    private static final String TAG_ANTWORT = "antwort";
    private static final String TAG_ANTUSER = "antUser";
    private static final String TAG_FEED = "feed";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_SCORE = "score";


    private static String url_all_kurse = "http://"+ LoginActivity.ip +"/android_vocabeln/get_all_kurse.php";
    private static String url_all_vocabeln = "http://"+LoginActivity.ip+"/android_vocabeln/get_all_vocabeln.php";
    private static String url_all_listen = "http://"+LoginActivity.ip+"/android_vocabeln/get_all_listen.php";
    private static String url_all_students = "http://"+LoginActivity.ip+"/android_vocabeln/get_all_students.php";
    private static String url_all_fragen = "http://"+LoginActivity.ip+"/android_vocabeln/get_all_fragen.php";


    // products JSONArray
    JSONArray products = null;

    public  ArrayList<HashMap<String, String>> loadData (String type, String var) throws Exception{
        productsList = new ArrayList<HashMap<String, String>>();

        Log.i("new", "start http_request");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject json = new JSONObject();
        Log.i("type", type);
        if (type.equals("liste")){
            kursname = Database.kurs;
            Log.i("Kursname", kursname);
            Log.i("kurs", kursname);
        } else if (type.equals("vokabel")){
            listid = Database.liste;
            Log.i("ListId", listid);
        } else if (type.equals("frage")){
            kursname = Database.fragen;
        }

        switch (type) {
            case "frage":

                // getting JSON string from URL
                Log.i("new", "start fragen http_request");
                json = jParser.makeHttpRequest(url_all_fragen, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("Fragen: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_FRAGEN);
                        Log.i("Load Fragen : userMail",Database.userMail);

                        //Gebe alle Texte, die noch nicht bewertet wurden (Worte==NULL)
                        //und die nicht vom aktuellen User sind (Benutzer sollen nicht ihre eigenen Texte bewerten)
                        if (kursname.equals("all")) {
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.isNull(TAG_WORTE)) {
                                    if (!c.getString(TAG_USER).equals(Database.userMail)) {

                                        // Storing each json item in variable
                                        String pid = c.getString(TAG_PID);
                                        String satz = c.getString(TAG_SATZ);
                                        String user = c.getString(TAG_USER);
                                        String gramatik = c.getString("gramatik");
                                        String worte = c.getString(TAG_WORTE);
                                        String komplex = c.getString(TAG_KOMPLEX);
                                        String kreativ = c.getString(TAG_KREATIV);
                                        String antwort = c.getString(TAG_ANTWORT);
                                        String antUser = c.getString(TAG_ANTUSER);
                                        String feed = c.getString("feed gut");
                                        String extra = c.getString(TAG_EXTRA);
                                        String besser = c.getString("verbesserung");
                                        String mode = c.getString("Textart");


                                        // creating new HashMap
                                        HashMap <String, String> map = new HashMap <String, String>();

                                        // adding each child node to HashMap key => value

                                        map.put(TAG_PID, pid);
                                        map.put(TAG_SATZ, satz);
                                        map.put(TAG_USER, user);
                                        map.put("gramatik", gramatik);
                                        map.put(TAG_WORTE, worte);
                                        map.put(TAG_KOMPLEX, komplex);
                                        map.put(TAG_KREATIV, kreativ);
                                        map.put(TAG_ANTWORT, antwort);
                                        map.put(TAG_ANTUSER, antUser);
                                        map.put("feed gut", feed);
                                        map.put(TAG_EXTRA, extra);
                                        map.put("verbesserung", besser);
                                        map.put("Textart", mode);

                                        // adding HashList to ArrayList
                                        productsList.add(map);
                                    }
                                }

                            }
                        }

                        //Gebe alle Fragen in denen das Feedback bereits bewertet wurde
                        //und in welchen das feedback vom aktuellen User stammt
                        else if (kursname.equals("rev")) {
                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_ANTUSER).equals(Database.userMail)) {
                                    if (!c.isNull("feed gut")) {

                                        // Storing each json item in variable
                                        String pid = c.getString(TAG_PID);
                                        String satz = c.getString(TAG_SATZ);
                                        String user = c.getString(TAG_USER);
                                        String gramatik = c.getString("gramatik");
                                        String worte = c.getString(TAG_WORTE);
                                        String komplex = c.getString(TAG_KOMPLEX);
                                        String kreativ = c.getString(TAG_KREATIV);
                                        String antwort = c.getString(TAG_ANTWORT);
                                        String antUser = c.getString(TAG_ANTUSER);
                                        String feed = c.getString("feed gut");
                                        String extra = c.getString(TAG_EXTRA);
                                        String besser = c.getString("verbesserung");
                                        String mode = c.getString("Textart");


                                        // creating new HashMap
                                        HashMap <String, String> map = new HashMap <String, String>();

                                        // adding each child node to HashMap key => value

                                        map.put(TAG_PID, pid);
                                        map.put(TAG_SATZ, satz);
                                        map.put(TAG_USER, user);
                                        map.put("gramatik", gramatik);
                                        map.put(TAG_WORTE, worte);
                                        map.put(TAG_KOMPLEX, komplex);
                                        map.put(TAG_KREATIV, kreativ);
                                        map.put(TAG_ANTWORT, antwort);
                                        map.put(TAG_ANTUSER, antUser);
                                        map.put("feed gut", feed);
                                        map.put(TAG_EXTRA, extra);
                                        map.put("verbesserung", besser);
                                        map.put("Textart", mode);

                                        // adding HashList to ArrayList
                                        productsList.add(map);
                                    }
                                }
                            }
                        }
                        //Gebe alle Texte aus die bereits bewertet wurden
                        //und welche vom aktuelle user stammen
                        else if (kursname.equals("fr")) {
                        // looping through All Products
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);
                            if (c.getString(TAG_USER).equals(Database.userMail)) {
                                if (!c.isNull(TAG_WORTE)) {
                                    if (c.isNull("feed gut")) {

                                        // Storing each json item in variable
                                        String pid = c.getString(TAG_PID);
                                        String satz = c.getString(TAG_SATZ);
                                        String user = c.getString(TAG_USER);
                                        String gramatik = c.getString("gramatik");
                                        String worte = c.getString(TAG_WORTE);
                                        String komplex = c.getString(TAG_KOMPLEX);
                                        String kreativ = c.getString(TAG_KREATIV);
                                        String antwort = c.getString(TAG_ANTWORT);
                                        String antUser = c.getString(TAG_ANTUSER);
                                        String feed = c.getString("feed gut");
                                        String extra = c.getString(TAG_EXTRA);
                                        String besser = c.getString("verbesserung");
                                        String mode = c.getString("Textart");


                                        // creating new HashMap
                                        HashMap <String, String> map = new HashMap <String, String>();

                                        // adding each child node to HashMap key => value

                                        map.put(TAG_PID, pid);
                                        map.put(TAG_SATZ, satz);
                                        map.put(TAG_USER, user);
                                        map.put("gramatik", gramatik);
                                        map.put(TAG_WORTE, worte);
                                        map.put(TAG_KOMPLEX, komplex);
                                        map.put(TAG_KREATIV, kreativ);
                                        map.put(TAG_ANTWORT, antwort);
                                        map.put(TAG_ANTUSER, antUser);
                                        map.put("feed gut", feed);
                                        map.put(TAG_EXTRA, extra);
                                        map.put("verbesserung", besser);
                                        map.put("Textart", mode);

                                        // adding HashList to ArrayList
                                        productsList.add(map);
                                }
                    }}
                }
            }

        } else {

        }

    } catch (JSONException e) {
        e.printStackTrace();
    }


            case "student":
                // getting JSON string from URL
                Log.i("new", "start student_http_request");
                json = jParser.makeHttpRequest(url_all_students, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("User: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        products = json.getJSONArray(TAG_STUDENTS);

                        //Falls var == kurs -> Lade alle Schuler des Kurses
                        //Aufgerufen von Lehrer und Studenten zum Überprüfen der in den Kurs eingeschriebenen Studenten
                        if (var.equals("kurs")) {
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_TYPE).equals("Schuler")) {
                                    if (c.getString(TAG_KURS).equals(Database.kurs)) {
                                        // Storing each json item in variable
                                        String email = c.getString(TAG_EMAIL);
                                        String password = c.getString(TAG_PASSWORD);
                                        String name = c.getString(TAG_NAME);
                                        String ty = c.getString(TAG_TYPE);
                                        String kurs = c.getString(TAG_KURS);
                                        String score = c.getString(TAG_SCORE);


                                        // creating new HashMap
                                        HashMap <String, String> map = new HashMap <String, String>();

                                        // adding each child node to HashMap key => value

                                        map.put(TAG_NAME, name);
                                        map.put(TAG_EMAIL, email);
                                        map.put(TAG_PASSWORD, password);
                                        map.put(TAG_TYPE, ty);
                                        map.put(TAG_KURS, kurs);
                                        map.put(TAG_SCORE, score);

                                        // adding HashList to ArrayList
                                        productsList.add(map);

                                    }
                                }
                            }
                        }
                        //Falls var == all -> Lade alle User (Schüler und Lehrer)
                        //Aufgerufen durch LogIn und Register zur Prüfung Einlog Daten
                        else if (var.equals("all")) {
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                // Storing each json item in variable
                                String email = c.getString(TAG_EMAIL);
                                String password = c.getString(TAG_PASSWORD);
                                String name = c.getString(TAG_NAME);
                                String ty = c.getString(TAG_TYPE);
                                String kurs = c.getString(TAG_KURS);
                                String score = c.getString(TAG_SCORE);
                                String rep = c.getString("reputation");
                                String komp = c.getString("durchschnitt komp");
                                String kre = c.getString("durchschnitt kre");
                                String punkt = c.getString("durchschnitt punkt");
                                String gram = c.getString("Fehler Gram");
                                String vok = c.getString("Fehler Vok");
                                String feed = c.getString("Durchschnitt Feed");
                                String zahl = c.getString("Anzahl Akt");

                                // creating new HashMap
                                HashMap <String, String> map = new HashMap <String, String>();

                                // adding each child node to HashMap key => value

                                map.put(TAG_NAME, name);
                                map.put(TAG_EMAIL, email);
                                map.put(TAG_PASSWORD, password);
                                map.put(TAG_TYPE, ty);
                                map.put(TAG_KURS, kurs);
                                map.put(TAG_SCORE, score);
                                map.put("rep", rep);
                                map.put("komp", komp);
                                map.put("kre", kre);
                                map.put("punkt", punkt);
                                map.put("gram", gram);
                                map.put("vok", vok);
                                map.put("feed", feed);
                                map.put("zahl", zahl);

                                // adding HashList to ArrayList
                                productsList.add(map);

                            }
                        }
                        //Falls var == me -> aktuallisiere Daten des aktuellen Users
                        //Aufgerufen durch FragenMenu zum überprüfen ob neue Punkte abgeholt werden können
                        else if (var.equals("me")) {
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_EMAIL).equals(Database.userMail)) {

                                    // Storing each json item in variable
                                    String email = c.getString(TAG_EMAIL);
                                    String password = c.getString(TAG_PASSWORD);
                                    String name = c.getString(TAG_NAME);
                                    String ty = c.getString(TAG_TYPE);
                                    String kurs = c.getString(TAG_KURS);
                                    String score = c.getString(TAG_SCORE);
                                    String rep = c.getString("reputation");
                                    String komp = c.getString("durchschnitt komp");
                                    String kre = c.getString("durchschnitt kre");
                                    String punkt = c.getString("durchschnitt punkt");
                                    String gram = c.getString("Fehler Gram");
                                    String vok = c.getString("Fehler Vok");
                                    String feed = c.getString("Durchschnitt Feed");
                                    String zahl = c.getString("Anzahl Akt");

                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value

                                    map.put(TAG_NAME, name);
                                    map.put(TAG_EMAIL, email);
                                    map.put(TAG_PASSWORD, password);
                                    map.put(TAG_TYPE, ty);
                                    map.put(TAG_KURS, kurs);
                                    map.put(TAG_SCORE, score);
                                    map.put("rep", rep);
                                    map.put("komp", komp);
                                    map.put("kre", kre);
                                    map.put("punkt", punkt);
                                    map.put("gram", gram);
                                    map.put("vok", vok);
                                    map.put("feed", feed);
                                    map.put("zahl", zahl);

                                    // adding HashList to ArrayList
                                    productsList.add(map);


                                }
                            }
                        }
                    }
                    else {}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "kurs":
                // getting JSON string from URL
                Log.i("new", "start kurs_http_request");
                json = jParser.makeHttpRequest(url_all_kurse, "GET", params);

                // Check your log cat for JSON reponse
               if (!(json==null)){
                Log.d("Kurse: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_KURSE);

                        // looping through All Products
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                                // Storing each json item in variable
                                String name = c.getString(TAG_NAME);
                                String lehrer = c.getString(TAG_LEHRER);
                                String language = c.getString(TAG_LANGUAGE);


                                // creating new HashMap
                                HashMap <String, String> map = new HashMap <String, String>();

                                // adding each child node to HashMap key => value

                                map.put(TAG_NAME, name);
                                map.put(TAG_LEHRER, lehrer);
                                map.put(TAG_LANGUAGE, language);

                                // adding HashList to ArrayList
                                productsList.add(map);

                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }}
                break;
            case "liste":
                Log.d("All  ", url_all_listen);
                Log.d("All  ", params.toString());
                Log.i("new", "start listen_http_request");
                json = jParser.makeHttpRequest(url_all_listen, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("Listen: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_LISTEN);

                        if (kursname.equals("all")){
                            Log.i("FFFFFFFFFF","ALL");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_KURS).equals(Database.userKurs)) {


                                    // Storing each json item in variable
                                String id = c.getString(TAG_PID);
                                String name = c.getString(TAG_NAME);
                                String kurs = c.getString(TAG_KURS);


                                // creating new HashMap
                                HashMap <String, String> map = new HashMap <String, String>();

                                // adding each child node to HashMap key => value
                                map.put(TAG_PID, id);
                                map.put(TAG_NAME, name);
                                map.put(TAG_KURS, kurs);

                                // adding HashList to ArrayList
                                productsList.add(map);
                            }}
                        }
                        else {
                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_KURS).equals(kursname)) {


                                    // Storing each json item in variable
                                    String id = c.getString(TAG_PID);
                                    String name = c.getString(TAG_NAME);
                                    String kurs = c.getString(TAG_KURS);


                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value
                                    map.put(TAG_PID, id);
                                    map.put(TAG_NAME, name);
                                    map.put(TAG_KURS, kurs);

                                    // adding HashList to ArrayList
                                    productsList.add(map);
                                }
                            }
                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "vokabel":
                Log.d("All  ", url_all_vocabeln);
                Log.d("All  ", params.toString());
                Log.i("new", "start vokabel_http_request");
                json = jParser.makeHttpRequest(url_all_vocabeln, "GET", params);

                // Check your log cat for JSON reponse
                Log.d("All Products: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_VOCABELN);
                        if (listid.equals("all")) {
                            Log.i("FFFFFFFFFF", "ALL");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (true) {


                                    // Storing each json item in variable
                                    String id = c.getString(TAG_PID);
                                    String word = c.getString(TAG_WORD);
                                    String translation = c.getString(TAG_TRANSLATION);
                                    String description = c.getString("description");


                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value
                                    map.put(TAG_PID, id);
                                    map.put(TAG_WORD, word);
                                    map.put(TAG_TRANSLATION, translation);
                                    map.put("description", description);

                                    // adding HashList to ArrayList
                                    productsList.add(map);
                                }
                            }
                        }
                        if (listid.equals("kurs")) {
                            Log.i("Load ", "KURS");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString("description").equals(Database.userKurs)) {


                                    // Storing each json item in variable
                                    String id = c.getString(TAG_PID);
                                    String word = c.getString(TAG_WORD);
                                    String translation = c.getString(TAG_TRANSLATION);
                                    String description = c.getString("description");


                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value
                                    map.put(TAG_PID, id);
                                    map.put(TAG_WORD, word);
                                    map.put(TAG_TRANSLATION, translation);
                                    map.put("description", description);

                                    // adding HashList to ArrayList
                                    productsList.add(map);
                                }
                            }
                        }
                        else {
                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);
                                if (c.getString(TAG_LISTE).equals(listid)) {


                                    // Storing each json item in variable
                                    String id = c.getString(TAG_PID);
                                    String word = c.getString(TAG_WORD);
                                    String translation = c.getString(TAG_TRANSLATION);
                                    String description = c.getString("description");


                                    // creating new HashMap
                                    HashMap <String, String> map = new HashMap <String, String>();

                                    // adding each child node to HashMap key => value
                                    map.put(TAG_PID, id);
                                    map.put(TAG_WORD, word);
                                    map.put(TAG_TRANSLATION, translation);
                                    map.put("description", description);

                                    // adding HashList to ArrayList
                                    productsList.add(map);
                                }
                            }
                        }
                        Log.d("Liste ", listid);
                        Log.d("All Vok: ", productsList.toString());
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

        return productsList;

    }

}

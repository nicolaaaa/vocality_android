package com.example.nicola.login.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.nicola.login.Database;
import com.example.nicola.login.LoadAllProducts;
import com.example.nicola.login.Login.LoginActivity;
import com.example.nicola.login.MainLehrerActivity;
import com.example.nicola.login.MainSchulerActivity;
import com.example.nicola.login.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class GetAllActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;


    LoadAllProducts loader = new LoadAllProducts();

    //Liste in die geforderte Elemente geladen werden
    ArrayList<HashMap<String, String>> loadList;

    //Typ der aktuell geladenen Listenelemente
    static String Listentype;
    //Ausgewähltes Element auf höherer Ebene, von dem aktuell Details angezeigt werden
    static String Ordner;

    private static final String TAG_NAME = "name";
    private static final String TAG_PID = "pid";
    private static final String TAG_WORD = "word";
    private static final String TAG_TRANSLATION = "translation";
    private static final String TAG_KURS = "kurs";
    private static final String TAG_SCORE = "score";
    boolean error=false;


    public void onCreate(Bundle savedInstanceState) {

        //Design un Layout aufbauen

        //richtiges Design beibehalten
        super.onCreate(savedInstanceState);
        //Behalte richtiges Design bei
        if(Database.theme.equals("color")) {
            setTheme(R.style.AppTheme_My);
            if (Database.userType.equals("Schuler")) {
                //Bei userType Schuler Nutze Layout ohne Erstell Button
                setContentView(R.layout.activity_get_all_sch);
            }else
                setContentView(R.layout.activity_get_all);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.sun_clouds));
        }
        else {
            setTheme(R.style.AppTheme_MyDark);
            if (Database.userType.equals("Schuler")) {
                //Bei userType Schuler Nutze Layout ohne Erstell Button
                setContentView(R.layout.activity_get_all_sch);
            }else
                setContentView(R.layout.activity_get_all);
            LinearLayout bg = findViewById(R.id.mainLehrer);
            bg.setBackground(getDrawable(R.drawable.unnamed));
        }
        Log.i("GetAll", "start get_all");

        //Speichere übergebenen Type parameter in variable Listentype
        Intent intent = getIntent();
        Listentype = intent.getStringExtra("type");
        Log.d("GetAll Type: ", Listentype);
        Log.i("GetAll DB-type",com.example.nicola.login.Database.userType);

        //Erstelle loadList
        loadList = new ArrayList<HashMap<String, String>>();

        createToolbar();


        //suche für entsprechenen ListenTypen Ordner aus Datenbank in dem sich listenElemente befinden
        //Beispiel: Für Vokabel suche speichere aktuelle Vokabelliste als Ordner
        switch (Listentype) {
            case "liste":
                Ordner = Database.kurs;
                Log.i("GetAll Variable Db", Ordner);
                break;
            case "vokabel":
                Ordner = Database.liste;
                break;
        }

        //Lade benötigte Daten und Stelle sie in Liste dar
        new Load().execute();

        //Erhalten Detailierter Informationen durch Klicken auf ListItems
        ListView lv = findViewById(R.id.list);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getDetails(view, position, id);
            }
        });
    }

    public void createToolbar(){
        //Ermittle UserType um über Rechte auf Erstellen/Verändern der Listen sowie Layout zu entscheiden
        if (Database.userType.equals("Schuler")) {


            //Initiiere Toolbar
            Toolbar myChildToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myChildToolbar);
            getSupportActionBar().setTitle(Listentype);

            //Aktiviere Zurück Button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            //Weise Homebutton eigene Funktion zu
            myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Einführung Verschiedener Ebenen (stack) für IneinanderVerschachtelte Listen
                        // stack 0 = Vokabellisten, stack 2 = Vokabeln, stack 1 = Bedeutungen
                        //Initialwert immer 0

                        //Falls wir auf GrundEbene sind(Vokabellisten) -> Home Button zu Schuler Main Menu
                        if (Database.stack == 0) {
                            startActivity(new Intent(getApplicationContext(), MainSchulerActivity.class));
                        }
                        //Auf erster Ebene(Bedeutungen) -> Home Button zu zweiter Ebene
                        else if (Database.stack == 1) {
                            Intent in = new Intent(getApplicationContext(), GetAllActivity.class);
                            in.putExtra("type", "vokabel");
                            Database.stack = 2;
                            startActivity(in);
                        }
                        //Auf zweiter Ebene(Vokabeln) -> Home Button zu Grundebene
                        else if (Database.stack == 2) {
                            Intent in = new Intent(getApplicationContext(), GetAllActivity.class);
                            in.putExtra("type", "liste");
                            Database.stack = 0;
                            startActivity(in);
                        }

                }
            });
        }
        else {
            //falls userTyp = Lehrer, wähle Layout mit Erstelle Button
            //außer bei Schüler Liste

            //Initiiere Toolbar
            Toolbar myChildToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myChildToolbar);

            //Initiiere Homebutton
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(Listentype);

            //Lege eigene Funktionen je nach Ebene(stack) fest
            myChildToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Database.stack == 0) {
                        startActivity(new Intent(getApplicationContext(), MainLehrerActivity.class));
                    } else if (Database.stack == 1) {
                        Intent in = new Intent(getApplicationContext(), GetAllActivity.class);
                        in.putExtra("type", "vokabel");
                        Database.stack = 2;
                        startActivity(in);
                    } else if (Database.stack == 2) {
                        Intent in = new Intent(getApplicationContext(), GetAllActivity.class);
                        in.putExtra("type", "liste");
                        Database.stack = 0;
                        startActivity(in);
                    }
                }
            });

        }
    }

    public void getDetails(View view, int position, long id){
        Intent in = new Intent(getApplicationContext(),
                GetAllActivity.class);

        switch (Listentype) {
            //Gehe von den Vokabellisten zu den Vokabeln in der ausgeählten Liste
            //Speichere aktuelle var Variable unter oldId um Rückkehr in ListEbene zu Erlaichtern
            case "liste":
                com.example.nicola.login.Database.liste = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
                in.putExtra("type", "vokabel");
                Database.oldId=Ordner;
                Database.stack = 2;
                startActivity(in);
                break;

            //Gehe von den Vokabeln zu den Übersetzungen und Details der ausgeählten Vokabel
            //Speichere aktuelle var Variable unter liste um Rückkehr zu Vokabeln zu Erleichtern
            case "vokabel":
                ListView lv = findViewById(R.id.list);
                String word = ((TextView) view.findViewById(R.id.name)).getText()
                        .toString();

                //Lade Liste der Übersetzungen für ausgewählte Vokabel (In Form von String) und Wandle sie in StringArray um
                //Dazu Teilen des Strings an jedem ","
                String trans = ((TextView) view.findViewById(R.id.kurs)).getText()
                        .toString();
                String[] parts = trans.split(",");


                //Erstelle Liste listi durch Zusammenfügen der Liste der Übersetzungen und der Beispiele
                ArrayList<HashMap<String,String>> listi = new ArrayList <>();
                for (int i=0;i<parts.length;i++){
                    HashMap<String,String> has = new HashMap <>();
                    has.put("TRANS",parts[i]);
                    listi.add(has);
                }

                //Stelle listi durch listAdapter auf GUI dar
                ListAdapter adapter = new SimpleAdapter(
                        GetAllActivity.this, listi,
                        R.layout.list_item, new String[] { "TRANS",},
                        new int[] { R.id.name });
                lv.setAdapter(adapter);

                Listentype="not";
                getSupportActionBar().setTitle(word);

                //Falls userTyp Lehrer, Entferne Erstellen Button
                if (Database.userType.equals("Lehrer")) {
                    Button bt = findViewById(R.id.newButton);
                    bt.setVisibility(View.GONE);
                }

                Database.stack = 1;
                Database.liste=Ordner;
                break;
        }
    }

    //Gebe Lehrer Möglichkeit zum Löschen von Listen Elementen
    //Bei gedrückt halten auf ListItem erscheint Menu mit Action Löschen
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (Database.userType.equals("Lehrer")) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Löschen");
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    //Falls Löschen ausgewählt wird, Erstelle Fenster, das dich nach Bestätigung der Aktion fragt
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if(item.getTitle()=="Löschen") {
            int id = info.position;
            AlertDialog.Builder builder1 = new AlertDialog.Builder(GetAllActivity.this);
            builder1.setMessage("Are you sure?");
            builder1.setCancelable(true);

            //wird Löschen bestätigt wird Funktion DeleteAktivity gestartet und auf Antwort gewartet
            // nötige Parameter werden in Form von zu löschendem Objekt und Typ des Objekts übergeben
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            switch (Listentype) {
                                case "liste":
                                    Ordner = ((TextView) info.targetView.findViewById(R.id.pid)).getText()
                                            .toString();
                                    ListView lv = findViewById(R.id.list);
                                    Log.i("SUCCESS", Ordner);

                                    Intent in = new Intent(getApplicationContext(), DeleteActivity.class);
                                    in.putExtra("var", Ordner);
                                    in.putExtra("type", Listentype);

                                    startActivityForResult(in, 100);
                                    break;
                                case "vokabel":
                                    String ide = ((TextView) info.targetView.findViewById(R.id.pid)).getText()
                                            .toString();
                                    lv = findViewById(R.id.list);
                                    Log.i("SUCCESS", ide);

                                    in = new Intent(getApplicationContext(), DeleteActivity.class);
                                    in.putExtra("var", ide);
                                    in.putExtra("type", Listentype);

                                    startActivityForResult(in, 100);
                                    break;
                            }
                        }
                    })
                    //Bei Verneinung wird Fenster geschlossen
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            return false;
        }
        return true;
    }

    //Funktionsaufruf bei Klicken auf Neu Erstellen Button
    //type übergabe und starten der Funktion NewAktivity
    public void neu(View view){
        Intent in = new Intent(getApplicationContext(), NewActivity.class);
        switch (Listentype) {
            case "liste":
                in.putExtra("type", Listentype);
                startActivity(in);
                break;
            case "vokabel":
                in.putExtra("type", Listentype);
                startActivity(in);
                break;
        }
    }

    //Bei Empfangen von Rückgabewert des Löschen Aufrufs
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Database.getInstance().deleteVoc(Ordner);
            new Load().execute();
        }
    }

    //Laden der Benötigten Listen Elemente
    //Funktion Load als AsyncTask um System bei nicht vorhandener Internetverbindung nicht ganze App zu stoppen
    class Load extends AsyncTask<String, String, String> {


         //Before starting background thread Show Progress Dialog
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GetAll", "start loading products");
            pDialog = new ProgressDialog(GetAllActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        //Lade benötigte Daten
        protected String doInBackground(String... args) {
            switch (Listentype) {
                //Lade Schuler aus Kurs für Bestenliste
                //hierbei wird immer downgeloaded, da sich die Daten zu oft verändern um sinnvoll Zwischengespeichert zu werden
                case "student":
                    //Database.language zeigt an, dass Anfrage von Lehrer kommt
                    //lade alle Schuler des Kurses
                    if (Database.userType.equals("Lehrer")) {
                        GetAllActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Button bt = findViewById(R.id.newButton);
                                bt.setVisibility(View.GONE);
                            }
                        });
                    }
                            Ordner = "kurs";
                    try {
                        loadList = loader.loadData(Listentype, Ordner);
                    }
                    catch (Exception e){
                        GetAllActivity.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {

                                Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                                error=true;

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(GetAllActivity.this);
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
                    if (error==false)
                        Database.getInstance().addList(Ordner,loadList);
                    Log.i("GetAll LOAD", "download");
                    break;
                //Lade Liste aller Vokabellisten für ausgewählten Kurs(Ordner)
                case "liste":
                    //Schaue, ob aktuelle Liste noch im Zwischenspeicher
                    //Falls nicht, downloade sie und speichere im Zwischenspeicher
                    if (Database.getInstance().getList(Ordner)==null) {
                        try {
                            loadList = loader.loadData(Listentype, Ordner);
                        }
                        catch (Exception e){
                            GetAllActivity.this.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {

                                    Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                                    error=true;

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GetAllActivity.this);
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
                        if (error==false)
                            Database.getInstance().addList(Ordner,loadList);
                    }
                    //falls doch lade aus zwischenspeicher
                    else {
                            loadList = Database.getInstance().getList(Ordner);
                    }
                    break;
                //Lade Liste aller Vokabeln für ausgewählte Vokabelliste(Ordner)
                case "vokabel":
                    //Schaue, ob aktuelle Liste noch im Zwischenspeicher
                    if (Database.getInstance().getVoc(Ordner)==null) {
                        try {
                            loadList = loader.loadData(Listentype, Ordner);
                        }
                        catch (Exception e){
                            GetAllActivity.this.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {

                                    Log.d("Liste ", "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                                    error=true;

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(GetAllActivity.this);
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
                        if (error==false)
                            Database.getInstance().addVoc(Ordner,loadList);

                    }else {
                        loadList = Database.getInstance().getVoc(Ordner);
                        Log.i("GetAll LOAD", "aus db");
                        Log.i("GetAll LOAD", Database.getInstance().getVoc(Ordner).toString());
                    }
                    break;
            }
           return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss progress dialog
            pDialog.dismiss();
            if(error==false) {
                // updating UI from Background Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        ListView lv = findViewById(R.id.list);

                        //Stelle geladene Daten in Liste auf der Benutzeroberfläche da
                        switch (Listentype) {
                            //Für die Bestenliste sollen Studentenname und level nebeneinander in derselben Zeile dargestellt werden
                            // hierzu wird die ursprüngliche Liste der heruntergeladenen Studentendaten in eine neue Liste listi umgeformt
                            case "student":
                                Log.i("GET all produktlist", loadList.toString());
                                ArrayList <HashMap <String, String>> listi = new ArrayList <>();
                                for (int i = 0; i < loadList.size(); i++) {
                                    HashMap <String, String> has = new HashMap <>();
                                    Log.i("GET all item", loadList.get(i).get(TAG_NAME));
                                    String ll = Integer.toString(getLevel(Double.parseDouble(loadList.get(i).get(TAG_SCORE))));
                                    Log.i("GET all item", Integer.toString(i));
                                    Log.i("GET all item", ll);
                                    has.put("ITEM", loadList.get(i).get(TAG_NAME) + "     Level: " + ll);
                                    has.put("LV", ll);
                                    has.put("KURS", loadList.get(i).get(TAG_KURS));
                                    listi.add(has);
                                }

                                //Sortiere die Elemente von listi nach Höhe des Levels
                                Collections.sort(listi, new Comparator <HashMap <String, String>>() {
                                    public int compare(HashMap <String, String> obj1, HashMap <String, String> obj2) {
                                        return (Integer.parseInt(obj1.get("LV")) > Integer.parseInt(obj2.get("LV"))) ? -1 : (Integer.parseInt(obj1.get("LV")) > Integer.parseInt(obj2.get("LV"))) ? 1 : 0;
                                    }
                                });

                                //Stelle listi durch listAdater auf GUI dar
                                Log.i("GET all list", listi.toString());
                                ListAdapter adapter = new SimpleAdapter(
                                        GetAllActivity.this, listi,
                                        R.layout.list_item, new String[]{
                                        "ITEM"},
                                        new int[]{R.id.name});
                                lv.setAdapter(adapter);
                                break;

                            //Stelle Liste der geladenen Kurse durch listAdater auf GUI dar
                            case "kurs":
                                adapter = new SimpleAdapter(
                                        GetAllActivity.this, loadList,
                                        R.layout.list_item, new String[]{
                                        TAG_NAME, "lehrer", "language"},
                                        new int[]{R.id.name, R.id.pid, R.id.kurs});
                                // updaten des Layouts durch updaten listview
                                lv.setAdapter(adapter);
                                break;

                            //Stelle Liste der geladenen Vokabellisten durch listAdater auf GUI dar
                            case "liste":
                                adapter = new SimpleAdapter(
                                        GetAllActivity.this, loadList,
                                        R.layout.list_item, new String[]{TAG_PID,
                                        TAG_NAME, TAG_KURS},
                                        new int[]{R.id.pid, R.id.name, R.id.kurs});
                                // updating listview
                                lv.setAdapter(adapter);
                                break;

                            //Stelle Liste der geladenen Vokabeln durch listAdater auf GUI dar
                            case "vokabel":
                                adapter = new SimpleAdapter(
                                        GetAllActivity.this, loadList,
                                        R.layout.list_item, new String[]{TAG_PID,
                                        TAG_WORD, TAG_TRANSLATION},
                                        new int[]{R.id.pid, R.id.name, R.id.kurs});
                                // updating listview
                                lv.setAdapter(adapter);
                                break;
                        }
                    }
                });
            }
            error=false;

        }

    }

    public int getLevel(double score){
        int i;
        for (i = 1; score>=getMaxExp(i);i++)
            score = score-getMaxExp(i);
        return i;
    }

    public int getMaxExp(int lv){
        return 100+lv*20;
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
            builder1.setMessage("Hier ist die Liste der für deinen Kurs zur Verfügung stehenden Vokabeln. "+
                            "Die Vokablelisten können nur vom Tutor deines Kurses geändert werden!");
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


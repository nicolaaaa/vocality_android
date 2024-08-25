package com.example.nicola.login;

import android.app.LauncherActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    public static String userName;
    public static String userType;
    public static String userMail;
    public static String userKurs;
    public static double userScore = 0;
    public static double userZahl = 0;
    public static double userRep = 0;
    public static double userKom = 0;
    public static double userKre = 0;
    public static double userPunk = 0;
    public static double userFehl = 0;
    public static double userFehl2 = 0;
    public static double userBon = 0;
    public static double userFeed = 0;
    public static String kurs;
    public static String liste;
    public static String fragen;
    public static String rep;
    public static String fragen2;
    public static String theme="color";
    public static  String oldId;
    public static int stack=0;
    public static String language;
    public static boolean test;
    public static boolean created;

    private ArrayList <HashMap <String, String>> kursItems = null;
    private HashMap <String, ArrayList <HashMap <String, String>>> listItems = null;
    private HashMap <String, ArrayList <HashMap <String, String>>> vocItems = null;

    private static Database mInstance;
    private ArrayList <String> list = null;

    public static Database getInstance() {
        if (mInstance == null) {
            mInstance = new Database();
        }
        return mInstance;
    }

    private Database() {
    }

    public void create () {
        if (created==false) {
            Database.getInstance().listItems = new HashMap <String, ArrayList <HashMap <String, String>>>();
            Database.getInstance().vocItems = new HashMap <String, ArrayList <HashMap <String, String>>>();
            created=true;
        }
    }

    public int getMaxExp(int lv){
        return 100+lv*20;
    }

    public int getLevel(){
        int i;
        double score=userScore;
        for (i = 1; score>=getMaxExp(i);i++)
            score = score-getMaxExp(i);
        return i;
    }

    public double getExp(){
        int i;
        double score=userScore;
        for (i = 1; score>=getMaxExp(i);i++)
            score = score-getMaxExp(i);
        return score;
    }

    // retrieve array from anywhere
    public ArrayList <HashMap <String, String>> getKurse() {
        if (kursItems!=null) {
            Log.i("Database getKurs: ", kursItems.toString());
        }
        return this.kursItems;
    }

    //Add element to array
    public void addKurse(ArrayList <HashMap <String, String>> value) {
        kursItems = value;
        Log.i("Database Kurse: " ,kursItems.toString());
    }

    public void deleteKurs(){
        Log.i("Database DELETE  " ,"kurs");
        kursItems=null;
    }

    public ArrayList <HashMap <String, String>> getList (String var) {
        if (listItems!=null ) {
            Log.i("Database List DB",listItems.toString());
            Log.i("Database Contains DB",Boolean.toString(listItems.containsKey(var)));
            if (listItems.containsKey(var)) {
                Log.i("Database getList: ", listItems.get(var).toString());
                return listItems.get(var);
            }
            else return null;
        }
        else return null;
    }

    public void addList (String var, ArrayList <HashMap <String, String>> list){
            listItems.put(var,list);
            Log.i("Database Listen: " ,listItems.toString());
    }

    public void deleteList (String var){
        if (listItems!=null && listItems.containsKey(var)) {
            listItems.remove(var);
            Log.i("Database DLETE!!!!!!!!","list");
        }
    }

    public ArrayList <HashMap <String, String>> getVoc (String var) {
        if (vocItems!=null && vocItems.containsKey(var)) {
            Log.i("Database getVoc: ", vocItems.get(var).toString());
            return vocItems.get(var);
        }
        else return null;

    }

    public void addVoc (String var, ArrayList <HashMap <String, String>> list){
            vocItems.put(var,list);
            Log.i("Database Vokablen: " ,vocItems.toString());
    }

    public void deleteVoc (String var){
        if (vocItems!=null && vocItems.containsKey(var)) {
            vocItems.remove(var);
            Log.i("Database DELETE!!","vokabel");
        }
    }
}
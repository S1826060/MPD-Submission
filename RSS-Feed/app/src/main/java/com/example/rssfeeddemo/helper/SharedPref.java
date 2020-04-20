package com.example.rssfeeddemo.helper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.rssfeeddemo.R;

//S1826060 Scott Derek Robertson
public class SharedPref {
    private Context context;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    public SharedPref(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences("com.example.rssfeed.MY_PREF", Context.MODE_PRIVATE);
    }


    public void setNearMeValue(int value){
        editor = sharedPref.edit();
        editor.putInt("NEAR_ME", value);
        editor.apply();
    }

    public int getNearMeValue(){
        return sharedPref.getInt("NEAR_ME", 0);
    }
}

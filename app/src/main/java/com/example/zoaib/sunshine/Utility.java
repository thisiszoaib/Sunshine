package com.example.zoaib.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Zoaib on 6/27/2015.
 */
public class Utility {
    public static String getPreferredLocation(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String location = prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
        return location;
    }
}


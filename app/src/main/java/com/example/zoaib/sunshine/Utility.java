package com.example.zoaib.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.zoaib.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

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

    public static boolean isMetric(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))) == context.getString(R.string.pref_units_metric);
    }

    public static String formatTemperature(double temperature,Context context)
    {
        double temp;
        if(!isMetric(context))
        {
            temp = 9*temperature/5+32;
        }
        else
        {
            temp = temperature;
        }
        return String.format("%.0f",temp);
    }

    public static String formatDate(String dateString )
    {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);

    }


}


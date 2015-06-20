package com.example.zoaib.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.zoaib.sunshine.data.WeatherContract.LocationEntry;
import com.example.zoaib.sunshine.data.WeatherContract.WeatherEntry;
import com.example.zoaib.sunshine.data.WeatherDBHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Zoaib on 6/20/2015.
 */
public class TestDb extends AndroidTestCase {
    public void testCreateDb() throws Throwable
    {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }

    public void testInsertReadDb()
    {
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase();

        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME,null,values);

        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst())
        {
            validateCursor(values,cursor);
        }

        ContentValues weatherValues = getWeatherContentValues(locationRowId);

        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME,null,weatherValues);
        assertTrue(weatherRowId != -1);

        Cursor weatherCursor = db.query(
                WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if(weatherCursor.moveToFirst())
        {
            validateCursor(weatherValues,weatherCursor);
        }

    }

    static public void validateCursor(ContentValues expectedValues,Cursor valueCursor)
    {
        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String,Object> entry : valueSet)
        {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);

            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(idx);
            assertEquals(expectedValue, actualValue);

        }

    }

    ContentValues getLocationContentValues()
    {
        String testName = "North Pole";
        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME,testName);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING,testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT,testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG,testLongitude);

        return values;
    }

    ContentValues getWeatherContentValues(long locationRowId)
    {

        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_LOC_KEY,locationRowId);
        values.put(WeatherEntry.COLUMN_DATETEXT,"20141205");
        values.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        values.put(WeatherEntry.COLUMN_HUMIDITY,1.2);
        values.put(WeatherEntry.COLUMN_PRESSURE,1.3);
        values.put(WeatherEntry.COLUMN_MAX_TEMP,75);
        values.put(WeatherEntry.COLUMN_MIN_TEMP,65);
        values.put(WeatherEntry.COLUMN_WIND_SPEED,5.5);
        values.put(WeatherEntry.COLUMN_WEATHER_ID,321);
        values.put(WeatherEntry.COLUMN_SHORT_DESC,"Asteroid");


        return values;
    }







}

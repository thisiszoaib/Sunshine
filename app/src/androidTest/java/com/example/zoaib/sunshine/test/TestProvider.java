package com.example.zoaib.sunshine.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.zoaib.sunshine.data.WeatherContract.LocationEntry;
import com.example.zoaib.sunshine.data.WeatherContract.WeatherEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by Zoaib on 6/20/2015.
 */
public class TestProvider extends AndroidTestCase {

    static public String TEST_CITY_NAME = "North Pole";
    static public String TEST_LOCATION = "99705";
    static public String TEST_DATE = "20141205";

    public void testDeleteAllRecords()
    {
        mContext.getContentResolver().delete(WeatherEntry.CONTENT_URI,
                null,
                null);

        mContext.getContentResolver().delete(LocationEntry.CONTENT_URI,
                null,
                null);

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(), 0);

        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(),0);

        cursor.close();

        }

    public void testType()
    {
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);

        assertEquals(type,WeatherEntry.CONTENT_TYPE);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));

        assertEquals(type, WeatherEntry.CONTENT_TYPE);

        String testDate = "20150612";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));

        assertEquals(type,WeatherEntry.CONTENT_ITEM_TYPE);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals(type, LocationEntry.CONTENT_TYPE);

        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        assertEquals(type, LocationEntry.CONTENT_ITEM_TYPE);


    }



    public void testInsertReadProvider()
    {
        testDeleteAllRecords();

        ContentValues values = getLocationContentValues();

        long locationRowId;
        Uri insertUri = mContext.getContentResolver().insert(
                LocationEntry.CONTENT_URI,values);
        locationRowId = ContentUris.parseId(insertUri);


        Cursor cursor = mContext.getContentResolver().query(LocationEntry.buildLocationUri(locationRowId),
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst())
        {
            validateCursor(values,cursor);
        }


        ContentValues weatherValues = getWeatherContentValues(locationRowId);

        long weatherRowId;

        insertUri = mContext.getContentResolver().insert(
                WeatherEntry.CONTENT_URI, weatherValues);
        weatherRowId = ContentUris.parseId(insertUri);

        Cursor weatherCursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if(weatherCursor.moveToFirst())
        {
            validateCursor(weatherValues,weatherCursor);
        }

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                null,
                null,
                null,
                null);

        if(weatherCursor.moveToFirst())
        {
            validateCursor(weatherValues,weatherCursor);
        }

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TEST_LOCATION,TEST_DATE),
                null,
                null,
                null,
                null);

        assertEquals(1,weatherCursor.getCount());

        if(weatherCursor.moveToFirst())
        {
            validateCursor(weatherValues,weatherCursor);
        }

        weatherCursor.close();

        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(
                        TEST_LOCATION,TEST_DATE),
                null,
                null,
                null,
                null);

        assertEquals(1, weatherCursor.getCount());

        if(weatherCursor.moveToFirst())
        {
            validateCursor(weatherValues,weatherCursor);
        }

        testDeleteAllRecords();
    }

    public void testUpdateLocation() {
        testDeleteAllRecords();

        ContentValues values = getLocationContentValues();

        Uri insertUri = mContext.getContentResolver().insert(
                LocationEntry.CONTENT_URI,
                values
        );

        long locationRowId = ContentUris.parseId(insertUri);

        ContentValues values2 = new ContentValues(values);

        values2.put(LocationEntry.COLUMN_CITY_NAME, "Santa Clause City");
        values2.put(LocationEntry._ID, locationRowId);

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI,
                values2,
                LocationEntry._ID + " = ? ",
                new String[]{Long.toString(locationRowId)}
        );

        assertEquals(count, 1);

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                LocationEntry._ID + " = ? ",
                new String[]{Long.toString(locationRowId)},
                null
        );

        if (cursor.moveToFirst()) {
            validateCursor(values2,cursor);
        }
        else
        {
            fail("Rows not returned.");
        }

        cursor.close();

        testDeleteAllRecords();
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
        String testName = TEST_CITY_NAME;
        String testLocationSetting = TEST_LOCATION;
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
        values.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        values.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        values.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        values.put(WeatherEntry.COLUMN_MIN_TEMP,65);
        values.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        values.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        values.put(WeatherEntry.COLUMN_SHORT_DESC,"Asteroid");


        return values;
    }







}

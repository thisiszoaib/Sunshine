package com.example.zoaib.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zoaib.sunshine.data.WeatherContract;

import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String mLocation;
    private static final int FORECAST_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int COL_WEATHER_UNIQUE_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_ID = 6;


    private CursorAdapter mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateWeather();
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.action_refresh)
        {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
       super.onStart();
       //updateWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocation != null && !Utility.getPreferredLocation(getActivity()).equals(mLocation)) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    private void updateWeather()
    {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        weatherTask.execute(Utility.getPreferredLocation(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //List<String> weekForecast;
        //weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        mForecastAdapter = new ForecastAdapter(
                getActivity(),null,0
        );

//        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                switch(columnIndex)
//                {
//                    case COL_WEATHER_MAX_TEMP:
//                    case COL_WEATHER_MIN_TEMP:
//                        ((TextView)view).setText(Utility.formatTemperature(cursor.getDouble(columnIndex),getActivity()));
//                        return true;
//
//                    case COL_WEATHER_DATE:
//                        String dateString = cursor.getString(columnIndex);
//                        TextView dateView = (TextView)view;
//                        dateView.setText(Utility.formatDate(dateString));
//                        return true;
//                }
//                return false;
//            }
//        });



        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ForecastAdapter adapter = (ForecastAdapter)parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                if(cursor != null && cursor.moveToPosition(position))
                {
                    String dateString = cursor.getString(COL_WEATHER_DATE);
                    //Toast.makeText(getActivity(), dateString, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT,dateString);
                    startActivity(intent);
                };
                //Toast.makeText(getActivity(),forecast, Toast.LENGTH_SHORT).show();


            }
        });

        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String startDate = WeatherContract.getDbDateString(new Date());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate
        );

        Log.d("URI: ",mLocation + " " + weatherForLocationUri.toString());

        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mForecastAdapter.swapCursor(null);
    }

}

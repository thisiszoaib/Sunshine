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
import com.example.zoaib.sunshine.sync.SunshineSyncAdapter;

import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SELECTED_KEY = "selected";
    private String mLocation;
    private int mPosition;
    private static final int FORECAST_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    public static final int COL_WEATHER_UNIQUE_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_ID = 6;
    public static final int COL_LOCATION_LAT = 7;
    public static final int COL_LOCATION_LONG = 8;



    private CursorAdapter mForecastAdapter;
    private ListView mListView;
    private boolean mUseTodayLayout;

    public void setUseTodayLayout(boolean useToday) {
        mUseTodayLayout = useToday;
        if(mForecastAdapter != null)
        {
            ((ForecastAdapter)mForecastAdapter).setUseTodayLayout(mUseTodayLayout);
        }
    }

    public interface Callback {
        public void onItemSelected(String date);
    }

    public ForecastFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //updateWeather();
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
//        if(id == R.id.action_refresh)
//        {
//            updateWeather();
//            return true;
//        }

        if(id==R.id.action_map)
        {
            openPreferredLocationInMap();
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
        SunshineSyncAdapter.syncImmediately(getActivity());

//        Intent alarmIntent = new Intent(getActivity(),SunshineService.AlarmReceiver.class);
//        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, mLocation);
//
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,
//                PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,pi);


        //FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        //weatherTask.execute(Utility.getPreferredLocation(getActivity()));
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
        ((ForecastAdapter)mForecastAdapter).setUseTodayLayout(mUseTodayLayout);

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


        mListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ForecastAdapter adapter = (ForecastAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(
                            cursor.getString(COL_WEATHER_DATE)
                    );
//                    String dateString = ;
//                    //Toast.makeText(getActivity(), dateString, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(),DetailActivity.class)
//                            .putExtra(Intent.EXTRA_TEXT,dateString);
//                    startActivity(intent);
                }
                ;
                mPosition = position;
                //Toast.makeText(getActivity(),forecast, Toast.LENGTH_SHORT).show();


            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

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
        if(mPosition != ListView.INVALID_POSITION)
        {
            mListView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void openPreferredLocationInMap()
    {
        if(mForecastAdapter != null)
        {
            Cursor c = mForecastAdapter.getCursor();
            if( c != null){
                c.moveToPosition(0);
                String posLat = c.getString(COL_LOCATION_LAT);
                String posLong = c.getString(COL_LOCATION_LONG);

                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivity(intent);
                }
                else
                {
                    Log.d("Map", "Couldn't call location, no map app found.");
                }
            }
        }


    }
}
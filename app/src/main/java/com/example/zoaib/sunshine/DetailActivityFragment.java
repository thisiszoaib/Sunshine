package com.example.zoaib.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zoaib.sunshine.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 1;
    public static final String DATE_KEY = "date";
    public static final String LOCATION_KEY = "location";



    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private String mForecastStr;
    private String mLocation;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }else
        {
            Log.d(LOG_TAG,"Share Action Provider is null.");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);

        }
        getLoaderManager().initLoader(DETAIL_LOADER,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return rootView;
    }

    private Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
        };

        mLocation = Utility.getPreferredLocation(getActivity());
        String date = "";
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            date = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        Log.d(LOG_TAG,"Date passed: " + date);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation,date
        );

        return new CursorLoader(
                getActivity(),
                weatherUri,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()){
            String description = data.getString(
                    data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            String dateText = data.getString(
                    data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));

            double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
            double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));

            TextView dateView = (TextView)getView().findViewById(R.id.detail_date_textview);
            TextView forecastView = (TextView)getView().findViewById(R.id.detail_forecast_textview);
            TextView highView = (TextView)getView().findViewById(R.id.detail_high_textview);
            TextView lowView = (TextView)getView().findViewById(R.id.detail_low_textview);

            dateView.setText("");
            dateView.setText(Utility.formatDate(dateText));
            forecastView.setText("");
            forecastView.setText(description);
            highView.setText("");
            highView.setText(Utility.formatTemperature(high,getActivity())+"\u00B0");
            lowView.setText("");
            lowView.setText(Utility.formatTemperature(low,getActivity())+"\u00B0");


            mForecastStr = String.format("%s - %s - %s/%s",dateView.getText(),
                    forecastView.getText(),highView.getText(),lowView.getText());
        }
        else
        {
            TextView dateView = (TextView)getView().findViewById(R.id.detail_date_textview);
            TextView forecastView = (TextView)getView().findViewById(R.id.detail_forecast_textview);
            TextView highView = (TextView)getView().findViewById(R.id.detail_high_textview);
            TextView lowView = (TextView)getView().findViewById(R.id.detail_low_textview);

            dateView.setText("");
            forecastView.setText("");
            highView.setText("");
            lowView.setText("");

            mForecastStr = "";
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mLocation != null) {
            outState.putString(LOCATION_KEY, mLocation);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity())))
        {
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }
}

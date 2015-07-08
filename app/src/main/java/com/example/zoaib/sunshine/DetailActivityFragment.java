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
import android.widget.ImageView;
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

    private ImageView mIconView;
    private TextView mDateView, mFriendlyDateView,mDescriptionView,mHighTempView,
    mLowTempView,mHumidityView,mWindView,mPressureView;

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
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconView = (ImageView)rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView)rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView)rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView)rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);

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
        String date = getArguments().getString(DetailActivity.DATE_KEY);

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

            int weatherId = data.getInt(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));

            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            String description = data.getString(
                    data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            mDescriptionView.setText(description);

            String date = data.getString(
                    data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
            long dateInMilli = WeatherContract.getDateFromDb(date).getTime();
            String friendlyDateText = Utility.getDayName(getActivity(),
                    dateInMilli);
            String dateText = Utility.getFormattedMonthDay(getActivity(),
                    dateInMilli);

            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            double high = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
            double low = data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));

            mHighTempView.setText(getString(R.string.format_temperature,high));
            mLowTempView.setText(getString(R.string.format_temperature,low));

            double humidity = data.getDouble(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
            mHumidityView.setText(getString(R.string.format_humidity,humidity));

            float wind = data.getFloat(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_WIND_SPEED
            ));
            float degrees = data.getFloat(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_DEGREES
            ));
            mWindView.setText(Utility.getFormattedWind(getActivity(),wind,degrees));

            double pressure = data.getDouble(data.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_PRESSURE
            ));
            mPressureView.setText(getString(R.string.format_pressure,pressure));

            mForecastStr = String.format("%s - %s - %s/%s",mDateView.getText(),
                    mDescriptionView.getText(),mHighTempView.getText(),mLowTempView.getText());
        }
        else
        {
            mDateView.setText("");
            mDescriptionView.setText("");
            mHighTempView.setText("");
            mLowTempView.setText("");
            mHumidityView.setText("");
            mWindView.setText("");
            mPressureView.setText("");

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
        Bundle arguments = getArguments();
        if(arguments != null && arguments.containsKey(DetailActivity.DATE_KEY) &&
                mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity())))
        {
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }
}

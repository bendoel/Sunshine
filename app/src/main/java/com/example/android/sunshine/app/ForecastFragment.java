package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ForecastFragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> adapterForecast;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //callback from setHasOptionMenu(true), this function is called when you call function setHasOptionMenu(True)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_refresh:
                new FetchWeatherTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//          create fake data
        String[] forecastArray = {
                "Today - Storm - 88/63",
                "Tuesday - Sunny - 88/63",
                "Wednesday - Breeze - 88/63",
                "Thursday - Hot - 88/63",
                "Friday - Rain - 88/63",
                "Saturday - Rain Storm - 88/63",
                "Sunday - Windy - 88/63"
        };

        List<String> listForecast = new ArrayList<>(Arrays.asList(forecastArray));

//          initialize adapter
        adapterForecast = new ArrayAdapter<String>(
                // The current context (this fragment activity that is an activity)
                // so if we have the activity object, we have the context object
                getActivity(),
                //ID of list item layout (the list of adapter)
                R.layout.list_item_forecast,
                //ID of the textview item to populate (the adapter)
                R.id.list_item_tv_forecast,
                // forecast data
                listForecast);

        ListView listviewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        listviewForecast.setAdapter(adapterForecast);

        return rootView;
    }


    public static class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.


        String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        public FetchWeatherTask() {



        }

        @Override
//        protected Object doInBackground(Object[] params) {
          protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
//                    forecastJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
//                forecastJsonStr = null;
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}
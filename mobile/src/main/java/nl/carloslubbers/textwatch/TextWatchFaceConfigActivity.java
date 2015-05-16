package nl.carloslubbers.textwatch;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class TextWatchFaceConfigActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG_BACKGROUND_COLOUR_CHOOSER = "background_chooser";
    private static final String TAG_DATE_AND_TIME_COLOUR_CHOOSER = "date_time_chooser";
    private static final String TAG = "Text Watchface";

    private GoogleApiClient googleApiClient;

    private View backgroundColourImagePreview;
    private View dateAndTimeColourImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_watch_face_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        /*findViewById(R.id.configuration_background_colour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColourChooserDialog.newInstance(getString(R.string.pick_background_colour))
                        .show(getFragmentManager(), TAG_BACKGROUND_COLOUR_CHOOSER);
            }
        });

        findViewById(R.id.configuration_time_colour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColourChooserDialog.newInstance(getString(R.string.pick_date_time_colour))
                        .show(getFragmentManager(), TAG_DATE_AND_TIME_COLOUR_CHOOSER);
            }
        });

        backgroundColourImagePreview = findViewById(R.id.configuration_background_colour_preview);
        dateAndTimeColourImagePreview = findViewById(R.id.configuration_date_and_time_colour_preview);*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@Override
    public void onLanguageSelected(String lang) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    /*@Override
    public void onColourSelected(String colour, String tag) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(WatchfaceSyncCommons.PATH);

        if (TAG_BACKGROUND_COLOUR_CHOOSER.equals(tag)) {
            backgroundColourImagePreview.setBackgroundColor(Color.parseColor(colour));
            watchConfigurationPreferences.setBackgroundColour(Color.parseColor(colour));
            putDataMapReq.getDataMap().putString(WatchfaceSyncCommons.KEY_BACKGROUND_COLOUR, colour);
        } else {
            dateAndTimeColourImagePreview.setBackgroundColor(Color.parseColor(colour));
            watchConfigurationPreferences.setDateAndTimeColour(Color.parseColor(colour));
            putDataMapReq.getDataMap().putString(WatchfaceSyncCommons.KEY_DATE_TIME_COLOUR, colour);
        }

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }*/

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
}
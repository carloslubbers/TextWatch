package nl.carloslubbers.textwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.Timer;
import java.util.TimerTask;

public class WatchFace extends Activity {

    protected static final String TAG = "TextWatch";

    public GoogleApiClient mGoogleApiClient;
    public SharedPreferences settings;
    public SharedPreferences.Editor editor;
    public MatrixManager matrixManager;
    public String heightString = "";
    private MessageListener messageListener;

    /**
     * Starts the asynchronous scheduled task to update the text
     */
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        final WatchFace thisFace = this;
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            TimeTask performBackgroundTask = new TimeTask(thisFace, matrixManager);
                            performBackgroundTask.execute();
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 3000);
    }

    protected void onCreate(Bundle bundle) {
        Log.v("WatchFace", "onCreate();");
        super.onCreate(bundle);
        setContentView(R.layout.activity_watch_face);

        initConfig();

        callAsynchronousTask();

        googleApiConnect();
    }

    protected void onPause() {
        Log.v("WatchFace", "onPause();");
        super.onPause();
        setWatchTheme("pause");
    }

    protected void onResume() {
        Log.v("WatchFace", "onResume();");
        super.onResume();
        setContentView(R.layout.activity_watch_face);

        initConfig();

        callAsynchronousTask();

        googleApiConnect();
    }

    private void initConfig() {
        // Restore preferences
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();

        matrixManager = new MatrixManager(this);
        messageListener = new MessageListener(this);

        setHeight(Integer.parseInt(settings.getString("height", "3")));
        setWatchTheme(settings.getString("theme", "dark"));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Anonymous.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(typeface);
    }

    private void googleApiConnect() {
        //  Is needed for communication between the wearable and the device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(messageListener)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }


    /**
     * Sets the text height
     *
     * @param h Amount of lines to move the text up
     */
    protected void setHeight(int h) {
        Log.v("TextWatch", "Height: " + h);
        editor.putString("height", String.valueOf(h));
        editor.commit();
        heightString = "";
        matrixManager.getMatrix()[9][11] = "";
        for (int i = 0; i < h; i++) {
            heightString += "<br/>";
        }
    }

    /**
     * Set the watch theme (currently light or dark)
     *
     * @param t The theme identifier
     */
    protected void setWatchTheme(String t) {
        if (t.equals("dark")) {
            editor.putString("theme", "dark").commit();
            matrixManager.darkColor = "#282828";
            matrixManager.lightColor = "white";
            matrixManager.backgroundColor = Color.argb(255, 0, 0, 0);
        } else if (t.equals("light")) {
            editor.putString("theme", "light").commit();
            matrixManager.darkColor = "#e5e5e5";
            matrixManager.lightColor = "black";
            matrixManager.backgroundColor = Color.argb(255, 255, 255, 255);
        } else {
            matrixManager.darkColor = "#282828";
            matrixManager.lightColor = "white";
            matrixManager.backgroundColor = Color.argb(255, 0, 0, 0);
        }
    }




}

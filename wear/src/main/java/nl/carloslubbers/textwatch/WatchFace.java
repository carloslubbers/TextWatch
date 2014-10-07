package nl.carloslubbers.textwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.TimerTask;

public class WatchFace extends Activity {

    protected static final String TAG = "TextWatch";
    final Handler handler = new Handler();
    final WatchFace thisFace = this;
    public GoogleApiClient mGoogleApiClient;
    public SharedPreferences settings;
    public SharedPreferences.Editor editor;
    public MatrixManager matrixManager;
    public String heightString = "";
    WatchViewStub stub;
    TextView tv;
    private MessageListener messageListener;

    /**
     * Starts the asynchronous scheduled task to update the text
     */
    public void callAsynchronousTask() {
        TimerTask doAsynchronousTask;
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            TimeTask performBackgroundTask = new TimeTask(thisFace, matrixManager);
                            performBackgroundTask.execute();

                            handler.postDelayed(this, 1000);
                        } catch (Exception ignored) {
                        }
                        }
                });
            }
        };
        handler.postDelayed(doAsynchronousTask, 1000);
        // timer.schedule(doAsynchronousTask, 0, 1000);

    }

    protected void onCreate(Bundle bundle) {
        Log.v("WatchFace", "onCreate();");
        super.onCreate(bundle);
        setContentView(R.layout.activity_watch_face);

        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tv = (TextView) stub.findViewById(R.id.textView);
            }
        });

        stub.inflate();

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();
        matrixManager = new MatrixManager(this);
        messageListener = new MessageListener(this);

        initConfig();

        callAsynchronousTask();

        googleApiConnect();
    }

    protected void onPause() {
        Log.v("WatchFace", "onPause();");
        super.onPause();
        setWatchTheme("pause");

        initConfig();

        googleApiConnect();
    }

    protected void onResume() {
        Log.v("WatchFace", "onResume();");
        super.onResume();

        initConfig();

        googleApiConnect();
    }

    private void initConfig() {
        // Restore preferences

        matrixManager.setLanguage(settings.getString("lang", "en"));
        setHeight(Integer.parseInt(settings.getString("height", "3")));
        setWatchTheme(settings.getString("theme", "dark"));
        setPadding(settings.getInt("padding", 0));
        setFontSize(settings.getInt("fontsize", 0));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Anonymous.ttf");
        tv.setTypeface(typeface);
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
        tv.setPadding(tv.getPaddingLeft(), h * 10, tv.getPaddingRight(), tv.getPaddingBottom());
        /**heightString = "";
        matrixManager.getMatrix()[9][11] = "";
        for (int i = 0; i < h; i++) {
            heightString += "<br/>";
         }*/
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

    protected void setPadding(int p) {
        editor.putInt("padding", p).apply();
        tv.setPadding(p, tv.getPaddingTop(), tv.getPaddingRight(), tv.getPaddingBottom());
    }

    public void setFontSize(int i) {
        editor.putInt("fontsize", i).apply();
        tv.setTextSize((float) (13 + 0.1 * i));
    }
}

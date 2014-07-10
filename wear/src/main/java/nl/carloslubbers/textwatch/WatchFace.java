// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package nl.carloslubbers.textwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WatchFace extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    public static final String START_ACTIVITY_PATH = "/config/update";
    private static final String TAG = "TextWatch";
    private static final String MATRIX[][] = {
            {
                    "I", "T", "H", "I", "S", "U", "Q", "J", "S", "P",
                    "G", ""
            }, {
            "A", "C", "Q", "U", "A", "R", "T", "E", "R", "D",
            "C", "\n"
    }, {
            "T", "W", "E", "N", "T", "Y", "F", "I", "V", "E",
            "X", ""
    }, {
            "H", "A", "L", "F", "B", "T", "E", "N", "F", "T",
            "O", "\n"
    }, {
            "P", "A", "S", "T", "E", "R", "U", "N", "I", "N",
            "E", ""
    }, {
            "O", "N", "E", "S", "I", "X", "T", "H", "R", "E",
            "E", "\n"
    }, {
            "F", "O", "U", "R", "F", "I", "V", "E", "T", "W",
            "O", ""
    }, {
            "E", "I", "G", "H", "T", "E", "L", "E", "V", "E",
            "N", "\n"
    }, {
            "S", "E", "V", "E", "N", "T", "W", "E", "L", "V",
            "E", ""
    }, {
            "T", "E", "N", "S", "O", "'", "C", "L", "O", "C",
            "K", "\n\n\n\n"
    }
    };
    private static int STATUS[][] = {
            {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0
            }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }, {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0
    }
    };
    private GoogleApiClient mGoogleApiClient;
    private List<Node> nodes;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private String darkColor = "#282828";
    private String lightColor = "white";
    private int backgroundColor = Color.argb(255, 0, 0, 0);

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);

    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            TimeTask performBackgroundTask = new TimeTask();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 50000 ms
    }

    protected void onCreate(Bundle bundle) {
        Log.v("WatchFace", "onCreate();");
        super.onCreate(bundle);
        setContentView(R.layout.activity_watch_face);

        // Restore preferences
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();

        setHeight(Integer.parseInt(settings.getString("height", "3")));
        setWatchTheme(settings.getString("theme", "dark"));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Anonymous.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(typeface);
        callAsynchronousTask();

        //  Is needed for communication between the wearable and the device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
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

    protected void onPause() {
        Log.v("WatchFace", "onPause();");
        super.onPause();
        setWatchTheme("pause");
    }

    protected void onResume() {
        Log.v("WatchFace", "onResume();");
        super.onResume();
        // Restore preferences
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();

        setHeight(Integer.parseInt(settings.getString("height", "3")));
        setWatchTheme(settings.getString("theme", "dark"));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Anonymous.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(typeface);
        callAsynchronousTask();

        //  Is needed for communication between the wearable and the device.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
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

    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {

        /*
        This method apparently runs in a background thread.
         */

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cmd = messageEvent.getPath();
                if (cmd.startsWith("/config/height/")) {
                    String height = cmd.replace("/config/height/", "");
                    int h = Integer.parseInt(height);
                    setHeight(h);
                } else if (cmd.startsWith("/config/theme/")) {
                    String t = cmd.replace("/config/theme/", "");
                    setWatchTheme(t);
                }
            }
            // soon.
        });

        Log.v(TAG, "Message received on wear: " + messageEvent.getPath());

    }

    private void setHeight(int h) {
        Log.v("TextWatch", "Height: " + h);
        editor.putString("height", String.valueOf(h));
        editor.commit();
        MATRIX[9][11] = "";
        for (int i = 0; i < h; i++) {
            MATRIX[9][11] += "<br/>";
        }
    }

    private void setWatchTheme(String t) {
        if (t.equals("dark")) {
            editor.putString("theme", "dark").commit();
            darkColor = "#282828";
            lightColor = "white";
            backgroundColor = Color.argb(255, 0, 0, 0);
        } else if (t.equals("light")) {
            editor.putString("theme", "light").commit();
            darkColor = "#e5e5e5";
            lightColor = "black";
            backgroundColor = Color.argb(255, 255, 255, 255);
        } else {
            darkColor = "#282828";
            lightColor = "white";
            backgroundColor = Color.argb(255, 0, 0, 0);
        }
    }

    protected class TimeTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v("WatchFace", "doInBackground();");
            publishProgress(0);
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.v("WatchFace", "onPreExecute();");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... integers) {
            Log.v("WatchFace", "onProgressUpdate();");
            setTime((TextView) findViewById(R.id.textView));
        }

        public void setTime(TextView textview) {
            int h12;
            int m5;
            Log.v("WatchFace", "setTime();");
            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            int m = calendar.get(Calendar.MINUTE);
            h12 = h % 12;
            m5 = (int) Math.floor(m / 5);
            if (m5 >= 7) {
                h12 += 1;
                if (h12 > 11) h12 = 0;
            }
            for (int i1 = 0; i1 < MATRIX.length; i1++) {
                for (int l1 = 0; l1 < MATRIX[i1].length; l1++) {
                    STATUS[i1][l1] = 0;
                }

            }

            STATUS[0][0] = 1;
            STATUS[0][1] = 1;
            STATUS[0][3] = 1;
            STATUS[0][4] = 1;

            switch (h12) {
                case 0:
                    STATUS[8][5] = 1;
                    STATUS[8][6] = 1;
                    STATUS[8][7] = 1;
                    STATUS[8][8] = 1;
                    STATUS[8][9] = 1;
                    STATUS[8][10] = 1;
                    break;
                case 1:
                    STATUS[5][0] = 1;
                    STATUS[5][1] = 1;
                    STATUS[5][2] = 1;
                    break;
                case 2:
                    STATUS[6][8] = 1;
                    STATUS[6][9] = 1;
                    STATUS[6][10] = 1;
                    break;
                case 3:
                    STATUS[5][6] = 1;
                    STATUS[5][7] = 1;
                    STATUS[5][8] = 1;
                    STATUS[5][9] = 1;
                    STATUS[5][10] = 1;
                    break;
                case 4:
                    STATUS[6][0] = 1;
                    STATUS[6][1] = 1;
                    STATUS[6][2] = 1;
                    STATUS[6][3] = 1;
                    break;
                case 5:
                    STATUS[6][4] = 1;
                    STATUS[6][5] = 1;
                    STATUS[6][6] = 1;
                    STATUS[6][7] = 1;
                    break;
                case 6:
                    STATUS[5][3] = 1;
                    STATUS[5][4] = 1;
                    STATUS[5][5] = 1;
                    break;
                case 7:
                    STATUS[8][0] = 1;
                    STATUS[8][1] = 1;
                    STATUS[8][2] = 1;
                    STATUS[8][3] = 1;
                    STATUS[8][4] = 1;
                    break;
                case 8:
                    STATUS[7][0] = 1;
                    STATUS[7][1] = 1;
                    STATUS[7][2] = 1;
                    STATUS[7][3] = 1;
                    STATUS[7][4] = 1;
                    break;
                case 9:
                    STATUS[4][7] = 1;
                    STATUS[4][8] = 1;
                    STATUS[4][9] = 1;
                    STATUS[4][10] = 1;
                    break;
                case 10:
                    STATUS[9][0] = 1;
                    STATUS[9][1] = 1;
                    STATUS[9][2] = 1;
                    break;
                case 11:
                    STATUS[7][5] = 1;
                    STATUS[7][6] = 1;
                    STATUS[7][7] = 1;
                    STATUS[7][8] = 1;
                    STATUS[7][9] = 1;
                    STATUS[7][10] = 1;
                    break;

            }
            switch (m5) {
                case 0:
                    STATUS[9][4] = 1;
                    STATUS[9][5] = 1;
                    STATUS[9][6] = 1;
                    STATUS[9][7] = 1;
                    STATUS[9][8] = 1;
                    STATUS[9][9] = 1;
                    STATUS[9][10] = 1;
                    break;
                case 1:
                    STATUS[2][6] = 1;
                    STATUS[2][7] = 1;
                    STATUS[2][8] = 1;
                    STATUS[2][9] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 2:
                    STATUS[3][5] = 1;
                    STATUS[3][6] = 1;
                    STATUS[3][7] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 3:
                    STATUS[1][0] = 1;
                    STATUS[1][2] = 1;
                    STATUS[1][3] = 1;
                    STATUS[1][4] = 1;
                    STATUS[1][5] = 1;
                    STATUS[1][6] = 1;
                    STATUS[1][7] = 1;
                    STATUS[1][8] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 4:
                    STATUS[2][0] = 1;
                    STATUS[2][1] = 1;
                    STATUS[2][2] = 1;
                    STATUS[2][3] = 1;
                    STATUS[2][4] = 1;
                    STATUS[2][5] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 5:
                    STATUS[2][0] = 1;
                    STATUS[2][1] = 1;
                    STATUS[2][2] = 1;
                    STATUS[2][3] = 1;
                    STATUS[2][4] = 1;
                    STATUS[2][5] = 1;
                    STATUS[2][6] = 1;
                    STATUS[2][7] = 1;
                    STATUS[2][8] = 1;
                    STATUS[2][9] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 6:
                    STATUS[3][0] = 1;
                    STATUS[3][1] = 1;
                    STATUS[3][2] = 1;
                    STATUS[3][3] = 1;
                    STATUS[4][0] = 1;
                    STATUS[4][1] = 1;
                    STATUS[4][2] = 1;
                    STATUS[4][3] = 1;
                    break;
                case 7:
                    STATUS[2][0] = 1;
                    STATUS[2][1] = 1;
                    STATUS[2][2] = 1;
                    STATUS[2][3] = 1;
                    STATUS[2][4] = 1;
                    STATUS[2][5] = 1;
                    STATUS[2][6] = 1;
                    STATUS[2][7] = 1;
                    STATUS[2][8] = 1;
                    STATUS[2][9] = 1;
                    STATUS[3][9] = 1;
                    STATUS[3][10] = 1;
                    break;
                case 8:
                    STATUS[2][0] = 1;
                    STATUS[2][1] = 1;
                    STATUS[2][2] = 1;
                    STATUS[2][3] = 1;
                    STATUS[2][4] = 1;
                    STATUS[2][5] = 1;
                    STATUS[3][9] = 1;
                    STATUS[3][10] = 1;
                    break;
                case 9:
                    STATUS[1][0] = 1;
                    STATUS[1][2] = 1;
                    STATUS[1][3] = 1;
                    STATUS[1][4] = 1;
                    STATUS[1][5] = 1;
                    STATUS[1][6] = 1;
                    STATUS[1][7] = 1;
                    STATUS[1][8] = 1;
                    STATUS[3][9] = 1;
                    STATUS[3][10] = 1;
                    break;
                case 10:
                    STATUS[3][5] = 1;
                    STATUS[3][6] = 1;
                    STATUS[3][7] = 1;
                    STATUS[3][9] = 1;
                    STATUS[3][10] = 1;
                    break;
                case 11:
                    STATUS[2][6] = 1;
                    STATUS[2][7] = 1;
                    STATUS[2][8] = 1;
                    STATUS[2][9] = 1;
                    STATUS[3][9] = 1;
                    STATUS[3][10] = 1;
                    break;
            }
            String s = "<font color='" + darkColor + "'>";
            for (int j1 = 0; j1 < MATRIX.length; j1++) {
                int k1 = 0;
                while (k1 < MATRIX[j1].length) {
                    if (STATUS[j1][k1] == 0) {
                        s = (new StringBuilder()).append(s).append(MATRIX[j1][k1]).toString();
                    } else {
                        s = (new StringBuilder()).append(s).append("</font><font color=").append(lightColor).append(">").append(MATRIX[j1][k1]).append("</font><font color='" + darkColor + "'>").toString();
                    }
                    k1++;
                }
            }
            ((ImageView) findViewById(R.id.background)).setBackgroundColor(backgroundColor);
            textview.setText(Html.fromHtml((new StringBuilder()).append(s).append("</font>").toString()));
        }
    }


}

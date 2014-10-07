package nl.carloslubbers.textwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "TextWatch";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.activity_my);

        // Load preferences
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        //  Google API client for communication between devices
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        // Restore theme
        if (settings.getString("theme", "dark").equals("dark")) {
            setWatchTheme("dark");
        } else {
            setWatchTheme("light");
        }

    }

    private void updateFontSize(int i) {
        sendMessage("/config/fontsize/" + i);
    }

    /**
     * Send a message to change the padding on the text
     *
     * @param i left padding in dp
     */
    private void updatePadding(int i) {
        sendMessage("/config/padding/" + i);
    }

    /**
     * Send a message to change the theme to dark or light
     *
     * @param t The theme identifier
     */
    private void setWatchTheme(String t) {
        sendMessage("/config/theme/" + t);
    }

    /**
     * Send a message to set the height of the text
     *
     * @param h Amount of lines to go up
     */
    private void updateHeight(int h) {
        sendMessage("/config/height/" + h);
    }

    /**
     * Send a message to the wear device
     * /config/height/$ - Set height
     * /config/theme/$ - theme
     * ($ = identifier)
     *
     * @param msg The message content
     */
    private void sendMessage(String msg) {
        new AsyncTask<String, Void, List<Node>>() {
            String msg;

            @Override
            protected List<Node> doInBackground(String... params) {
                msg = params[0];
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for (Node node : nodeList) {
                    Log.v(TAG, "telling " + node.getId() + " " + msg);

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            msg,
                            null
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        }
                    });
                }
            }
        }.execute(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets connected nodes in a List
     *
     * @return A list of connected nodes
     */
    private List<Node> getNodes() {
        List<Node> nodes = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult rawNodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : rawNodes.getNodes()) {
            nodes.add(node);
        }
        return nodes;
    }

    private void setLanguage(String lang) {
        sendMessage("/config/lang/" + lang);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.i(TAG, s);
        if (s.equals("height")) {
            updateHeight(Integer.parseInt(sharedPreferences.getString("height", "0")));
        } else if (s.equals("lang")) {
            setLanguage(sharedPreferences.getString("lang", "en"));
        } else if (s.equals("size")) {
            updateFontSize(Integer.parseInt(sharedPreferences.getString("size", "0")));
        } else if (s.equals("theme")) {
            setWatchTheme(sharedPreferences.getString("theme", "dark"));
        } else if (s.equals("padding")) {
            updatePadding(Integer.parseInt(sharedPreferences.getString("padding", "0")));
        }

    }
}

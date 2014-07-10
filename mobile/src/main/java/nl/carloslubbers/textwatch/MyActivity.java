package nl.carloslubbers.textwatch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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


public class MyActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "TextWatch";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view
        setContentView(R.layout.activity_my);

        // Load preferences
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = settings.edit();

        // Define elements
        final SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        final TextView statusText = (TextView) findViewById(R.id.statusText);
        final Button darkRadio = (Button) findViewById(R.id.radioButton);
        final Button lightRadio = (Button) findViewById(R.id.radioButton2);
        darkRadio.setOnClickListener(this);
        lightRadio.setOnClickListener(this);

        // Restore seeker
        sb.setProgress(Integer.parseInt(settings.getString("height", "5")));
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editor.putString("height", String.valueOf(i)).commit();
                updateHeight(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioButton:
                setWatchTheme("dark");
                break;
            case R.id.radioButton2:
                setWatchTheme("light");
                break;
        }

    }
}

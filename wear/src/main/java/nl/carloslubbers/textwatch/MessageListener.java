package nl.carloslubbers.textwatch;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MessageListener implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    private WatchFace watchFace;

    public MessageListener(WatchFace wf) {
        Log.v(WatchFace.TAG, "listener added");
        watchFace = wf;
    }

    /**
     * Method fired when a message is received from the paired device. Runs on a background thread.
     *
     * @param messageEvent The message object
     */
    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        // Run this on the UI thread
        watchFace.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String cmd = messageEvent.getPath();
                if (cmd.startsWith("/config/height/")) {
                    String height = cmd.replace("/config/height/", "");
                    int h = Integer.parseInt(height);
                    watchFace.setHeight(h);
                } else if (cmd.startsWith("/config/theme/")) {
                    String t = cmd.replace("/config/theme/", "");
                    watchFace.setWatchTheme(t);
                } else if (cmd.startsWith("/config/lang/")) {
                    String t = cmd.replace("/config/lang/", "");
                    watchFace.matrixManager.setLanguage(t);
                } else if (cmd.startsWith("/config/padding/")) {
                    String t = cmd.replace("/config/padding/", "");
                    watchFace.setPadding(Integer.parseInt(t));
                } else if (cmd.startsWith("/config/fontsize/")) {
                    String t = cmd.replace("/config/fontsize/", "");
                    watchFace.setFontSize(Integer.parseInt(t));
                }
            }
        });

        Log.v(WatchFace.TAG, "Message received on wear: " + messageEvent.getPath());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(watchFace.mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Wearable.MessageApi.removeListener(watchFace.mGoogleApiClient, this);
    }
}

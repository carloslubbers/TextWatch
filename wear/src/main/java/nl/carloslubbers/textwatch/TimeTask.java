package nl.carloslubbers.textwatch;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * The Asynchronous task that updates the text
 */
public class TimeTask extends AsyncTask<Void, Integer, Void> {

    WatchFace watchFace;
    MatrixManager matrixManager;

    public TimeTask(WatchFace wf, MatrixManager mm) {
        watchFace = wf;
        matrixManager = mm;
    }

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
        matrixManager.updateText((TextView) watchFace.findViewById(R.id.textView));
    }
}
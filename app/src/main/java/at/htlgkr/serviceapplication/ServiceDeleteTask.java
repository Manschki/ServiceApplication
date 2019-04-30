package at.htlgkr.serviceapplication;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class ServiceDeleteTask extends AsyncTask<Void, Void, Void> {
    private final MainActivity activity;
    int serviceId;

    public ServiceDeleteTask(MainActivity activity, int serviceId) {
        this.activity = activity;
        this.serviceId = serviceId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Backend b = new Backend();
        try {
            b.deleteService(serviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        activity.serviceDeleted(serviceId); // Korrigieren Sie diese Zeile
    }
}

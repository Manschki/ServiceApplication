package at.htlgkr.serviceapplication;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class CreateEmployeeTask extends AsyncTask<Void, Void, Void> {
    private final MainActivity activity;
    private final String name;
    private final String lat;
    private final String lng;
    private Employee emp = null;

    public CreateEmployeeTask(MainActivity activity, String name, String longitude, String latitude) {
        this.activity = activity;
        this.name = name;
        this.lat = latitude;
        this.lng = longitude;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Backend b = new Backend();

        try {
            emp = b.addEmployee(name, lng, lat);
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
        activity.employeeAdded(emp);
    }
}

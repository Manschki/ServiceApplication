package at.htlgkr.serviceapplication;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class ServiceCreateTask extends AsyncTask<Void, Void, Void> {
    private final CreateEmployeeActivity activity;
    private final String name;
    Date date;
    String lat;
    String lng;
    Employee employee;


    public ServiceCreateTask(CreateEmployeeActivity activity, String name, Date date, String latitude, String longitude, Employee employee) {
        this.activity = activity;
        this.name = name;
        this.date = date;
        this.lat = latitude;
        this.lng = longitude;
        this.employee = employee;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Backend b = new Backend();
        try {
            b.addService(name, date, employee, lat, lng);
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
        activity.serviceCreated();
    }
}

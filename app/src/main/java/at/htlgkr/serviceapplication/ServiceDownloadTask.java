package at.htlgkr.serviceapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class ServiceDownloadTask extends AsyncTask<Void, Void, Void> {
    private final IServiceDownloadActivity activity;
    private List<ServiceResource> serviceResources = new LinkedList<>();
    private List<Employee> employees = new LinkedList<>();
    String kind;
    public ServiceDownloadTask(IServiceDownloadActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Backend b = new Backend();

        try {
            serviceResources = b.getAllServices();
            employees = b.getAllEmployees();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        Model m = null;
        try {
            m = new Model(employees, serviceResources);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        activity.servicesLoaded(m);
    }
}

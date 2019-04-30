package at.htlgkr.serviceapplication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Backend {
    private static final String BACKEND_SERVER_URL = "http://10.0.2.2:8080/serviceBackend";
    private static final String SERVICES_ENDPOINT = "/services";
    private static final String EMPLOYEES_ENDPOINT = "/employees";

    public List<Employee> getAllEmployees() throws IOException {
        String sJson = "";

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(BACKEND_SERVER_URL + EMPLOYEES_ENDPOINT).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                sJson = readResponseStream(reader);
            }
        } catch (IOException e) {
            Log.d("TAG", e.getLocalizedMessage());
        }
        Gson gson = new Gson();


        TypeToken<List<Employee>> token = new TypeToken<List<Employee>>(){};

        List<Employee> services = gson.fromJson(sJson, token.getType());

        return services;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        Log.d("TAG", "entered readResponseStreaulat");
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public List<ServiceResource> getAllServices() throws IOException {
        String sJson = "";

        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(BACKEND_SERVER_URL + SERVICES_ENDPOINT).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                sJson = readResponseStream(reader);
            }
        } catch (IOException e) {
            Log.d("TAG", e.getLocalizedMessage());
        }
        Gson gson = new Gson();


        TypeToken<List<ServiceResource>> token = new TypeToken<List<ServiceResource>>(){};

        List<ServiceResource> services = gson.fromJson(sJson, token.getType());

        return services;
    }



    public Employee addEmployee(String name, String longitude, String latitude) throws JSONException, IOException {
        String sJson = "";

        JSONObject postParams = new JSONObject();
        postParams.put("name", name);
        postParams.put("longitude", longitude);
        postParams.put("latitude", latitude);
        String body = postParams.toString();
        byte[] data = body.getBytes();

        HttpURLConnection connection =
                (HttpURLConnection) new URL(BACKEND_SERVER_URL + EMPLOYEES_ENDPOINT).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            sJson = readResponseStream(reader);
        }
        Gson gson = new Gson();
        Employee e = gson.fromJson(sJson, Employee.class);

        return e;

    }

    public ServiceResource addService(String name, Date date, Employee employee, String longitude, String latitude) throws JSONException, IOException {
        String sJson = "";
        SimpleDateFormat sdf = new SimpleDateFormat(Model.DATE_FORMAT);

        int empId = employee.getId();

        JSONObject postParams = new JSONObject();
        postParams.put("name", name);
        postParams.put("date", sdf.format(date));
        postParams.put("employeeId", empId);
        postParams.put("longitude", longitude);
        postParams.put("latitude", latitude);
        String body = postParams.toString();
        byte[] data = body.getBytes();

        HttpURLConnection connection =
                (HttpURLConnection) new URL(BACKEND_SERVER_URL + SERVICES_ENDPOINT).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            sJson = readResponseStream(reader);
        }
        Gson gson = new Gson();
        ServiceResource sr = gson.fromJson(sJson, ServiceResource.class);

        return sr;
    }

    public boolean deleteService(int serviceId) throws JSONException, IOException {
        String sJson = "";

        JSONObject postParams = new JSONObject();
        postParams.put("serviceId", serviceId);
        String body = postParams.toString();
        byte[] data = body.getBytes();

        HttpURLConnection connection =
                (HttpURLConnection) new URL(BACKEND_SERVER_URL + SERVICES_ENDPOINT + "/"+ serviceId).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return true;
        }

        return false;
    }

}

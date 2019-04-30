package at.htlgkr.serviceapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Muss in der AndroidManifest.xml gesetzt werden:
 * android:usesCleartextTraffic="true"
 */

public class MainActivity extends AppCompatActivity implements IServiceDownloadActivity {
    private static final int RQ_ACCESS_FINE_LOCATION = 1;
    ListView listView;
    ArrayAdapter adapter;
    private boolean gpsGranted = false;
    private LocationManager locationManager;
    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.service_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        checkPermissionGPS();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("TAG", "onOptionsItemSelected: " + id);
        switch (id) {
            case R.id.retrieve_data:
                ServiceDownloadTask sd = new ServiceDownloadTask(this);
                sd.execute();
                break;
            case R.id.new_employee:
                final View vDialog = getLayoutInflater().inflate(R.layout.new_employee_dialog, null);
                new AlertDialog.Builder(this)
                        .setMessage("Neuen Mitarbeiter anlegen")
                        .setCancelable(false)
                        .setView(vDialog)
                        .setPositiveButton("ok", (dialog, which) -> handleDialog(vDialog))
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
            case R.id.new_service:
                Intent intent = new Intent(this, CreateEmployeeActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void handleDialog(View vDialog) {
        EditText ed = vDialog.findViewById(R.id.employee_name);
        Location location = new Location("");

        if (gpsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            location = locationManager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER);

        }
        if(location == null){
            location = new Location("");
        }
        
        CreateEmployeeTask ce = new CreateEmployeeTask(this, ed.getText().toString(), String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
        ce.execute();
    }

    private void checkPermissionGPS() {
        Log.d("TAG", "checkPermissionGPS");
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ permission },
                    RQ_ACCESS_FINE_LOCATION );
        } else {
            gpsIsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != RQ_ACCESS_FINE_LOCATION) return;
        if (grantResults.length > 0 &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
            .setMessage("Permission ACCESS_FINE_LOCATION denied!")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            gpsIsGranted();
        }
    }

    private void gpsIsGranted() {
        gpsGranted = true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.service_list) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_service) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = info.position;

            Service s = (Service) adapter.getItem(pos);

            ServiceDeleteTask sd = new ServiceDeleteTask(this, s.getId());
            sd.execute();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void servicesLoaded(Model model) {
        this.model = model;
        adapter.clear();
        adapter.addAll(this.model.getServices());
    }

    public void employeeAdded(Employee e) {
        /*List<Employee> emps = model.getEmployees();
        emps.add(e);
        model.setEmployees(emps);*/
        ServiceDownloadTask sd = new ServiceDownloadTask(this);
        sd.execute();

    }

    public void serviceDeleted(int serviceId) {
        model.deleteService(serviceId);
        adapter.clear();
        adapter.addAll(model.getServices());
    }
}

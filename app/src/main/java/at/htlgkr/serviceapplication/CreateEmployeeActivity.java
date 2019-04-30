package at.htlgkr.serviceapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CreateEmployeeActivity extends AppCompatActivity implements IServiceDownloadActivity {


    private Spinner spinner;
    private Button btn;
    private ArrayAdapter adapter;
    private List<Employee> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_service);

        spinner = findViewById(R.id.service_employee);
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        btn = findViewById(R.id.btn_create_new_service);
        btn.setOnClickListener(v -> speichern());

        ServiceDownloadTask sd = new ServiceDownloadTask(this);
        sd.execute();
    }

    private void speichern() {
        //String name, Date date, String latitude, String longitude, Employee employee)
        EditText eName = findViewById(R.id.service_name);
        EditText eDate = findViewById(R.id.service_date);
        EditText eLat = findViewById(R.id.service_latitude);
        EditText eLong = findViewById(R.id.service_longitude);
        Employee emp = null;

        for (Employee e :
                employees) {
            if (e.getName().equals(spinner.getSelectedItem().toString())) {
                emp = e;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Model.DATE_FORMAT);
        Date d = null;
        try {
            d = sdf.parse(eDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ServiceCreateTask sc = new ServiceCreateTask(this, eName.getText().toString(), d, eLong.getText().toString(), eLat.getText().toString(), emp);
        sc.execute();
    }

    @Override
    public void servicesLoaded(Model model) {
        List<String> names = new LinkedList<>();
        employees = model.getEmployees();
        for (Employee s:
             employees) {
            names.add(s.getName());
        }
        adapter.addAll(names);
    }

    public void serviceCreated() {
    }
}

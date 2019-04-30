package at.htlgkr.serviceapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Model {
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    private List<Employee> employees;
    private List<Service> services ;

    public Model(List<Employee> employees, List<ServiceResource> serviceResources) throws ParseException {
        this.employees = employees;
        services = new LinkedList<>();
        //SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        for (ServiceResource s:
             serviceResources) {
            //int id, String name, Employee employee, Date date, String longitude, String latitude
            //nt id, String name, String longitude, String latitude
            SimpleDateFormat sdf = new SimpleDateFormat(Model.DATE_FORMAT);
            Date d = sdf.parse(s.getDate());
            Employee employee = null;
            for (Employee e:
                 this.employees) {
                if(e.getId() == s.getEmployeeId()){
                    employee = e;
                }
            }
            //int id, String name, Employee employee, Date date, String longitude, String latitude
            services.add(new Service(s.getId(), s.getName(), employee, d, s.getLongitude(), s.getLatitude()));
        }
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Service> getServices() {
        return services;
    }

    public void deleteService(int serviceId) {
        // Implementieren Sie diese Methode
        Service toDelete = null;
        for (Service s:
             services) {
            if(s.getId() == serviceId){
                toDelete = s;
            }
        }
        services.remove(toDelete);
    }
}

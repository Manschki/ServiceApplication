package at.htlgkr.serviceapplication;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/*
 * androidTestImplementation 'com.android.support.test:rules:1.0.2' must  be added to dependencies
 * */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackendTest {
    @Rule
    public ActivityTestRule<MainActivity> myActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test_backend_getAllEmployees_returnsSomething() throws IOException {
        Backend underTest = new Backend();

        List<Employee> employees = underTest.getAllEmployees();

        assertTrue(employees != null);
        assertTrue(employees.size() > 0);
    }

    @Test
    public void test_backend_getAllEmployees_returnsCorrectSize() throws IOException {
        Backend underTest = new Backend();

        List<Employee> employees = underTest.getAllEmployees();

        assertTrue(employees.size() == 2);
    }

    @Test
    public void test_backend_getAllEmployees_returnsCorrectData() throws IOException {
        Backend underTest = new Backend();

        List<Employee> employeesTest = underTest.getAllEmployees();
        List<Employee> employeesExpected = this.getEmployeeAssertData();

        ListTester<Employee> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(employeesTest, employeesExpected));
    }

    @Test
    public void test_backend_getAllServices_returnsSomething() throws IOException {
        Backend underTest = new Backend();

        List<ServiceResource> serviceResources = underTest.getAllServices();

        assertTrue(serviceResources != null);
        assertTrue(serviceResources.size() > 0);
    }

    @Test
    public void test_backend_getAllServices_returnsCorrectSize() throws IOException {
        Backend underTest = new Backend();

        List<ServiceResource> serviceResources = underTest.getAllServices();

        assertTrue(serviceResources.size() == 3);
    }

    @Test
    public void test_backend_getAllServices_returnsCorrectData() throws IOException {
        Backend underTest = new Backend();

        List<ServiceResource> serviceResourcesTest = underTest.getAllServices();
        List<ServiceResource> serviceResourcesExpected = getServiceAssertData();

        ListTester<ServiceResource> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(serviceResourcesTest, serviceResourcesExpected));
    }

    @Test
    public void test_backend_addEmployee_correctId() throws IOException, JSONException {
        Backend underTest = new Backend();

        Employee addedEmployee = underTest.addEmployee("Test1", "Test2", "Test3");

        assertEquals(2, addedEmployee.getId());

        underTest.getAllEmployees(); // reset;
    }

    @Test
    public void test_backend_addEmployee_correctData() throws IOException, JSONException {
        Backend underTest = new Backend();

        Employee addedEmployee = underTest.addEmployee("Test1", "Test2", "Test3");

        assertEquals(2, addedEmployee.getId());
        assertEquals("Test1", addedEmployee.getName());
        assertEquals("Test2", addedEmployee.getLongitude());
        assertEquals("Test3", addedEmployee.getLatitude());

        underTest.getAllEmployees(); // reset;
    }

    @Test
    public void test_backend_addEmployee_correctAllEmployeesDataSize() throws IOException, JSONException {
        Backend underTest = new Backend();

        underTest.addEmployee("Test1", "Test2", "Test3");
        List<Employee> allEmployeesTest = underTest.getAllEmployees();

        assertEquals(3, allEmployeesTest.size());

        underTest.getAllEmployees(); // reset;
    }

    @Test
    public void test_backend_addEmployee_correctAllEmployeesData() throws IOException, JSONException {
        Backend underTest = new Backend();

        underTest.addEmployee("Test1", "Test2", "Test3");
        List<Employee> allEmployeesTest = underTest.getAllEmployees();
        List<Employee> allEmployeesExpected = getEmployeeAssertData();

        Employee test = new Employee();
        test.setId(2);
        test.setName("Test1");
        test.setLongitude("Test2");
        test.setLatitude("Test3");

        allEmployeesExpected.add(test);

        ListTester<Employee> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(allEmployeesTest, allEmployeesExpected));

        underTest.getAllEmployees(); // reset;
    }

    @Test
    public void test_backend_addService_correctId() throws ParseException, IOException, JSONException {
        Backend undertTest = new Backend();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        ServiceResource addedService = undertTest.addService("Test1", sdf.parse("12.12.1212 12:12"), getEmployeeAssertData().get(0), "TestLong", "TestLat");

        assertEquals(4, addedService.getId());

        undertTest.getAllServices(); //reset;
    }

    @Test
    public void test_backend_addService_correctData() throws ParseException, IOException, JSONException {
        Backend undertTest = new Backend();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        ServiceResource addedService = undertTest.addService("Test1", sdf.parse("12.12.1212 12:12"), getEmployeeAssertData().get(1), "TestLong", "TestLat");

        assertEquals(4, addedService.getId());
        assertEquals("Test1", addedService.getName());
        assertEquals("12.12.1212 12:12", addedService.getDate());
        assertEquals(1, addedService.getEmployeeId());
        assertEquals("TestLong", addedService.getLongitude());
        assertEquals("TestLat", addedService.getLatitude());

        undertTest.getAllServices(); //reset;
    }

    @Test
    public void test_backend_addService_correctAllServicesSize() throws ParseException, IOException, JSONException {
        Backend undertTest = new Backend();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        undertTest.addService("Test1", sdf.parse("12.12.1212 12:12"), getEmployeeAssertData().get(1), "TestLong", "TestLat");
        List<ServiceResource> allServices = undertTest.getAllServices();

        assertEquals(4, allServices.size());

        undertTest.getAllServices(); //reset;
    }

    @Test
    public void test_backend_addService_correctAllServicesData() throws ParseException, IOException, JSONException {
        Backend undertTest = new Backend();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        ServiceResource addedService = undertTest.addService("Test1", sdf.parse("12.12.1212 12:12"), getEmployeeAssertData().get(1), "TestLong", "TestLat");
        List<ServiceResource> allServicesTest = undertTest.getAllServices();
        List<ServiceResource> allServicesExpected = getServiceAssertData();
        allServicesExpected.add(addedService);

        ListTester<ServiceResource> listTester = new ListTester<>();

        listTester.listsEqual(allServicesTest, allServicesExpected);

        undertTest.getAllServices(); //reset;
    }

    @Test
    public void test_backend_deleteService_deletesSomething() throws IOException, JSONException {
        Backend undertTest = new Backend();

        undertTest.deleteService(0);

        List<ServiceResource> allServicesTest = undertTest.getAllServices();

        assertEquals(2, allServicesTest.size());
    }

    @Test
    public void test_backend_deleteService_deletesCorrectService() throws IOException, JSONException {
        Backend undertTest = new Backend();

        undertTest.deleteService(0);

        List<ServiceResource> allServicesTest = undertTest.getAllServices();
        List<ServiceResource> allServicesExpected = getServiceAssertData(false);

        ListTester<ServiceResource> listTester = new ListTester<>();

        listTester.listsEqual(allServicesTest, allServicesExpected);

        undertTest.getAllServices(); //reset;
    }

    private class ListTester<T> {
        private boolean listsEqual(List<T> listA, List<T> listB) {
            for (T cA : listA) {
                if(!isElementInList(cA, listB))
                    return false;
            }

            for(T cB : listB) {
                if(!isElementInList(cB, listA))
                    return false;
            }

            return true;
        }

        private boolean isElementInList(T needle, List<T> list) {
            for (T cElement : list) {
                if (cElement instanceof Employee) {
                    if (employeeEquals((Employee) cElement, (Employee) needle))
                        return true;
                }
                if (cElement instanceof ServiceResource) {
                    if(serviceEquals((ServiceResource) cElement, (ServiceResource) needle)) {
                       return true;
                    }
                }
            }

            return false;
        }

        private boolean serviceEquals(ServiceResource a, ServiceResource b) {
            return a.getId() == b.getId() &&
                    a.getName().equals(b.getName()) &&
                    a.getEmployeeId() == b.getEmployeeId() &&
                    a.getDate().equals(b.getDate()) &&
                    a.getLatitude().equals(b.getLatitude()) &&
                    a.getLongitude().equals(b.getLongitude());
        }

        private boolean employeeEquals(Employee a, Employee b) {
            return a.getId() == b.getId() &&
                    a.getName().equals(b.getName()) &&
                    a.getLatitude().equals(b.getLatitude()) &&
                    a.getLongitude().equals(b.getLongitude());
        }
    }

    private List<Employee> getEmployeeAssertData() {
        Employee e1 = new Employee();
        e1.setId(0);
        e1.setName("Hubert Sauerampfer");
        e1.setLatitude("48.405560");
        e1.setLongitude("13.528200");

        Employee e2 = new Employee();
        e2.setId(1);
        e2.setName("Franz Mayer");
        e2.setLatitude("47.972800");
        e2.setLongitude("13.033750");

        List<Employee> result = new ArrayList<>();

        result.add(e1);
        result.add(e2);

        return result;
    }

    private List<ServiceResource> getServiceAssertData() {
        return getServiceAssertData(true);
    }

    private List<ServiceResource> getServiceAssertData(boolean withFirst) {
        ServiceResource s1 = new ServiceResource();
        s1.setId(0);
        s1.setName("Putzen");
        s1.setEmployeeId(0);
        s1.setDate("09.03.2019 12:33");
        s1.setLatitude("48.215802");
        s1.setLongitude("13.627451");

        ServiceResource s2 = new ServiceResource();
        s2.setId(1);
        s2.setName("Rasenmaehen");
        s2.setDate("09.04.2019 13:37");
        s2.setEmployeeId(0);
        s2.setLatitude("48.248112");
        s2.setLongitude("13.723441");

        ServiceResource s3 = new ServiceResource();
        s3.setId(2);
        s3.setName("Heckenschneiden");
        s3.setDate("22.04.2019 18:00");
        s3.setEmployeeId(1);
        s3.setLatitude("48.310845");
        s3.setLongitude("14.401865");

        List<ServiceResource> result = new ArrayList<>();

        if(withFirst) {
            result.add(s1);
        }
        result.add(s2);
        result.add(s3);
        return result;
    }
}

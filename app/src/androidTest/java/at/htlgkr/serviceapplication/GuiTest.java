package at.htlgkr.serviceapplication;

import android.se.omapi.SEService;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GuiTest {
    @Rule
    public ActivityTestRule<MainActivity> myActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

	@Test
    public void test_gui_mainMenu_available() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).check(matches(isDisplayed()));
        onView(withText("Neuer Mitarbeiter")).check(matches(isDisplayed()));
        onView(withText("Neuer Dienst")).check(matches(isDisplayed()));
    }

    @Test
    public void test_gui_loadData_loadsSomething() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());

        onView(withId(R.id.service_list)).perform(WaitAction.waitFor(withSomethingInList(), 10000));
    }

    @Test
    public void test_gui_loadData_correctListSize() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());

        onView(withId(R.id.service_list)).perform(WaitAction.waitFor(withListSize(3), 10000));
    }

    @Test
    public void test_gui_loadData_correctData() throws ParseException {
	    Backend b = new Backend();
        try {
            b.getAllServices();
        } catch (IOException e) {
            e.printStackTrace();
        }

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());

        onView(withId(R.id.service_list)).perform(WaitAction.waitFor(withListSize(3), 10000));

        ListView servicesView = myActivityRule.getActivity().findViewById(R.id.service_list);

        ArrayList<Service> actual = new ArrayList<>();

        for (int i = 0; i < servicesView.getAdapter().getCount(); i++) {
            assertTrue(servicesView.getAdapter().getItem(i) instanceof Service);

            Service cService = (Service) servicesView.getAdapter().getItem(i);

            actual.add(cService);
        }

        ListTester<Service> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(actual, getServiceAssertData(true)));
    }

    @Test
    public void test_gui_createEmployee_showsDialog() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Mitarbeiter")).perform(click());
        onView(withText("Neuen Mitarbeiter anlegen")).check(matches(isDisplayed()));
    }

    @Test
    public void test_gui_createEmployee_canCreateEmployee() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Mitarbeiter")).perform(click());
        onView(withText("Neuen Mitarbeiter anlegen")).check(matches(isDisplayed()));

        onView(withId(R.id.employee_name)).perform(typeText("Max Must"));

        onView(withId(android.R.id.button2)).perform(click());

        Backend backend = new Backend();
        backend.getAllServices(); // reset
    }

    @Test
    public void test_gui_createEmployee_employeeIsCreated() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Mitarbeiter")).perform(click());
        onView(withText("Neuen Mitarbeiter anlegen")).check(matches(isDisplayed()));

        onView(withId(R.id.employee_name)).perform(typeText("Max Must"));

        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitFor(1000));

        Backend backend = new Backend();
        List<Employee> employees = backend.getAllEmployees();

        boolean found = false;

        for(Employee cEmployee : employees) {
            if(cEmployee.getName().equals("Max Must")) {
                found = true;
            }
        }

        assertTrue(found);
    }

    // Last known location muss sein: latitude: 13, longitude: 37 damit dieser test funktioniert
    @Test
    public void test_gui_createEmployee_employeeIsCreatedCorrectData() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Mitarbeiter")).perform(click());
        onView(withText("Neuen Mitarbeiter anlegen")).check(matches(isDisplayed()));

        onView(withId(R.id.employee_name)).perform(typeText("Max Must"));

        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitFor(1000));

        Backend backend = new Backend();
        List<Employee> employees = backend.getAllEmployees();

        boolean found = false;

        for(Employee cEmployee : employees) {
            if(cEmployee.getName().equals("Max Must")) {
                if(cEmployee.getLatitude().equals("13.0") && cEmployee.getLongitude().equals("37.0")) {
                    found = true;
                }
            }
        }

        assertTrue(found);
    }

    @Test
    public void test_gui_createService_checkIfActivityOpens() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));
    }

    @Test
    public void test_gui_createService_checkIfSpinnerContainsSomething() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_employee)).perform(WaitAction.waitFor(withSomethingInSpinner(), 10000));
    }

    @Test
    public void test_gui_createService_checkSpinnerCorrectSize() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_employee)).perform(WaitAction.waitFor(withSpinnerSize(2), 10000));
    }

    @Test
    public void test_gui_createService_canCreateService() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_name)).perform(typeText("Dienst 1"));
        onView(withId(R.id.service_date)).perform(typeText("12.12.2012 12:00"));
        onView(withId(R.id.service_latitude)).perform(typeText("1"));
        onView(withId(R.id.service_longitude)).perform(typeText("2"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btn_create_new_service)).perform(click());

        Backend backend = new Backend();
        backend.getAllServices(); // reset
    }

    @Test
    public void test_gui_createService_canCreateServiceCorrectData() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_name)).perform(typeText("Dienst 1"));
        onView(withId(R.id.service_date)).perform(typeText("12.12.2012 12:00"));
        onView(withId(R.id.service_longitude)).perform(typeText("2.0"));
        onView(withId(R.id.service_latitude)).perform(typeText("1.0"));


        Espresso.closeSoftKeyboard();

        onView(withId(R.id.service_employee)).perform(click());
        onView(withText("Franz Mayer")).perform(click());

        onView(withId(R.id.btn_create_new_service)).perform(click());

        Backend backend = new Backend();
        List<ServiceResource> allServices = backend.getAllServices(); // reset

        boolean found = false;

        for(ServiceResource cService: allServices) {
            if(cService.getName().equals("Dienst 1") &&
                    cService.getDate().equals("12.12.2012 12:00") &&
                    cService.getLatitude().equals("1.0") &&
                    cService.getLongitude().equals("2.0") &&
                    cService.getEmployeeId() == 1) {
                found = true;
            }
        }

        assertTrue(found);
    }

    @Test
    public void test_gui_createService_employeeGetsLoaded() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Mitarbeiter")).perform(click());
        onView(withText("Neuen Mitarbeiter anlegen")).check(matches(isDisplayed()));

        onView(withId(R.id.employee_name)).perform(typeText("Max Must"));

        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitFor(1000));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_employee)).perform(WaitAction.waitFor(withSpinnerSize(3), 10000));
    }

    @Test
    public void test_gui_createService_checkIfServiceCreationWorkflowWorks() throws IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Neuer Dienst")).perform(click());
        onView(withText("NEUEN DIENST ANLEGEN")).check(matches(isDisplayed()));

        onView(withId(R.id.service_name)).perform(typeText("Dienst 1"));
        onView(withId(R.id.service_date)).perform(typeText("12.12.2012 12:00"));
        onView(withId(R.id.service_latitude)).perform(typeText("1"));
        onView(withId(R.id.service_longitude)).perform(typeText("2"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btn_create_new_service)).perform(click());

        Espresso.pressBack();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());
    }

    @Test
    public void test_gui_deleteService_deletesSomething() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());

        onView(withText("Rasenmaehen")).perform(longClick());

        onView(withText("Dienst loeschen")).perform(click());

        onView(withId(R.id.service_list)).perform(WaitAction.waitFor(withListSize(2), 10000));
    }

    @Test
    public void test_gui_deleteService_correctData() throws ParseException, IOException {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Daten abrufen")).perform(click());

        onView(withText("Putzen")).perform(longClick());

        onView(withText("Dienst loeschen")).perform(click());

        onView(withId(R.id.service_list)).perform(WaitAction.waitFor(withListSize(2), 10000));

        ListView servicesView = myActivityRule.getActivity().findViewById(R.id.service_list);

        ArrayList<Service> actual = new ArrayList<>();

        for (int i = 0; i < servicesView.getAdapter().getCount(); i++) {
            assertTrue(servicesView.getAdapter().getItem(i) instanceof Service);

            Service cService = (Service) servicesView.getAdapter().getItem(i);

            actual.add(cService);
        }

        ListTester<Service> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(actual, getServiceAssertData(false)));

        Backend backend = new Backend();
        backend.getAllServices(); // reset
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    private List<Service> getServiceAssertData(boolean withFirst) throws ParseException {
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Service s1 = new Service();
        s1.setId(0);
        s1.setName("Putzen");
        s1.setEmployee(e1);
        s1.setDate(sdf.parse("09.03.2019 12:33"));
        s1.setLatitude("48.215802");
        s1.setLongitude("13.627451");

        Service s2 = new Service();
        s2.setId(1);
        s2.setName("Rasenmaehen");
        s2.setDate(sdf.parse("09.04.2019 13:37"));
        s2.setEmployee(e1);
        s2.setLatitude("48.248112");
        s2.setLongitude("13.723441");

        Service s3 = new Service();
        s3.setId(2);
        s3.setName("Heckenschneiden");
        s3.setDate(sdf.parse("22.04.2019 18:00"));
        s3.setEmployee(e2);
        s3.setLatitude("48.310845");
        s3.setLongitude("14.401865");

        List<Service> result = new ArrayList<>();

        if (withFirst) {
            result.add(s1);
        }
        result.add(s2);
        result.add(s3);
        return result;
    }

    private class ListTester<T> {
        private boolean listsEqual(List<T> listA, List<T> listB) {
            for (T cA : listA) {
                if (!isElementInList(cA, listB))
                    return false;
            }

            for (T cB : listB) {
                if (!isElementInList(cB, listA))
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
                    if (serviceResourceEquals((ServiceResource) cElement, (ServiceResource) needle)) {
                        return true;
                    }
                }
                if (cElement instanceof Service) {
                    if (serviceEquals((Service) cElement, (Service) needle)) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean serviceEquals(Service a, Service b) {
            Employee e1 = a.getEmployee();
            Employee e2 = b.getEmployee();

            if (!employeeEquals(e1, e2)) {
                return false;
            }

            return a.getId() == b.getId() &&
                    a.getName().equals(b.getName()) &&
                    a.getDate().equals(b.getDate()) &&
                    a.getLatitude().equals(b.getLatitude()) &&
                    a.getLongitude().equals(b.getLongitude());
        }

        private boolean serviceResourceEquals(ServiceResource a, ServiceResource b) {
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

    public Matcher<View> withSomethingInList() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return ((ListView) item).getCount() > 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ListView should contain items");
            }
        };
    }

    public Matcher<View> withSomethingInSpinner() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return ((Spinner) item).getCount() > 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Spinner should contain items");
            }
        };
    }

    public Matcher<View> withListSize(final int listSize) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return ((ListView) item).getCount() == listSize;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ListView should have " + listSize + " items");
            }
        };
    }

    public Matcher<View> withSpinnerSize(final int spinnerSize) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return ((Spinner) item).getCount() == spinnerSize;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Spinner should have " + spinnerSize + " items");
            }
        };
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
}

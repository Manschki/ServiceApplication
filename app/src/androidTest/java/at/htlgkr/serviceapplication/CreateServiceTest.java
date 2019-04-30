package at.htlgkr.serviceapplication;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CreateServiceTest {
    @Rule
    public ActivityTestRule<CreateEmployeeActivity> createEmployeeActivityRule = new ActivityTestRule<>(CreateEmployeeActivity.class);

    @Test
    public void test_gui_createService_checkSpinnerCorrectData() {
        onView(withId(R.id.service_employee)).perform(WaitAction.waitFor(withSpinnerSize(2), 10000));

        Spinner employeeSpinner = createEmployeeActivityRule.getActivity().findViewById(R.id.service_employee);

        ArrayList<Employee> actual = new ArrayList<>();

        for (int i = 0; i < employeeSpinner.getAdapter().getCount(); i++) {
            assertTrue(employeeSpinner.getAdapter().getItem(i) instanceof Employee);

            Employee cEmployee = (Employee) employeeSpinner.getAdapter().getItem(i);

            actual.add(cEmployee);
        }

        ListTester<Employee> listTester = new ListTester<>();

        assertTrue(listTester.listsEqual(actual, getEmployeeAssertData()));
    }

    @Test
    public void test_gui_createService_testCorrectSuggestion() {
        onView(withId(R.id.service_employee)).perform(WaitAction.waitFor(withSpinnerSize(2), 10000));

        onView(withId(R.id.service_latitude)).perform(typeText("1"));
        onView(withId(R.id.service_longitude)).perform(typeText("2"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btn_employee_suggestion)).perform(click());

        TextView distView = createEmployeeActivityRule.getActivity().findViewById(R.id.tv_employee_suggestion);

        assertTrue(distView.getText().toString().contains("Franz Mayer"));
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

package at.htlgkr.serviceapplication;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.anything;

public class WaitAction implements ViewAction {

    /** The amount of time to allow the main thread to loop between checks. */

    private final Matcher<View> condition;
    private final long timeoutMs;

    public WaitAction(Matcher<View> condition, long timeout) {
        this.condition = condition;
        this.timeoutMs = timeout;
    }

    @Override
    public Matcher<View> getConstraints() {
        return (Matcher) anything();
    }

    @Override
    public String getDescription() {
        return "wait";
    }

    @Override
    public void perform(UiController controller, View view) {
        controller.loopMainThreadUntilIdle();
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + timeoutMs;

        while (System.currentTimeMillis() < endTime) {
            if (condition.matches(view)) {
                return;
            }

            controller.loopMainThreadForAtLeast(100);
        }

        // Timeout.
        throw new RuntimeException("timeout");
    }

    public static ViewAction waitFor(Matcher<View> condition, long timeout) {
        return new WaitAction(condition, timeout);
    }
}

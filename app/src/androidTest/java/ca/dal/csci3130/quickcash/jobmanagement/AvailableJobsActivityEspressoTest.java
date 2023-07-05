package ca.dal.csci3130.quickcash.jobmanagement;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.ToastMatcher;
import ca.dal.csci3130.quickcash.home.EmployeeHomeActivity;


public class AvailableJobsActivityEspressoTest {
    @Rule
    public ActivityTestRule<AvailableJobsActivity> activityTestRule =
            new ActivityTestRule<>(AvailableJobsActivity.class);

    @Before
    public void setup() { Intents.init(); }

    /*** test Return to Home link ***/
    @Test
    public void testMoveToAvailableJobsActivity() {
        onView(withId(R.id.btn_returnHome_employee)).perform(click());
        intended(hasComponent(EmployeeHomeActivity.class.getName()));
    }

    @Test
    public void testNoJobsFound() {
        onView(withId(R.id.editTextSearchJobType)).perform(clearText(), typeText("asdf"), closeSoftKeyboard());
        onView(withId(R.id.editTextSearchPayRate)).perform(clearText(), typeText("900"), closeSoftKeyboard());
        onView(withId(R.id.editTextSearchDuration)).perform(clearText(), typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.buttonApplyFilter)).perform(click());
        onView(withText(R.string.no_job_found)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }


    @After
    public void tearDown() { Intents.release(); }

}

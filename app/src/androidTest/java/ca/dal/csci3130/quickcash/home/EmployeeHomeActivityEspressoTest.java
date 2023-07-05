package ca.dal.csci3130.quickcash.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

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
import ca.dal.csci3130.quickcash.jobmanagement.AvailableJobsActivity;

public class EmployeeHomeActivityEspressoTest {

    @Rule
    public ActivityTestRule<EmployeeHomeActivity> activityTestRule =
            new ActivityTestRule<>(EmployeeHomeActivity.class);

    @Before
    public void setup() { Intents.init(); }

    /*** test Active Jobs link ***/
    @Test
    public void testMoveToAvailableJobsActivity() {
        onView(withId(R.id.btn_seeAvailableJobs)).perform(click());
        intended(hasComponent(AvailableJobsActivity.class.getName()));
    }


    @After
    public void tearDown() { Intents.release(); }

}



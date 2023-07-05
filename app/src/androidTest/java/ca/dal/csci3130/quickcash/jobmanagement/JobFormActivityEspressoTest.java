package ca.dal.csci3130.quickcash.jobmanagement;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.common.Constants;
import ca.dal.csci3130.quickcash.usermanagement.User;

public class JobFormActivityEspressoTest {

    @Rule
    public ActivityTestRule<JobFormActivity> activityTestRule =
            new ActivityTestRule<>(JobFormActivity.class);

    @Before
    public void setup() { Intents.init(); }

    /** isNumeric() **/
    @Test
    public void durationInvalid(){
        fillFields("Title", "JobType", "somejob", "10!", "10");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.duration_pay_rate_numeric)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void payRateInvalid(){
        fillFields("Title", "JobType", "somejob", "10", "10!");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.duration_pay_rate_numeric)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /** isEmpty() **/
    @Test
    public void titleEmpty() {
        fillFields("", "JobType", "somejob", "1", "10");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.toast_missing_component)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void jobTypeEmpty() {
        fillFields("SomeTitle", "", "somejob", "1", "10");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.toast_missing_component)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void descriptionEmpty() {
        fillFields("SomeTitle", "JobType", "", "1", "10");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.toast_missing_component)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void durationEmpty() {
        fillFields("SomeTitle", "JobType", "somejob", "", "10");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.toast_missing_component)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void payRateEmpty() {
        fillFields("SomeTitle", "JobType", "somejob", "10", "");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.toast_missing_component)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /** Job posted **/
    @Test
    public void validJob(){
        fillFields("TestTitle", "TestType", "Test Description", "5", "24");
        onView(withId(R.id.post)).perform(click());
        onView(withText(R.string.job_posted_successfully)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        removeLastChild();
    }

    private void fillFields(String jobTitle, String jobType, String jobDescription, String duration, String payRate) {
        onView(withId(R.id.jobTitle)).perform(typeText(jobTitle));
        onView(withId(R.id.jobType)).perform(typeText(jobType));
        onView(withId(R.id.description)).perform(typeText(jobDescription));
        onView(withId(R.id.Duration)).perform(typeText(duration));
        onView(withId(R.id.payRate)).perform(typeText(payRate), closeSoftKeyboard());
    }

    private void removeLastChild(){
        FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.FIREBASE_URL);
        DatabaseReference databaseReference = db.getReference(Job.class.getSimpleName());

        databaseReference.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapShot : snapshot.getChildren()){
                    String lastChildKey = postSnapShot.getKey();
                    if(lastChildKey != null)
                        databaseReference.child(lastChildKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB Error", error.toString());
            }
        });
    }

    @After
    public void tearDown() { Intents.release(); }
}

package ca.dal.csci3130.quickcash.usermanagement;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.TextView;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.ToastMatcher;
import ca.dal.csci3130.quickcash.common.Constants;
import ca.dal.csci3130.quickcash.home.EmployeeHomeActivity;
import ca.dal.csci3130.quickcash.home.EmployerHomeActivity;

public class LoginActivityEspressoTest {

    static FirebaseDatabase db;
    static DatabaseReference databaseReference;
    String userObjectKey;

    final UserInterface testEmployee = new User("Test", "Employee", "testEmployee@dal.ca", "1234567890",
            "WhvwHpsorbhh@1", true);
    final UserInterface testEmployer = new User("Test", "Employer", "testEmployer@dal.ca", "1234567890",
            "WhvwHpsorbhu@1", false);

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @BeforeClass
    public static void init(){
        db = FirebaseDatabase.getInstance(Constants.FIREBASE_URL);
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    @Before
    public void setup() throws InterruptedException {
        Thread.sleep(1000);
        Intents.init();

        userObjectKey = databaseReference.push().getKey();
        if(userObjectKey == null)
            throw new NullPointerException("User Object Key is null!");
    }

    /*--------------Employee---------*/
    @Test
    public void validEmployeeCredentials() throws InterruptedException {
        // add employee to the db
        databaseReference.child(userObjectKey).setValue(testEmployee);

        fillFields("testEmployee@dal.ca", "TestEmployee@1");
        onView(withId(R.id.loginButtonCheckInfo)).perform(click());

        // wait for 1 second to reach the employee home
        Thread.sleep(1000);

        intended(hasComponent(EmployeeHomeActivity.class.getName()));
        onView(withId(R.id.welcomeEmployee)).check(matches(withText("Welcome Employee, Test Employee")));

        // logout before closing to close the session
        onView(withId(R.id.btn_logout_employee)).perform(click());
    }

    @Test
    public void invalidEmployeeCredentials(){
        // add employee to the db
        // NOTE/to-do: passing a user object with the same email makes it fail when all the tests of this class
        // are run together. If ran in isolation, still passes. Temporary workaround: send object with new
        // email
        testEmployee.setEmail("testEmployee2@dal.ca");
        databaseReference.child(userObjectKey).setValue(testEmployee);

        fillFields("invalidEmployee@dal.ca", "invalidEmployee@1");
        onView(withId(R.id.loginButtonCheckInfo)).perform(click());

        onView(withText(R.string.toast_invalid_email_and_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }


    /*-------------Employer-----------*/

    @Test
    public void validEmployerCredentials() throws InterruptedException {
        // add employee to the db
        databaseReference.child(userObjectKey).setValue(testEmployer);

        fillFields("testEmployer@dal.ca", "TestEmployer@1");
        onView(withId(R.id.loginButtonCheckInfo)).perform(click());

        // wait for 1 second to reach the employee home
        Thread.sleep(1000);

        intended(hasComponent(EmployerHomeActivity.class.getName()));
        onView(withId(R.id.welcomeEmployer)).check(matches(withText("Welcome Employer, Test Employer")));

        // logout before closing to close the session
        onView(withId(R.id.btn_logout_employer)).perform(click());
    }

    @Test
    public void invalidEmployerCredentials(){
        // add employee to the db
        // NOTE/to-do: passing a user object with the same email makes it fail when all the tests of this class
        // are run together. If ran in isolation, still passes. Temporary workaround: send object with new
        // email
        testEmployer.setEmail("testEmployer2@dal.ca");
        databaseReference.child(userObjectKey).setValue(testEmployer);

        fillFields("invalidEmployer@dal.ca", "invalidEmployer@1");
        onView(withId(R.id.loginButtonCheckInfo)).perform(click());

        onView(withText(R.string.toast_invalid_email_and_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }


    private void fillFields(String email, String password) {
        onView(withId(R.id.etEmailId)).perform(typeText(email));
        onView(withId(R.id.etPassword)).perform(typeText(password), closeSoftKeyboard());
    }

    @After
    public void tearDown(){
        // remove the added user from the db to avoid clutter
        databaseReference.child(userObjectKey).removeValue();
        Intents.release();
    }
}


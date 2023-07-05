package ca.dal.csci3130.quickcash.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.jobmanagement.AvailableJobsActivity;
import ca.dal.csci3130.quickcash.usermanagement.LoginActivity;
import ca.dal.csci3130.quickcash.usermanagement.PreferenceActivity;
import ca.dal.csci3130.quickcash.usermanagement.SessionManager;

public class EmployeeHomeActivity extends AppCompatActivity {

    private Button availableJobs;
    private Button preferencesButton;

    @Override
    //Prevent user from using back button once logged in
    public void onBackPressed () {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);
        availableJobs = findViewById(R.id.btn_seeAvailableJobs);
        preferencesButton = findViewById(R.id.buttonToPref);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        //Gets the name from the session
        String fullName = sessionManager.getKeyName();

        // printing welcome message
        TextView welcomeMessage = (TextView) findViewById(R.id.welcomeEmployee);
        welcomeMessage.setText(String.format("Welcome Employee, %s", fullName));

        availableJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAvailableJobsActivity();
            }
        });

        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToPreferenceActivity();
            }
        });
    }


    /**
     * Deletes session and opens login screen
     *
     * @param view
     */

    public void logout(View view) {
        SessionManager session = new SessionManager(EmployeeHomeActivity.this);
        session.logoutUser();

        moveToLoginActivity();
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(EmployeeHomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void moveToAvailableJobsActivity() {
        Intent intent = new Intent(EmployeeHomeActivity.this, AvailableJobsActivity.class);
        startActivity(intent);
    }

    private void moveToPreferenceActivity() {
        Intent intent = new Intent(EmployeeHomeActivity.this, PreferenceActivity.class);
        startActivity(intent);
    }
}
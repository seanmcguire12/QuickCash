package ca.dal.csci3130.quickcash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.dal.csci3130.quickcash.home.EmployeeHomeActivity;
import ca.dal.csci3130.quickcash.home.EmployerHomeActivity;
import ca.dal.csci3130.quickcash.usermanagement.LoginActivity;
import ca.dal.csci3130.quickcash.usermanagement.SessionManager;
import ca.dal.csci3130.quickcash.usermanagement.SignupActivity;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private Button signUp;

    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add logic to handle the two buttons added in the UI

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Checks if session already exists and moves user to home page session exists
     */

    private void checkSession() {
        SessionManager session = new SessionManager(MainActivity.this);

        boolean isLoggedIn = session.isLoggedIn();

        if (isLoggedIn) {
            boolean isEmployee = session.getIsEmployee();
            if (isEmployee) {
                moveToEmployeePage();
            } else {
                moveToEmployerPage();
            }
        }
    }

    private void moveToEmployeePage() {

        Intent intentEmployee = new Intent(MainActivity.this, EmployeeHomeActivity.class);
        intentEmployee.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentEmployee);

    }

    private void moveToEmployerPage() {

        Intent intentEmployer = new Intent(MainActivity.this, EmployerHomeActivity.class);
        intentEmployer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentEmployer);

    }
}
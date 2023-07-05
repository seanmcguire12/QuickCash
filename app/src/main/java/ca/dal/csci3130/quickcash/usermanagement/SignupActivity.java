package ca.dal.csci3130.quickcash.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import ca.dal.csci3130.quickcash.R;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Existing user Redirect hyperlink
        TextView loginRedirect = (TextView) findViewById(R.id.existingUserRedirect);
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // logic for signup
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User newUser = getUserData();
                if (newUser != null) {
                    checkAndPush(newUser); // push to DB if data is valid
                    Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * method to add user data to Data Base
     *
     * @param user
     */
    protected void addUser(UserInterface user) {
        UserDAO userDAO = new UserDAO();
        userDAO.addUser(user);
    }

    /**
     * method to create Toast message upon error
     * @param messageId
     */

    protected void createToast(int messageId){
        Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_LONG).show();
    }

    /**
     * Gets all the user Data from the UI. Validates it.
     * If the data is valid, returns a User Object created from that data
     * else it will return null
     *
     * @return UserObject or null
     */
    private User getUserData() {

        // get all data
        String firstName = ((EditText) findViewById(R.id.etFirstName)).getText().toString();
        String lastName = ((EditText) findViewById(R.id.etLastName)).getText().toString();
        String email = ((EditText) findViewById(R.id.etEmailIdSignUp)).getText().toString();

        String phone = ((EditText) findViewById(R.id.etPhoneNumber)).getText().toString();

        String password = ((EditText) findViewById(R.id.etPasswordSignUp)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.etConfirmPasswordSignUp)).getText().toString();

        RadioGroup userTypeRadioGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);
        boolean isEmployee = ((RadioButton) findViewById(userTypeRadioGroup.getCheckedRadioButtonId()))
                .getText().toString().equals("Employee");

        // validate all data
        if (!isEmpty(firstName, lastName, email, phone, password, confirmPassword)) {
            if (isValidEmail(email)) {
                if (isPasswordValid(password)) {
                    if (passwordMatcher(confirmPassword, password)) {
                        if (isPhoneValid(phone)) {
                            password = encryptUserPassword(password);
                            return new User(firstName, lastName, email, phone, password, isEmployee);
                        }
                    }
                }

            }

        }

        return null;

    }


    /**
     * Check if any field is empty
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param phone
     * @param password
     * @param confirmPassword
     * @return true or false
     */
    protected boolean isEmpty(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        boolean anyFieldsEmpty = firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty();
        if (anyFieldsEmpty) {
            createToast(R.string.toast_missing_component);
            return true;
        }
        return false;
    }

    /**
     * method to validate if the email entered is valid
     *
     * @param email
     * @return true or false
     */
    protected boolean isValidEmail(String email) {
        boolean isEmailValid = PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
        if (!isEmailValid) {
            createToast(R.string.toast_invalid_email);
            return false;
        }
        return true;
    }


    /**
     * method to validate if the password
     * matches the requirements from length to pattern
     *
     * @param password
     * @return true or false
     */
    protected boolean isPasswordValid(String password) {
        final String passwordPattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        boolean passwordPatternMatches = Pattern.compile(passwordPattern).matcher(password).matches();
        if (!passwordPatternMatches) {
            createToast(R.string.toast_invalid_password);
            return false;
        }
        return true;
    }

    /**
     * method to validate if the password matches the confirm password
     *
     * @param password
     * @return true or false
     */
    protected boolean passwordMatcher(String confirmPassword, String password) {
        boolean passwordMatches = password.equals(confirmPassword);
        if (!passwordMatches) {
            createToast(R.string.toast_password_mismatch);
            return false;
        }
        return true;
    }


    /**
     * method to validate if the number entered is 10 digits long
     *
     * @param phone
     * @return true or false
     */
    protected boolean isPhoneValid(String phone) {
        final String phonePattern = "^[0-9]{10}$";
        boolean isPhoneNumberValid = Pattern.compile(phonePattern).matcher("" + phone).matches();
        if (!isPhoneNumberValid) {
            createToast(R.string.toast_invalid_phone);
            return false;
        } else
            return true;
    }


    /**
     * method to validate if the user is already in the data base
     * if not in the data base it adds a new user to the data base
     *
     * @param newUser
     */
    private void checkAndPush(User newUser) {
        UserDAO databaseReference = new UserDAO();
        DatabaseReference dataBase = databaseReference.getDatabaseReference();

        dataBase.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean newAccount = true;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    String email = newUser.getEmail();
                    if (user != null && user.getEmail().equals(email)) {
                        Toast.makeText(getApplicationContext(), "email already exists please login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        newAccount = false;
                        intent.putExtra("Email", email);
                        startActivity(intent);
                    }
                }
                if (newAccount)
                    addUser(newUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                final String errorRead = error.getMessage();
            }
        });


    }

    /**
     * Encrypts user password and returns and the encrypted password.
     *
     * @param password
     * @return encryptedPassword
     */
    protected String encryptUserPassword(String password) {
        // ET5: encrypt user password here
        String result = "";
        int key = 3;
        for(int x = 0; x< password.length(); x++){
            char letter = password.charAt(x);
            if(Character.isLowerCase(letter)){
                char new_letter = (char)(letter+key);

                if(new_letter > 'z'){
                    result+= (char)(letter -(26-key));

                }else{
                    result += new_letter;}

            }
            else if(Character.isUpperCase(letter)){
                char new_letter = (char)(letter+key);

                if(new_letter > 'Z'){
                    result+= (char)(letter - (26-key));

                }else{
                    result += new_letter;}

            }
            else{
                result +=letter;
            }

        }
        return result;
    }

}
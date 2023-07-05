package ca.dal.csci3130.quickcash.usermanagement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;

public class LoginActivityJUnitTest {


    LoginActivity loginActivityMock;
    SignupActivity signupActivityMock;

    final String[] invalidCredentials = {"invalidCreds@dal.ca", "Invalidcreds@1"};
    final String[] validEmployeeCredentials = {"employee1@dal.ca", "Employee@1"};
    final String[] validEmployerCredentials = {"employer1@dal.ca", "Employer@1"};

    final UserInterface registeredEmployee = new User("Employee", "1", "employee1@dal.ca", "1234567890",
            "Employee@1", true);
    final UserInterface registeredEmployer = new User("Employer", "1", "employer1@dal.ca", "1234567890",
            "Employer@1", false);

    @Before
    public void setup () {
        loginActivityMock = Mockito.mock(LoginActivity.class, Mockito.CALLS_REAL_METHODS);
        signupActivityMock = Mockito.mock(SignupActivity.class, Mockito.CALLS_REAL_METHODS);
    }

    // No users present
    @Test
    public void nullUser(){
        assertFalse(loginActivityMock.checkCredentials(validEmployeeCredentials, null));
    }

    // Invalid Employee Credentials
    @Test
    public void invalidEmployeeCredentials(){
        assertFalse(loginActivityMock.checkCredentials(invalidCredentials, registeredEmployee));
    }

    // Valid Employee Credentials
    @Test
    public void validEmployeeCredentials(){
        registeredEmployee.setPassword(signupActivityMock.encryptUserPassword(registeredEmployee.getPassword()));
        assertTrue(loginActivityMock.checkCredentials(validEmployeeCredentials, registeredEmployee));
    }

    // Invalid Employer Credentials
    @Test
    public void invalidEmployerCredentials(){
        assertFalse(loginActivityMock.checkCredentials(invalidCredentials, registeredEmployer));
    }

    // Valid Employer Credentials
    @Test
    public void validEmployerCredentials(){
        registeredEmployer.setPassword(signupActivityMock.encryptUserPassword(registeredEmployer.getPassword()));
        assertTrue(loginActivityMock.checkCredentials(validEmployerCredentials, registeredEmployer));
    }
}

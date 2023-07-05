package ca.dal.csci3130.quickcash.usermanagement;

public class User implements UserInterface {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private boolean isEmployee; // true or false.

    public User(String firstName, String lastName, String email, String phone, String password,
                boolean isEmployee) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isEmployee = isEmployee;
    }

    public User() {
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean getIsEmployee() {
        return isEmployee;
    }

    @Override
    public void setIsEmployee(boolean isEmployee) {
        this.isEmployee = isEmployee;
    }

}

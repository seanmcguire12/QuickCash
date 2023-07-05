package ca.dal.csci3130.quickcash.usermanagement;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager implements SessionManagerInterface {

    private static SharedPreferences pref;
    SharedPreferences.Editor editor;
    String SHARE_PREF_NAME = "session";

    public SessionManager(Context context) {

        pref = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

    }


    @Override
    public void createLoginSession(String email, String password, String name, boolean isEmployee) {

        editor.putString("email", email).commit();
        editor.putString("password", password).commit();
        editor.putString("name", name).commit();
        editor.putBoolean("isEmployee", isEmployee).commit();

    }

    @Override
    public void checkLogin() {

    }

    @Override
    public void logoutUser() {
        editor.clear().commit();
    }

    @Override
    public boolean isLoggedIn() {
        if(getKeyEmail().equals("") || getKeyName().equals("")){
            return false;
        }

        return true;
    }

    @Override
    public String getKeyName() {
        String name = pref.getString("name","");
        return name;
    }

    @Override
    public String getKeyEmail() {
        String email = pref.getString("email","");
        return email;
    }

    @Override
    public boolean getIsEmployee() {
        boolean isEmployee = pref.getBoolean("isEmployee", false);
        return isEmployee;
    }
}

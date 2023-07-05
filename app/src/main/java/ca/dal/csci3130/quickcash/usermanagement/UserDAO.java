package ca.dal.csci3130.quickcash.usermanagement;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.dal.csci3130.quickcash.common.AbstractDAO;
import ca.dal.csci3130.quickcash.common.Constants;

public class UserDAO extends AbstractDAO {
    private final DatabaseReference databaseReference;

    public UserDAO() {
        // FIREBASE_URL needs to be updated
        FirebaseDatabase db = FirebaseDatabase.
                getInstance(Constants.FIREBASE_URL);
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    @Override
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    @Override
    public Task<Void> addUser(UserInterface user) {
        return databaseReference.push().setValue(user);
    }

}

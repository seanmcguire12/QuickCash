package ca.dal.csci3130.quickcash.usermanagement;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.dal.csci3130.quickcash.common.AbstractDAO;
import ca.dal.csci3130.quickcash.common.Constants;
import ca.dal.csci3130.quickcash.jobmanagement.JobInterface;

public class PreferenceDAO extends AbstractDAO {
    private final DatabaseReference databaseReference;

    public PreferenceDAO(){
        // FIREBASE_URL needs to be updated
        FirebaseDatabase db = FirebaseDatabase.
                getInstance(Constants.FIREBASE_URL);
        databaseReference = db.getReference(Preferences.class.getSimpleName());
    }

    @Override
    public DatabaseReference getDatabaseReference() { return databaseReference;}

    @Override
    public Task<Void> addPreference(PreferenceInterface preference) {
        return databaseReference.push().setValue(preference);
    }
}

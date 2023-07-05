package ca.dal.csci3130.quickcash.jobmanagement;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.dal.csci3130.quickcash.common.AbstractDAO;
import ca.dal.csci3130.quickcash.common.Constants;

public class JobDAO extends AbstractDAO {
    private final DatabaseReference databaseReference;

    public JobDAO() {
        // FIREBASE_URL needs to be updated
        FirebaseDatabase db = FirebaseDatabase.getInstance(Constants.FIREBASE_URL);
        this.databaseReference = db.getReference(Job.class.getSimpleName());
    }

    @Override
    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    @Override
    public Task<Void> addJob(JobInterface job) {
        return databaseReference.push().setValue(job);
    }
}

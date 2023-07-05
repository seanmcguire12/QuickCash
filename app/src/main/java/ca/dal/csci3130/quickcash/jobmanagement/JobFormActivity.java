package ca.dal.csci3130.quickcash.jobmanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.home.EmployerHomeActivity;

public class JobFormActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private static final Integer REQUEST_CODE = 123;
    private LatLng jobLocation;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_form);

        // Obtain the SupportMapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.jobLocation);

        // grabs the location services api
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // checks if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it is already granted, then just get the current location
            getCurrentLocation();
        } else {
            // if not then ask for it
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);
        }
    }

    /**
     * This method is called when the map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // if location was not retrievable for some reason, set to default - Dalhousie University
        LatLng location = jobLocation == null ? new LatLng(44.636585, -63.5938442) : jobLocation;

        // mark the location and zoom into that
        MarkerOptions markerOptions = new MarkerOptions().position(location).title("Your Current Location - Job Location");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        mMap.addMarker(markerOptions).showInfoWindow();

        // After the map is set up, continue with normal flow

        Button postButton = (Button) findViewById(R.id.post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job newJob = getJobData();
                if (newJob != null) {
                    checkAndPushJob(newJob);
                    Intent intent = new Intent(JobFormActivity.this, EmployerHomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * This method is called when the user accepts or denies the asked permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if request_code has not changed, the process of asking the permission was successful
        if (requestCode == REQUEST_CODE) {
            // if it was granted then get the current location
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                // if not move forward and start the map with a default location
                Toast.makeText(this, "Permission denied by user !!!", Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(this);
            }
        }
    }

    /**
     * Gets the current location of the user.
     * FOR EMULATOR:
     * Emulators simulate GPS by giving latitudes and longitudes. You can adjust them from the
     * Elipsis(3 dots along side the emulator) -> Location.
     * Checkout the first solution here: https://stackoverflow.com/questions/2279647/how-to-emulate-gps-location-in-the-android-emulator
     */
    protected void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //This error is a android studio bug, the permissions are added
        fusedLocationClient
                .getCurrentLocation(100, cancellationTokenSource.getToken()) // 100 is PRIORITY_HIGH_ACCURACY
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // if location is not null, set it to job location
                        if (location != null) {
                            jobLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            // get the map ready
                            mapFragment.getMapAsync(JobFormActivity.this);
                        }
                    }
                });
    }

    /**
     * Gets all the job data from the form and returns a job object or throws appropriate toasts
     * according to the error and returns null
     * @return Job OR null
     */
    protected Job getJobData() {

        String jobTitle = ((EditText) findViewById(R.id.jobTitle)).getText().toString();
        String jobType = ((EditText) findViewById(R.id.jobType)).getText().toString();
        String jobDescription = ((EditText) findViewById(R.id.description)).getText().toString();
        String durationString = ((EditText) findViewById(R.id.Duration)).getText().toString();
        String payRateString = ((EditText) findViewById(R.id.payRate)).getText().toString();

        if(isNumeric(durationString, payRateString)) {
            int duration = durationString.equals("") ? 0 : Integer.parseInt(durationString);
            double payRate = payRateString.equals("") ? 0 : Double.parseDouble(payRateString);

            if (!isEmpty(jobTitle, jobType, jobDescription, duration, payRate)) {
                Random random = new Random();
                int num = random.nextInt(999999);

                String numString = String.format("%06d", num);
                durationString = String.format("%02d", duration);

                String jobID = jobType.substring(0, 2) + numString + durationString;

                return new Job(jobTitle, jobType, jobDescription, duration, payRate, jobID, jobLocation.latitude, jobLocation.longitude);
            }
        }

        return null;
    }

    /**
     * method to create Toast message upon error
     * @param messageId
     */
    protected void createToast(int messageId){
        Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_LONG).show();
    }

    /**
     * Checks if any of the field are empty
     * @param jobTitle
     * @param jobType
     * @param jobDescription
     * @param duration
     * @param payRate
     * @return true or false
     */
    protected boolean isEmpty(String jobTitle, String jobType, String jobDescription, int duration, double payRate) {
        boolean anyFieldsEmpty = jobTitle.isEmpty() || jobType.isEmpty() || jobDescription.isEmpty()
                || duration <= 0 || payRate <= 0;
        if (anyFieldsEmpty) {
            createToast(R.string.toast_missing_component);
            return true;
        }
        return false;
    }

    /**
     * checks if duration and pay rate are numeric or not
     * @param duration
     * @param payRate
     * @return
     */
    protected boolean isNumeric(String duration, String payRate){
        try{
            if(!duration.equals("")) {
                Double.parseDouble(duration);
                if(!payRate.equals("")){
                    Double.parseDouble(payRate);
                }
            }

            return true;
        }
        catch(Exception e){
            createToast(R.string.duration_pay_rate_numeric);
            return false;
        }
    }

    /**
     * method to validate if the jobID is already in the data base
     * if not in the data base it adds a new job to the data base
     * @param newJob
     */
    private void checkAndPushJob(Job newJob) {
        JobDAO databaseReference = new JobDAO();
        DatabaseReference dataBase = databaseReference.getDatabaseReference();

        dataBase.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean newPosting = true;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Job job = postSnapshot.getValue(Job.class);
                    String jobID = newJob.getJobID();
                    if (job != null && job.getJobID().equals(jobID)) {
                        createToast(R.string.job_id_exists);
                        Intent intent = new Intent(getApplicationContext(), EmployerHomeActivity.class);
                        newPosting = false;
                        startActivity(intent);
                    }
                }
                if (newPosting) {
                    addJob(newJob);
                    createToast(R.string.job_posted_successfully);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                final String errorRead = error.getMessage();
            }
        });
    }

    /**
     * adds the job to db
     * @param job
     */
    protected void addJob(JobInterface job) {
        JobDAO jobDAO = new JobDAO();
        jobDAO.addJob(job);
    }
}
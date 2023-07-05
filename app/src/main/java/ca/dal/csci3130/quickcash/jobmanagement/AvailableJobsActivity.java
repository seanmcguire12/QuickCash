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
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.dal.csci3130.quickcash.R;
import ca.dal.csci3130.quickcash.home.EmployeeHomeActivity;
import ca.dal.csci3130.quickcash.usermanagement.PreferenceActivity;
import ca.dal.csci3130.quickcash.usermanagement.PreferenceDAO;
import ca.dal.csci3130.quickcash.usermanagement.PreferenceInterface;
import ca.dal.csci3130.quickcash.usermanagement.Preferences;
import ca.dal.csci3130.quickcash.usermanagement.SessionManager;

public class AvailableJobsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private List<Job> jobList;
    private FusedLocationProviderClient fusedLocationClient;
    private static final Integer REQUEST_CODE = 123;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    private LatLng userLocation;
    private ArrayList<Marker> mJobMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_jobs);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.jobMap);

        jobList = new ArrayList<>();
        // grabs the location services api
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button returnHome = findViewById(R.id.btn_returnHome_employee);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AvailableJobsActivity.this, EmployeeHomeActivity.class);
                startActivity(intent);
            }
        });

        //import Preference logic
        Button importButton = (Button) findViewById(R.id.buttonImportPreferences);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importPref();
            }
        });

        getJobs();

        //filter jobs logic
        Button filterJobButton = (Button) findViewById(R.id.buttonApplyFilter);
        filterJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobType = grabJobType();
                String payRate = grabPayRate();
                String duration = grabDuration();
                getFilteredJobs(jobType, payRate, duration);
                if(jobList.isEmpty()){
                    createToast(R.string.no_job_found);
                }
                else {
                    createToast(R.string.job_found);
                }
            }
        });

    }

    /**
     * Gets all the jobs from the db
     */
    protected void getJobs() {
        JobDAO jobDAO = new JobDAO();
        DatabaseReference jobRef = jobDAO.getDatabaseReference();

        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Job job = dataSnapshot.getValue(Job.class);
                    // get jobs and add them to a global list
                    jobList.add(job);
                }

                // start loading the map
                loadMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                final String errorRead = error.getMessage();
            }
        });
    }

    /**
     * Gets all the jobs from the db
     */
    protected void getFilteredJobs(String jobType, String payRateS, String durationS) {
        jobList.clear();
        removeMarkers();
        JobDAO jobDAO = new JobDAO();
        DatabaseReference jobRef = jobDAO.getDatabaseReference();

        boolean jobTypeSpecified = !jobType.isEmpty();
        double payRate;
        double duration;

        //If no payRate specified its 0
        if(payRateS.isEmpty()){
            payRate = 0;
        } else {
            payRate = Double.valueOf(payRateS);
        }

        //If not duration specified its 100
        if(durationS.isEmpty()){
            duration = 100;
        } else {
            duration = Double.valueOf(durationS);
        }

        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Job job = dataSnapshot.getValue(Job.class);
                    //if job type specified
                    if(jobTypeSpecified){
                        if(jobType.equals(job.getJobType()) && payRate < job.getPayRate() && duration > job.getDuration()){
                            jobList.add(job);
                        }
                    }
                    //Jobtype not specified
                    else {
                        if(payRate < job.getPayRate() && duration > job.getDuration()){
                            jobList.add(job);
                        }
                    }

                }

                // start loading the map
                loadMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                final String errorRead = error.getMessage();
            }
        });
    }

    /**
     * Remove all markers
     */
    private void removeMarkers(){
        for (Marker marker : mJobMarkers){
            marker.remove();
        }
    }
    /**
     * Checks/Asks for location permissions and starts map load
     */
    protected void loadMap() {
        // checks if the permission is already granted
        if (ContextCompat.checkSelfPermission(AvailableJobsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if it is already granted, then get current location and start the map
            getCurrentLocationAndStartMap();

            // the code to add clickable ads would be added here or in the dummy method below
            methodToAddClickableAds();
        } else {
            // if not then ask for it
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE);
        }
    }

    /**
     * Gets current location of the user and starts the map
     */
    protected void getCurrentLocationAndStartMap() {
        if (ActivityCompat.checkSelfPermission(AvailableJobsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AvailableJobsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //This error is a android studio bug, the permissions are added
        fusedLocationClient
                .getCurrentLocation(100, cancellationTokenSource.getToken()) // 100 is PRIORITY_HIGH_ACCURACY
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // if location is not null, set it to user location
                        if (location != null) {
                            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            // get the map ready
                            mapFragment.getMapAsync(AvailableJobsActivity.this);
                        }
                    }
                });
    }

    /**
     * Invoked when map is ready
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // add job pins to map
        addJobPinsToMap();

        // if location was not retrievable for some reason, set to default - Dalhousie University
        LatLng location = userLocation == null ? new LatLng(44.636585, -63.5938442) : userLocation;

        // mark the location and zoom into that
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Your Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        mMap.addMarker(markerOptions).showInfoWindow();

        mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Intent intent = new Intent(AvailableJobsActivity.this, JobAdActivity.class);
                intent.putExtra("JobID", marker.getTag().toString());
                startActivity(intent);
                // need to find a way to pass the corresponding job to the jobAdActivity

            }
        });

    }

    protected void methodToAddClickableAds(){ }

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
                getCurrentLocationAndStartMap();
            } else {
                // if not move forward and start the map with a default location
                Toast.makeText(AvailableJobsActivity.this, "Permission denied by user !!!", Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(AvailableJobsActivity.this);
            }
        }
    }

    /**
     * add pins to the map and create links from pins to clickable ads
     */
    protected void addJobPinsToMap(){
        for(JobInterface job : jobList){
            // add pin
            LatLng pin = new LatLng(job.getLatitude(), job.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(pin)
                    .title(job.getJobTitle()));
            marker.setTag(job.getJobID());

            mJobMarkers.add(marker);
            // **can add marker links here**
        }
    }

    /**
     * Imports filter settings into edit text
     */
    private void importPref(){
        EditText jobTypeEdit = (EditText) findViewById(R.id.editTextSearchJobType);
        EditText payRateEdit = (EditText) findViewById(R.id.editTextSearchPayRate);
        EditText durationEdit = (EditText) findViewById(R.id.editTextSearchDuration);

        PreferenceDAO prefDAO = new PreferenceDAO();
        DatabaseReference databaseReference = prefDAO.getDatabaseReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean noPref = true;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PreferenceInterface preferenceItem = dataSnapshot.getValue(Preferences.class);
                    if(checkID(preferenceItem)){
                        jobTypeEdit.setText(preferenceItem.getJobType());
                        payRateEdit.setText(String.valueOf(preferenceItem.getPayRate()));
                        durationEdit.setText(String.valueOf(preferenceItem.getDuration()));
                        noPref = false;
                        break;
                    }
                }
                //if no preferences found
                if(noPref){
                    createToast(R.string.toast_no_preference_found);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                final String errorRead = error.getMessage();
            }
        });
    }

    private boolean checkID(PreferenceInterface preference){
        String id = grabEmail();
        return id.equals(preference.getUserID());
    }

    /**
     * Returns the email of the user signed in
     * @return
     */

    private String grabEmail() {

        SessionManager session = new SessionManager(AvailableJobsActivity.this);

        boolean isLoggedIn = session.isLoggedIn();

        if (isLoggedIn){
            return  session.getKeyEmail();
        }
        return null;
    }

    /**
     * returns text in jobtype
     */

    private String grabJobType(){
        EditText jobTypeEdit = (EditText) findViewById(R.id.editTextSearchJobType);
        return jobTypeEdit.getText().toString();
    }

    /**
     * returns text in payRate
     */

    private String grabPayRate(){
        EditText payRateEdit = (EditText) findViewById(R.id.editTextSearchPayRate);
        return payRateEdit.getText().toString();
    }

    /**
     * returns text in duration
     */

    private String grabDuration(){
        EditText durationEdit = (EditText) findViewById(R.id.editTextSearchDuration);
        return durationEdit.getText().toString();
    }

    /**
     * method to create Toast message upon error
     *
     * @param messageId
     */
    protected void createToast(int messageId) {
        Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_LONG).show();
    }
}
/*	SearchBarActivity.java is the entry point of the Nearby Restaurants application.
 * 
 * The life cycle of this activity is as follows:
 * 
 * onCreate()
 * 		initialize the first UI screen to be seen defined in res/search_layout.xml
 * 		set UI widgets for the search field and radius seekbar
 * 		build Google Api Client for Google Play Location Service
 * 		connect Api with connect() that will trigger ConnectionCallbacks: onConnected(Bundle connectionHint), onConnectionSuspended(int cause)
 * 
 * onStart()
 * 		Check if Google Api Client isConnected() determined by OnConnectionFailedListener: onConnectionFailed(ConnectionResult result)
 * 		Else use Android Location Service
 * 
 * onResume()
 * 		same as onStart()
 * 		While in this state: 
 * 			implement Android LocationListener or Google Play LocationListener
 * 			wait for click of Search button to invoke search(View v) by button attribute android:onClick="search" in res/search_layout.xml
 * 			search(View v)
 * 				start new Intent of YelpSearchListActivity.java with search parameters gathered from UI
 * 
 * onPause()
 * 		suspend listening to location updates
 * 
 * onStop()
 * 		disconnect location listeners
 * 
 * @author: michael barnes
 * */
package com.apps.michaelbarnes.RestaurantsNearby;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class SearchBarActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{


    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    private EditText mSearchTerm;//The search term recorded from UI search field will be stored here

	
	//These variables are used to initialize values for the Google Play Location Service
	protected static final String TAG = "location-updates-sample";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;//In the case of this app this is to initialize but we will just rely on 
    																//the LocationListener for updates and not update every 10s because it is not necessary
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected final static String LOCATION_KEY = "location-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected static LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    private TextView seekBarStatus;//Number of meters displayed on UI when seekbar dragged
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		setTitle("Search Restaurants Nearby");
		mSearchTerm = (EditText) findViewById(R.id.searchTerm);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBarStatus = (TextView) findViewById(R.id.textView3);
		
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue + 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarStatus.setText(String.valueOf(progress));
            }
        });
		

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        

	}

	public void search(View v){
		try {
			String term;
			if(mSearchTerm == null){
				term = "";
			}else{
				term = mSearchTerm.getText().toString();
			}
			String latitude = String.valueOf(mCurrentLocation.getLatitude());
			String longitude = String.valueOf(mCurrentLocation.getLongitude());
			double radiusAdjustment = Double.parseDouble((String) seekBarStatus.getText());
			String Radius = String.valueOf(radiusAdjustment);
			
			//Setting up Intent to take us to the next Activity
			Intent intent = new Intent(this, YelpSearchListActivity.class);
			intent.putExtra("query",new String[]{term, latitude, longitude, Radius});
			intent.putExtra("location", mCurrentLocation);
			startActivity(intent);
		} catch (NullPointerException e) {
			e.printStackTrace();
			Toast.makeText(this, "Location Not Available:\nCheck that location servieces are on",
                    Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	/**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location as defined in
     * the AndroidManifest.xml.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

   

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate().
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            startLocationUpdates();
        }

        

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        int errorCode = result.getErrorCode();

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + errorCode);

    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


}

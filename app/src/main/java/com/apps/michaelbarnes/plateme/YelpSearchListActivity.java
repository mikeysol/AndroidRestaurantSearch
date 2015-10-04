/*	YelpSearchListActivity.java is the second activity of the Nearby Restaurants application and follows SearchBarActivity.
 * 
 * The life cycle of this activity is as follows:
 * 
 * onCreate()
 * 		initialize the UI screen to be seen defined in res/search_results.xml
 * 		get a reference to the Google Api so we can reuse its Location Service data else reuse previous Android Location service
 * 		get search parameter data stored in the Intent invoked by SearchBarActivity.java
 * 		set the View to not be visible
 * 		start communicating with Yelp Search Api with a new instance of YelpQuery.java that will execute the query Asynchronously
 * 		YelpQuery will launch an adapter to handle the data we retrieve and apply it to our view 
 * 		 
 * onStart()
 * 		start location listeners the same as in SearchBarActivity.java
 * onResume()
 * 		start location listeners the same as in SearchBarActivity.java
 * onPause()
 * 		suspend location listeners the same as in SearchBarActivity.java
 * onStop()
 * 		stop location listeners the same as in SearchBarActivity.java
 * 
 * @author: michael barnes
 * */

package com.apps.michaelbarnes.plateme;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class YelpSearchListActivity extends ListActivity
implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
	  private Location mCurrentLocation;
	  private GoogleApiClient mGoogleApiClient;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Searching Nearby...");
		setContentView(R.layout.search_results);

        mGoogleApiClient = Singleton.getInstance().getApi();
		
        Intent intent = getIntent();
        final String searchTerm = "restaurants "+intent.getStringArrayExtra("query")[0];
        final String latitude = intent.getStringArrayExtra("query")[1];
        final String longitude = intent.getStringArrayExtra("query")[2];
        final String radius = intent.getStringArrayExtra("query")[3];
        
        setProgressBarIndeterminateVisibility(true);
        
        new YelpQuery(YelpSearchListActivity.this).execute(searchTerm, latitude, longitude, radius);
        
    }



/*
 * In The onLocationChanged method I wanted to launch a new YelpQuery search with the new location but the
 * problem I ran into is I kept getting an exception that would say my listview.setAdapter() method needed
 * to be passed an ExpandableListAdapter which I am doing so I don't understand why I get this exception
 */
@Override
public void onLocationChanged(Location location) {
	mCurrentLocation = location;
    Toast.makeText(this, this.getResources().getString(R.string.location_updated_message) + ": Press Back Button and then Search Button to see updated list.",
            Toast.LENGTH_SHORT).show();
    /*
    setTitle("Searching Nearby...");
	setContentView(R.layout.search_results);
	
	setProgressBarIndeterminateVisibility(true);
    
    
    new YelpQuery(this).execute(Container.getSearchTerm(), String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()), Container.getRadius());
    */
	
}





/**
 * Requests location updates from the FusedLocationApi.
 */
protected void startLocationUpdates() {
    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient, SearchBarActivity.mLocationRequest, this);
}


/**
 * Removes location updates from the FusedLocationApi.
 */
protected void stopLocationUpdates() {
	
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
}

@Override
protected void onStart() {
    super.onStart();
    //mGoogleApiClient.connect();
    if (mGoogleApiClient.isConnected()) {
        startLocationUpdates();
    }
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


@Override
public void onConnected(Bundle connectionHint) {
    Log.i(SearchBarActivity.TAG, "Connected to GoogleApiClient");

    // If the initial location was never previously requested, we use
    // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
    // its value in the Bundle and check for it in onCreate().
    if (mCurrentLocation == null) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

}



@Override
public void onConnectionSuspended(int cause) {
    mGoogleApiClient.connect();
}

@Override
public void onConnectionFailed(ConnectionResult result) {

}

public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putParcelable(SearchBarActivity.LOCATION_KEY, mCurrentLocation);
    super.onSaveInstanceState(savedInstanceState);
}
	
}

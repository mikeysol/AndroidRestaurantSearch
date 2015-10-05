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

package com.apps.michaelbarnes.RestaurantsNearby;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class YelpSearchListActivity extends ListActivity {

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Searching Nearby...");
		setContentView(R.layout.search_results);
		
        Intent intent = getIntent();
        final String searchTerm = "restaurants "+intent.getStringArrayExtra("query")[0];
        final String latitude = intent.getStringArrayExtra("query")[1];
        final String longitude = intent.getStringArrayExtra("query")[2];
        final String radius = intent.getStringArrayExtra("query")[3];
        
        setProgressBarIndeterminateVisibility(true);
        
        new YelpQuery(YelpSearchListActivity.this).execute(searchTerm, latitude, longitude, radius);
        
    }

@Override
protected void onStart() {
    super.onStart();

}

@Override
public void onResume() {
    super.onResume();

}

@Override
protected void onPause() {
    super.onPause();

}

@Override
protected void onStop() {
    super.onStop();
}


public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
}
	
}

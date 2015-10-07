/*	SearchListFragment.java is a list fragment hosted by SearchResutlsActivity the second activity of the Nearby Restaurants application following SearchBarActivity.
 * 
 * The life cycle of this activity is as follows:
 * 
 * onCreateView()
 * 		initialize the UI screen to be seen defined in res/search_results.xml
 * 		get search parameter data stored in the Intent invoked by SearchBarActivity.java
 * 		set the View to not be visible
 * 		start communicating with Yelp Search Api with a new instance of YelpQuery.java that will execute the query Asynchronously
 * 		YelpQuery will launch an adapter to handle the data we retrieve and apply it to our view 
 * 		 
 * onStart()
 * 		super implementation
 * onResume()
 * 		super implementation
 * onPause()
 * 		super implementation
 * onStop()
 * 		super implementation
 * 
 * @author: michael barnes
 * */

package com.apps.michaelbarnes.RestaurantsNearby;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchListFragment extends ListFragment {

	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_results, null);

        Intent intent = this.getActivity().getIntent();
        final String searchTerm = "restaurants "+intent.getStringArrayExtra("query")[0];
        final String latitude = intent.getStringArrayExtra("query")[1];
        final String longitude = intent.getStringArrayExtra("query")[2];
        final String radius = intent.getStringArrayExtra("query")[3];

        this.getActivity().setProgressBarIndeterminateVisibility(true);

        new YelpQuery(SearchListFragment.this.getActivity()).execute(searchTerm, latitude, longitude, radius);
        return v;
    }

@Override
public void onStart() {
    super.onStart();

}

@Override
public void onResume() {
    super.onResume();

}

@Override
public void onPause() {
    super.onPause();

}

@Override
public void onStop() {
    super.onStop();
}


public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
}
	
}

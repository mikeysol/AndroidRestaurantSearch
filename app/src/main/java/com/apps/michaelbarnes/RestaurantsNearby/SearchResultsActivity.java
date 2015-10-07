package com.apps.michaelbarnes.RestaurantsNearby;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SearchResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setTitle("Searching Nearby...");
        setContentView(R.layout.search_results_fragment);

    }


}

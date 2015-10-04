/*	YelpQuery.java will follow the the YelpSearchList.java which creates a new instance to get and process
 * the resulting Yelp search.
 * 
 * The life cycle of this object is as follows:
 * 
 * doInBackgroun(String... params)
 * 		connect to Yelp Search Api, query with params, and then return string of JSON
 * 		process the string and parse all JSON objects, then select JSON objects to store in Business.java object
 * 		
 * onPostExecute(List<Business> businesses)
 * 		take the resulting ArrayList<Business> and sort them by distance with the BusinessDistanceComparator.java
 * 		create ExpandableListView that points to res/restaurant_list.xml (the parent view) and set ExpandleListAdapter with MyExpandableListAdapter.java to handle Business objects to View
 * 		set the view to now be visible
 * 		bind list with adapter
 * 
 * @author: michael barnes
 * */
package com.apps.michaelbarnes.plateme;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YelpQuery extends AsyncTask<String, Void, List<Business>> {
	
	Context activity;
	
	public YelpQuery(Context activity){
		this.activity=activity;
	}
	
    	@Override
    	protected List<Business> doInBackground(String... params) {
            String businesses = Yelp.getYelp(activity).search(params[0], params[1], params[2],  params[3]);
            try {
            	return processJson(businesses);
            } catch (JSONException e) {
                return Collections.emptyList();
            }
    	}

    	@Override
    	protected void onPostExecute(List<Business> businesses) {
    		ArrayList<Business> b = new ArrayList<>();
    		Collections.sort(businesses, new BusinessDistanceComparator());
    		b.addAll(businesses);
    		ExpandableListView listView = (ExpandableListView) ((Activity) activity).findViewById(android.R.id.list);
    		ExpandableListAdapter adapter = new MyExpandableListAdapter((Activity) activity,
    		        b);
    		((Activity) activity).setTitle("Results Found");
    		((Activity) activity).setProgressBarIndeterminateVisibility(false);

    	    listView.setAdapter(adapter);
    	}
    	
    	List<Business> processJson(String jsonStuff) throws JSONException {
    		JSONObject json = new JSONObject(jsonStuff);
    		JSONArray businesses = json.getJSONArray("businesses");
    		ArrayList<Business> businessObjs = new ArrayList<>(businesses.length());
    		for (int i = 0; i < businesses.length(); i++) {
    			JSONObject business = businesses.getJSONObject(i);
    			
    			ArrayList<String> locationList = new ArrayList<>();
    			JSONArray jArray = business.optJSONObject("location").optJSONArray("display_address"); 
    			if (jArray != null) { 
    				for (int j=0;j<jArray.length();j++){ 
    					locationList.add(jArray.get(j).toString());
    				} 
    			}
    			
    			
    			businessObjs.add(new Business((String)business.opt("name"), (String)business.opt("url"), 
    					(String)business.opt("image_url"), (Number)business.opt("rating"), (Number)business.opt("distance"), 
    					(String)business.opt("display_phone"), locationList, 
    					(Number) business.optJSONObject("location").optJSONObject("coordinate").opt("latitude"), 
    					(Number) business.optJSONObject("location").optJSONObject("coordinate").opt("longitude") ));
    		}
    		return businessObjs;
    	}

}

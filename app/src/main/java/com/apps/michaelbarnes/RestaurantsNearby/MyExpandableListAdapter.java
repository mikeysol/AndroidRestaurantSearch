/*	MyExpandableListAdapter.java delegates what the Business object data 
 * will bind to in the res/restaurant_list.xml (Parent View) and the res/rowlayout.xml (Child View)
 * 
 * There are two methods to focus on which are getGroupView(int groupPosition, boolean isExpanded,
 *    View convertView, ViewGroup parent) and the getChildView(int groupPosition, final int childPosition,
 *    boolean isLastChild, View convertView, ViewGroup parent).
 *    
 *getGroupView(...)
 * 		inflates restaurant_list.xml and puts the business name and distance on the listview
 * 				
 *getChildView(...)
 *      has a reference to its group view and inflates the rowlayout.xml for the group's child view
 *      the business image, url, rating, address, and phone will be binded respectively
 *      onClickListeners will be associated with the views to launch other intents like web browser, maps app, and call phone
 * 
 * @author: michael barnes
 * */
package com.apps.michaelbarnes.RestaurantsNearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyExpandableListAdapter extends BaseExpandableListAdapter {

  private final ArrayList<Business> groups;
  public LayoutInflater inflater;
  public Activity activity;
  private Context context;

  public MyExpandableListAdapter(Activity act, ArrayList<Business> groups) {
    activity = act;
    this.groups = groups;
    inflater = act.getLayoutInflater();
    this.context = (Context) act;
  }

  static class ViewHolder{//Used to cache data in child view to speed up performance
	  protected TextView firstLine;
	  protected TextView secondLine;
	  protected TextView thirdLine;
	  protected ImageView icon;
	  protected RatingBar rBar;
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return groups.get(groupPosition).getChildren().get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return 0;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
      boolean isLastChild, View convertView, ViewGroup parent) {

	  final int parentPos = groupPosition;
	  ViewHolder vh;

	    if (convertView == null) {//converView recycles and reuses its view and so we only need to inflate the layout once per group
		      convertView = inflater.inflate(R.layout.rowlayout, null);

		      vh = new ViewHolder();

		      vh.icon = (ImageView) convertView.findViewById(R.id.icon);//restaurant image
			  vh.firstLine = (TextView) convertView.findViewById(R.id.firstLine);//restaurant url
			  vh.secondLine = (TextView) convertView.findViewById(R.id.secondLine);//restaurant address
			  vh.thirdLine = (TextView) convertView.findViewById(R.id.thirdLine);//restaurant phone number
			  vh.rBar = (RatingBar) convertView.findViewById(R.id.ratingBar1);//restaurant rating

			  convertView.setTag(vh);

		    }else{//get the recycled view
		    	vh = (ViewHolder) convertView.getTag();
		    }



		  new ImageDownloader(vh.icon).execute((String)getChild(groupPosition, 1));//AsyncTask to download image at URL
		  vh.firstLine.setText((String) getChild(groupPosition, 0));
		  String address ="";
		  ArrayList<String> fields = (ArrayList<String>) getChild(groupPosition, 5);//a list of strings are returned for address so we need to concat to one line
		  for(int i=0; i<fields.size(); i++){

			  address += fields.get(i);
			  if(i!=(fields.size()-1)){
				address += ", ";
			  }
		  }
		  vh.secondLine.setText(address);
		  vh.thirdLine.setText((String) getChild(groupPosition, 4));
		  vh.rBar.setRating(((Double) getChild(groupPosition, 2)).floatValue());


		  //Listeners
		  vh.icon.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  Toast.makeText(activity, (String) getChild(parentPos, 1),
                          Toast.LENGTH_SHORT).show();
		    	  }
		    	  });

		  vh.firstLine.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {//Starts an intent to open a web browser to go to Yelp Restaurant page
		    	  activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String) getChild(parentPos, 0))));
		    	  }
		    	  });

		  vh.secondLine.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {//Starts an intent to launch the Google Maps app to navigate to restaurant location
		    	  Number latitude = (Number) getChild(parentPos, 6);
		    	  Number longitude = (Number) getChild(parentPos, 7);
		    	// Create a Uri from an intent string. Use the result to create an Intent.
		    	  Uri gmmIntentUri = Uri.parse("google.navigation:q="+String.valueOf(latitude)+","+String.valueOf(longitude));

		    	  // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
		    	  Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		    	  // Make the Intent explicit by setting the Google Maps package
		    	  mapIntent.setPackage("com.google.android.apps.maps");

		    	  if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {

		    	  // Attempt to start an activity that can handle the Intent
		    	  activity.startActivity(mapIntent);
		    	  }
		    	  }
		    	  });

		  vh.thirdLine.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {//Starts an intent to call restaurant phone number
		    	  String phone = (String)getChild(parentPos, 4);
		    	  Toast.makeText(activity, phone,
		  	            Toast.LENGTH_SHORT).show();
		    	  Intent callIntent = new Intent(Intent.ACTION_CALL);
		            callIntent.setData(Uri.parse("tel:"+phone));
		            activity.startActivity(callIntent);
		    	  }
		    	  });
	    
	    
	    
    
    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return 1;//groups.get(groupPosition).getChildren().size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return groups.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return groups.size();
  }

  @Override
  public void onGroupCollapsed(int groupPosition) {
    super.onGroupCollapsed(groupPosition);
  }

  @Override
  public void onGroupExpanded(int groupPosition) {
    super.onGroupExpanded(groupPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return 0;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded,
      View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.restaurant_list, null);
    }
    Business group = (Business) getGroup(groupPosition);
    TextView name = (TextView) convertView.findViewById(R.id.restName);//restaurant name
    TextView dist = (TextView) convertView.findViewById(R.id.restDist);//restaurant distance in meters
    name.setText(group.getName());
    dist.setText(String.format("%.2f",group.getDistance())+"m");
    return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false;
  }
}

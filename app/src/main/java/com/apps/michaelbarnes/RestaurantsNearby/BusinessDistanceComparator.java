package com.apps.michaelbarnes.RestaurantsNearby;

import java.util.Comparator;

public class BusinessDistanceComparator implements Comparator<Business> {

	@Override
	public int compare(Business b1, Business b2) {
	        if(((Double)b1.getDistance()) > ((Double)b2.getDistance())){
	            return 1;
	        } else {
	            return -1;
	        }
	    }

}

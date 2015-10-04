package com.apps.michaelbarnes.plateme;

import com.google.android.gms.common.api.GoogleApiClient;

public class Singleton {
    private static Singleton mInstance = null;
 
    private GoogleApiClient mGoogleApiClient;
 
    private Singleton(){
    }
 
    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }
 
    public GoogleApiClient getApi(){
        return this.mGoogleApiClient;
    }
 
    public void setApi(GoogleApiClient value){
        mGoogleApiClient = value;
    }


}

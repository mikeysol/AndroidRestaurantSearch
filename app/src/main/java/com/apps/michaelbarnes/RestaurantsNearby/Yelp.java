package com.apps.michaelbarnes.RestaurantsNearby;


import android.content.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Accessing the Yelp API.
 */
public class Yelp {

  OAuthService service;
  Token accessToken;

  public static Yelp getYelp(Context context) {
	  return new Yelp(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret),
			  context.getString(R.string.token), context.getString(R.string.token_secret));
  }

  /**
   * Setup the Yelp API OAuth credentials.
   *
   * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
   *
   * @param consumerKey Consumer key
   * @param consumerSecret Consumer secret
   * @param token Token
   * @param tokenSecret Token secret
   */
  public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }

  /**
   * Search with term and location.
   *
   * @param term Search term
   * @param latitude Latitude
   * @param longitude Longitude
   * @param radius Radius
   * @return JSON string response
   */
  public String search(String term, String latitude, String longitude, String radius) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latitude + "," + longitude);
    request.addQuerystringParameter("radius_filter", radius);
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    return response.getBody();
  }


}

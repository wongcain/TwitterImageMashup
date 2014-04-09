package com.cainwong.twitterimagemashup;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetImageListItem {
	private static final String LOG_TAG = TweetImageListItem.class.getSimpleName();

	private Date date;
	private String tweet;
    private String imageUrl;
	
	public TweetImageListItem(Date date, String tweet) {
		super();
		this.date = date;
		this.tweet = tweet;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		return tweet + "\n" + SimpleDateFormat.getDateTimeInstance().format(date);
	}

	/*
	 * Queries Twitter timeline for a list of tweets
	 */
	public static List<TweetImageListItem> getTweets(String twitterUser){
		List<TweetImageListItem> tweets = new ArrayList<TweetImageListItem>();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(IConstants.TWITTER_CONSUMER_KEY)
				.setOAuthConsumerSecret(IConstants.TWITTER_CONSUMER_SECRET).setOAuthAccessToken("")
				.setOAuthAccessTokenSecret("");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			List<twitter4j.Status> statuses;
			statuses = twitter.getUserTimeline(twitterUser);
			for (twitter4j.Status status : statuses) {
				Log.d(LOG_TAG, status.getText());
				tweets.add(new TweetImageListItem(status.getCreatedAt(), status.getText()));
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return tweets;
	}
	
	public static void sortChron(List<TweetImageListItem> tweets){
		Collections.sort(tweets, new Comparator<TweetImageListItem>(){
			@Override
			public int compare(TweetImageListItem t1, TweetImageListItem t2) {
				return 0 - (t1.getDate().compareTo(t2.getDate()));
			}});
	}

	public static void sortAlpha(List<TweetImageListItem> tweets){
		Collections.sort(tweets, new Comparator<TweetImageListItem>(){
			@Override
			public int compare(TweetImageListItem t1, TweetImageListItem t2) {
				return t1.getTweet().compareTo(t2.getTweet());
			}});
	}

}

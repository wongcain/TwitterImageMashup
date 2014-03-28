package com.cainwong.twitterimagemashup;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class TweetImageListItem {
	private static final String GOOGLE_IMG_SEARCH_BASE = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
	private static final String LOG_TAG = TweetImageListItem.class.getSimpleName();
	private Date date;
	private String tweet;
	private Bitmap image;
	
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

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
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

	/*
	 * Performs Google Image search and retrieves first result
	 */
	public void initImage(){
		try {
			String json = HttpRequest.get(GOOGLE_IMG_SEARCH_BASE + URLEncoder.encode(getImageSearchString(tweet), "UTF-8")).body();
			JSONObject response = new JSONObject(json);
			JSONObject responseData = response.getJSONObject("responseData");
			if(responseData != null && responseData.has("results") && responseData.getJSONArray("results").length()>0){
				String imgUrl = responseData.getJSONArray("results").getJSONObject(0).getString("unescapedUrl");
				InputStream in = new java.net.URL(imgUrl).openStream();
				image = BitmapFactory.decodeStream(in);
			}
			Log.d(LOG_TAG, "Image Fetched for " + tweet);
		} catch (UnsupportedEncodingException e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Simple filter for deriving search string from a tweet. 
	 * Returns a string containing only words that start with a capital letter... mostly proper nouns.
	 */
	public static String getImageSearchString(String text){
		StringBuilder sb = new StringBuilder();
		String[] split = text.split(" ");
		for(String s: split){
			if(Character.isUpperCase(s.charAt(0))){
				if(sb.length()>0){
					sb.append(" ");
				}
				sb.append(s.replaceAll("[^a-zA-Z]", ""));
			}
		}
		return sb.toString();
	}
}

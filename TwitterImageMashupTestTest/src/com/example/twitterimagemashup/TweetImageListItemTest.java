package com.example.twitterimagemashup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.test.AndroidTestCase;

import com.cainwong.twitterimagemashup.IConstants;
import com.cainwong.twitterimagemashup.TweetImageListItem;

public class TweetImageListItemTest extends AndroidTestCase {
	
	public static final String TEST_TWEET = "123 something (*^^!*&@#^(*!& Google lkadsKIHJLIU *^!*^#!(*&@#12831234123";
	public static final String TEST_SEARCH_STRING = "Google";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetTweets() throws Exception {
		List<TweetImageListItem> tweets = TweetImageListItem.getTweets(IConstants.TWITTER_USER);
		assertEquals(20, tweets.size());
	}
	
	public void testGetSearchString() throws Exception {
		String s = TweetImageListItem.getImageSearchString(TEST_TWEET);
		assertEquals(TEST_SEARCH_STRING, s);
	}
	
	public void testInitImage() throws Exception {
		TweetImageListItem tweet = new TweetImageListItem(new Date(), TEST_SEARCH_STRING);
		tweet.initImage();
		assertNotNull(tweet.getImage());
		int w = tweet.getImage().getWidth();
		int h = tweet.getImage().getHeight();
		tweet.setTweet(TEST_TWEET);
		tweet.setImage(null);
		tweet.initImage();
		assertNotNull(tweet.getImage());
		assertEquals(w, tweet.getImage().getWidth());
		assertEquals(h, tweet.getImage().getHeight());
	}
	
	public void testSorts() throws Exception {
		List<TweetImageListItem> tweets = new ArrayList<TweetImageListItem>();
		Date date = new Date();
		for(int i=0; i<100; i++){
			tweets.add(new TweetImageListItem(new Date(date.getTime() + 5*60*1000*i), "testing_" + i));
		}
		TweetImageListItem.sortChron(tweets);
		assertEquals("testing_99", tweets.get(0).getTweet());
		TweetImageListItem.sortAlpha(tweets);
		assertEquals("testing_0", tweets.get(0).getTweet());
	}

}

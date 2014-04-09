package com.cainwong.twitterimagemashup;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.twitterimagemashup.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

public class TweetImageListAdapter extends ArrayAdapter<TweetImageListItem> {
    private static final String GOOGLE_IMG_SEARCH_BASE = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
	private static final String LOG_TAG = TweetImageListAdapter.class.getSimpleName();
	private final Activity context;
	private final List<TweetImageListItem> tweets;

	public TweetImageListAdapter(Activity context, List<TweetImageListItem> tweets) {
		super(context, R.layout.list_single, tweets);
		this.context = context;
		this.tweets = tweets;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		TextView txtDate = (TextView) rowView.findViewById(R.id.date);
        NetworkImageView imageView = (NetworkImageView) rowView.findViewById(R.id.img);
		TweetImageListItem tweet = tweets.get(position);
		txtTitle.setText(tweet.getTweet());
		txtDate.setText(SimpleDateFormat.getDateTimeInstance().format(tweet.getDate()));
        showTweetImage(imageView, tweet);
		return rowView;
	}

    private void showTweetImage(NetworkImageView imageView, TweetImageListItem tweet){
        String imageUrl = tweet.getImageUrl();
        if (imageUrl == null) {
            initTweetImage(imageView, tweet);
        } else {
            imageView.setImageUrl(imageUrl, VolleySingleton.getInstance().getImageLoader());
        }
    }

    private void initTweetImage(final NetworkImageView imageView, final TweetImageListItem tweet){
        String url = GOOGLE_IMG_SEARCH_BASE + URLEncoder.encode(getImageSearchString(tweet.getTweet()));
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String imageUrl = null;
                        try {
                            JSONObject responseData = response.getJSONObject("responseData");
                            if (responseData != null && responseData.has("results") && responseData.getJSONArray("results").length() > 0) {
                                imageUrl = responseData.getJSONArray("results").getJSONObject(0).getString("unescapedUrl");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        tweet.setImageUrl(imageUrl);
                        showTweetImage(imageView, tweet);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        VolleySingleton.getInstance().getRequestQueue().add(req);
    }

    /*
     * Simple filter for deriving search string from a tweet.
     * Returns a string containing only words that start with a capital letter... mostly proper nouns.
     */
    public String getImageSearchString(String text){
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
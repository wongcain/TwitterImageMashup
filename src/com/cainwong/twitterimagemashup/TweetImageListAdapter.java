package com.cainwong.twitterimagemashup;

import java.text.SimpleDateFormat;
import java.util.List;

import com.example.twitterimagemashup.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetImageListAdapter extends ArrayAdapter<TweetImageListItem> {
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
		ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
		TweetImageListItem tweet = tweets.get(position);
		txtTitle.setText(tweet.getTweet());
		txtDate.setText(SimpleDateFormat.getDateTimeInstance().format(tweet.getDate()));
		if (tweet.getImage() == null) {
			imageView.setImageResource(R.drawable.ic_launcher);
			imageView.invalidate();
			new SearchAndDownloadImage(imageView, tweet).execute();
			Log.d(LOG_TAG, "fetching image");
		} else {
			Log.d(LOG_TAG, "img already set");
			imageView.setImageBitmap(tweet.getImage());
			imageView.invalidate();
		}
		return rowView;
	}

	protected class SearchAndDownloadImage extends AsyncTask<Void, Void, Void> {
		private final ImageView imageView;
		private final TweetImageListItem tweet;

		public SearchAndDownloadImage(ImageView imageView, TweetImageListItem tweet) {
			this.imageView = imageView;
			this.tweet = tweet;
		}

		@Override
		protected Void doInBackground(Void... params) {
			tweet.initImage();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(tweet.getImage() != null){
				imageView.setImageBitmap(tweet.getImage());
				imageView.invalidate();
			}
		}

	}
	
}
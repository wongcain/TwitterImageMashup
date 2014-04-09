package com.cainwong.twitterimagemashup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.twitterimagemashup.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	private ListView list;
	private List<TweetImageListItem> tweets = new ArrayList<TweetImageListItem>();
	private ArrayAdapter<TweetImageListItem> adapter;

	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		list = (ListView) findViewById(R.id.list);
		new GetTimeline().execute(IConstants.TWITTER_USER);
        VolleySingleton.getInstance(getApplicationContext());
	}

    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sort_alpha:
				TweetImageListItem.sortAlpha(tweets);
				refreshListView();
				return true;
			case R.id.sort_chron:
				TweetImageListItem.sortChron(tweets);
				refreshListView();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void refreshListView(){
		adapter = new TweetImageListAdapter(MainActivity.this, tweets);
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	protected void showProgress(String msg){
		pDialog = new ProgressDialog(MainActivity.this);
		pDialog.setMessage(msg);
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	protected void hideProgress(){
		pDialog.dismiss();
	}

	protected class GetTimeline extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress("Fetching Twitter Feed...");
		}

		@Override
		protected Void doInBackground(String... params) {
			tweets = TweetImageListItem.getTweets(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			refreshListView();
			hideProgress();
		}

	}
	
	

}

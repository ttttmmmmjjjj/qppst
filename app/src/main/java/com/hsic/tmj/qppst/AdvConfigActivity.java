package com.hsic.tmj.qppst;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hsic.tmj.fragment.AdvConfigFragment;


public class AdvConfigActivity extends Activity{
	String TAG="AdvConfigActivity";
	public Context getContext() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new AdvConfigFragment()).commit();
	}
		private void operatePreference(Preference preference) {
			if (preference.getKey().equals("bluetooth")) {
			}
		}

		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			Log.i(TAG,
					"onPreferenceClick----->" + String.valueOf(preference.getKey()));
			operatePreference(preference);
			return false;
		}

		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
				Preference preference) {
			return true;
		};

		public boolean onPreferenceChange(Preference preference, Object objValue) {
			Log.i(TAG,
					"onPreferenceChange----->"
							+ String.valueOf(preference.getKey()));

			return true; //
		}

		@Override
		protected void onStart() {
			// ActionBar actionBar = this.getActionBar();
			// actionBar.setDisplayHomeAsUpEnabled(true);
			// actionBar.setDisplayShowTitleEnabled(true);
			super.onStart();
		}

		@Override
		public void onStop() {
			super.onStop();
		}

		// load action bar
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			return super.onCreateOptionsMenu(menu);
		}

		// action bar method
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				// NavUtils.navigateUpFromSameTask(this);
				finish();
				break;
			}
			return super.onOptionsItemSelected(item);
		}
}

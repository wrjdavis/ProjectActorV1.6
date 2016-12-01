package com.tedrasoft.templategoogle;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
	Parse.initialize(this, getResources().getString(R.string.parse_id), getResources().getString(R.string.parse_client_id)); 

	// Specify an Activity to handle all pushes by default.
	
	ParsePush.subscribeInBackground("", new SaveCallback() {
		 
		@Override
		public void done(ParseException e) {
			if (e == null) {
			      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
			    } else {
			      Log.e("com.parse.push", "failed to subscribe for push", e);
			    }
			
		}
		});
  }
}
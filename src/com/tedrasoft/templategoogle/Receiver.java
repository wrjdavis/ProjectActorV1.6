package com.tedrasoft.templategoogle;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.SaveCallback;

public class Receiver extends ParsePushBroadcastReceiver {
	static JSONObject jObj = null;
	static String URL=null;
    @Override
    public void onPushOpen(Context context, Intent intent) {
//    	Parse.initialize(context, "BbFHxaqTlv3AD6YU8IIS3FJjdVWwLyJyMbMcYZGK", "webXOrkY9XO8875kIF0tIwPbih2E4J5dKKLurVkR");
//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
//                } else {
//                    Log.e("com.parse.push", "failed to subscribe for push", e);
//                }
//            }
//        });
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Log.e("Push", "Clicked");
        URL="";
        Bundle extras = intent.getExtras(); 
        String jsonData = extras.getString( "com.parse.Data" );
        try {
            jObj = new JSONObject(jsonData);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        try {
			URL = jObj.getString("badge");
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
        
        if(!URL.equals("")){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/details?id="+URL));
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           context.startActivity(i);
            
           
        }
        else{
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        }
    }

	
}
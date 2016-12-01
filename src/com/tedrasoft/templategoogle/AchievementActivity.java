package com.tedrasoft.templategoogle;



import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AchievementActivity extends Activity implements AppConstants {
	Button ok;
	Button cancel;
	TextView txtMessage;
	SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_achievement);
		settings = getSharedPreferences(PREFS_NAME,  0);
		String text= getResources().getString(R.string.text1_achievement)+getLevel()
				+getResources().getString(R.string.text2_achievement);
		ok=(Button) findViewById(R.id.btn_ok_achievement);
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "Font/game_font.ttf");
		ok.setTypeface(tf);
		ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(ACHIEVEMENT_OK);
				finish();
			}});
		cancel=(Button) findViewById(R.id.btn_no_achievement);
		cancel.setTypeface(tf);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				setResult(ACHIEVEMENT_CANCEL);
				finish();
			}});
		txtMessage=(TextView)findViewById(R.id.txt_Achievement);
		txtMessage.setText(text);
	}


	/**
	 * Get current level saved in shared settings
	 * 
	 * @return current level
	 */
	public int getLevel() {
		int level = settings.getInt(LEVEL, 1);
		return level;
	}
	
	
	@Override
	public void onBackPressed() {
		setResult(ACHIEVEMENT_CANCEL);
		finish();
	}

}

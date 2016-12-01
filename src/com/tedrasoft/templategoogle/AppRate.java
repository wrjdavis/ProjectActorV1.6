package com.tedrasoft.templategoogle;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * Resolves rating the application request
 *
 */
public class AppRate implements android.content.DialogInterface.OnClickListener, OnCancelListener,AppConstants {

	private static final String TAG = "AppRater";
	//activity which requested rating
private SuccessActivity hostActivity;
	private OnClickListener clickListener;
	private SharedPreferences preferences;
	private AlertDialog.Builder dialogBuilder = null;
	//minimum launches until app rating dialog appears
	private long minLaunchesUntilPrompt = 0;
	//minimum days until app rating appears
	private long minDaysUntilPrompt = 0;
private AlertDialog createdDialog;

public AppRate(SuccessActivity hostActivity) {
		this.hostActivity = hostActivity;
		preferences = hostActivity.getSharedPreferences(PREFS_NAME, 0);
	}

	/**
	 * @param minLaunchesUntilPrompt The minimum number of days before showing the rate dialog.<br/>
	 *            Default value is 0 days.
	 * @return This {@link AppRate} object to allow chaining.
	 */
	public AppRate setMinLaunchesUntilPrompt(long minLaunchesUntilPrompt) {
		this.minLaunchesUntilPrompt = minLaunchesUntilPrompt;
		return this;
	}

	/**
	 * @param minDaysUntilPrompt The minimum number of times the user lunches the application before showing the rate dialog.<br/>
	 *            Default value is 0 times.
	 * @return This {@link AppRate} object to allow chaining.
	 */
	public AppRate setMinDaysUntilPrompt(long minDaysUntilPrompt) {
		this.minDaysUntilPrompt = minDaysUntilPrompt;
		return this;
	}

	

	/**
	 * Use this method if you want to customize the style and content of the rate dialog.<br/>
	 * When using the {@link AlertDialog.Builder} you should use:
	 * <ul>
	 * <li>{@link AlertDialog.Builder#setPositiveButton} for the <b>rate</b> button.</li>
	 * <li>{@link AlertDialog.Builder#setNeutralButton} for the <b>rate later</b> button.</li>
	 * <li>{@link AlertDialog.Builder#setNegativeButton} for the <b>never rate</b> button.</li>
	 * </ul>
	 * @param customBuilder The custom dialog you want to use as the rate dialog.
	 * @return This {@link AppRate} object to allow chaining.
	 */
	public AppRate setCustomDialog(AlertDialog.Builder customBuilder) {
		dialogBuilder = customBuilder;
		return this;
	}

	/**
	 * Reset all the data collected about number of launches and days until first launch.
	 * @param context A context.
	 */
	public static void reset(Context context) {
		context.getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
		Log.d(TAG, "Cleared AppRate shared preferences.");
	}

	/**
	 * Display the rate dialog if needed.
	 */
	public void init() {

		Log.d(TAG, "Init AppRating dialog");

		if (preferences.getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
			return;
		}

		
		Editor editor = preferences.edit();

		// Get and increment launch counter.
		long launch_count = preferences.getLong(PREF_LAUNCH_COUNT, 0) + 1;
		editor.putLong(PREF_LAUNCH_COUNT, launch_count);

		// Get date of first launch.
		Long date_firstLaunch = preferences.getLong(PREF_DATE_FIRST_LAUNCH, 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong(PREF_DATE_FIRST_LAUNCH, date_firstLaunch);
		}

		// Show the rate dialog if needed.
		if (launch_count >= minLaunchesUntilPrompt) {
			if (System.currentTimeMillis() >= date_firstLaunch + (minDaysUntilPrompt * DateUtils.DAY_IN_MILLIS)) {

				if (dialogBuilder != null) {
showDefaultDialog();//showDialog(dialogBuilder);
				} else {
					showDefaultDialog();
				}
			}
		}

		editor.commit();
	}

	

	/**
	 * Shows the default rate dialog.
	 * @return
	 */
	private void showDefaultDialog() {

		Log.d(TAG, "Create default dialog.");
		Typeface tf = Typeface.createFromAsset(hostActivity.getContext().getAssets(),
				"Font/game_font.ttf");
		String title = hostActivity.getResources().getString(R.string.rate_dialog_rate) + " \""+getApplicationName(hostActivity.getApplicationContext())+"\"!";
		String message = hostActivity.getResources().getString(R.string.rate_dialog_enjoy) + " \""+ getApplicationName(hostActivity.getApplicationContext()) + "\" "+ hostActivity.getResources().getString(R.string.rate_dialog_please);
		String rate = hostActivity.getResources().getString(R.string.rate_dialog_rate_it);
		String remindLater =hostActivity.getResources().getString(R.string.rate_dialog_remind);
//	String dismiss = hostActivity.getResources().getString(R.string.rate_dialog_dismiss);
		LayoutInflater inflater = hostActivity.getLayoutInflater();
		View view=inflater.inflate(R.layout.dialog_layout, null);
		
		
		TextView titleView = (TextView)view.findViewById(R.id.rate_title);
		TextView messageView = (TextView)view.findViewById(R.id.rate_message);
		Button btnRate=(Button)view.findViewById(R.id.rate_rate);
		//Button btnDismiss=(Button)view.findViewById(R.id.rate_never);
		Button btnRemind=(Button)view.findViewById(R.id.rate_remind);
		
		//btnDismiss.setTypeface(tf);
		//btnRate.setTypeface(tf);
		//btnRemind.setTypeface(tf);
		
		btnRate.setText(rate);
		btnRate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//go to product page for rating
				Editor editor = preferences.edit();
				String packageName=hostActivity.getPackageName();
				hostActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hostActivity.getResources().getString(R.string.rate_dialog_base_url) +packageName)));
				editor.putBoolean(PREF_DONT_SHOW_AGAIN, true);
				hostActivity.setIsRated();
				editor.commit();
				createdDialog.dismiss();

			}

		});
		//btnDismiss.setText(dismiss);
	//	btnDismiss.setOnClickListener(new View.OnClickListener() {

	//		@Override
	//		public void onClick(View v) {
			
	//			createdDialog.dismiss();
	//		}

	//	});
		btnRemind.setText(remindLater);
		btnRemind.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createdDialog.dismiss();

			}

		});
		titleView.setText(title);
		messageView.setText(message);
		//builder.setCustomTitle(view);
		//AlertDialog alert = builder.create();
		createdDialog = new AlertDialog.Builder(hostActivity).setView(view).setOnCancelListener(this).create();
		createdDialog.show();		
		/*.setTitle(title)
				.setMessage(message)
				.setPositiveButton(rate, this)
				.setNegativeButton(dismiss, this)
				.setNeutralButton(remindLater, this)
				.setOnCancelListener(this)
				.create().show();
*/
	}

	/**
	 * Show the custom rate dialog.
	 * @return
	 */
	private void showDialog(AlertDialog.Builder builder) {

		Log.d(TAG, "Create custom dialog.");

		AlertDialog dialog = builder.create();
		dialog.show();

		String rate = (String) dialog.getButton(AlertDialog.BUTTON_POSITIVE).getText();
		String remindLater = (String) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).getText();
		String dismiss = (String) dialog.getButton(AlertDialog.BUTTON_NEGATIVE).getText();

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, rate, this);
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, remindLater, this);
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, dismiss, this);

		dialog.setOnCancelListener(this);
	}

	@Override
	public void onCancel(DialogInterface dialog) {

		Editor editor = preferences.edit();
		editor.putLong(PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
		editor.putLong(PREF_LAUNCH_COUNT, 0);
		editor.commit();
	}

	/**
	 * @param onClickListener A listener to be called back on.
	 * @return This {@link AppRate} object to allow chaining.
	 */
	public AppRate setOnClickListener(OnClickListener onClickListener){
		clickListener = onClickListener;
		return this;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		Editor editor = preferences.edit();
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			//go to product page for rating
			String packageName=hostActivity.getPackageName();
			hostActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hostActivity.getResources().getString(R.string.rate_dialog_base_url) +packageName)));
			editor.putBoolean(PREF_DONT_SHOW_AGAIN, true);
			hostActivity.setIsRated();
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			//editor.putBoolean(PREF_DONT_SHOW_AGAIN, true);
			//do nothing
			break;

		case DialogInterface.BUTTON_NEUTRAL:
			//editor.putLong(PREF_DATE_FIRST_LAUNCH, System.currentTimeMillis());
			//editor.putLong(PREF_LAUNCH_COUNT, 0);
			//do nothing
			break;

		default:
			break;
		}

		editor.commit();
		dialog.dismiss();

		if(clickListener != null){
			clickListener.onClick(dialog, which);
		}
	}

	/**
	 * @param context A context of the current application.
	 * @return The application name of the current application.
	 */
	private static final String getApplicationName(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			applicationInfo = null;
		}
		return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
	}
	
	public void dismissDialog(){
		if(createdDialog!=null){
			try{
				createdDialog.dismiss();
				}catch(Exception e){
					
				}
		}
	}
}
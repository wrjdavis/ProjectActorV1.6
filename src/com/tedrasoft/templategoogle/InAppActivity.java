package com.tedrasoft.templategoogle;


import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.tedrasoft.templategoogle.util.IabHelper;
import com.tedrasoft.templategoogle.util.IabResult;
import com.tedrasoft.templategoogle.util.Inventory;
import com.tedrasoft.templategoogle.util.Purchase;
import com.tedrasoft.templategoogle.util.SkuDetails;

 





import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This can be replaced by a InAppPurchase Activity, or added together with current options. Activity displays the ways in which the user can win additional free coins. 
 * These free coins can be obtain by rating the application, downloading (actually clicking as there is no way yet to be sure user downloaded it) a free game from RevMob or Chartboost,
 * or sharing a riddle on Facebook)
 * From this activity only Rate App is available , the rest are available in SceneActivity
 * @author Dragos
 *
 */
public class InAppActivity extends Activity implements AppConstants {
	public static final String TAG = "InAppActivity";
	private Button btnIapCancel;
	//private Button btnIapRate;
	private SharedPreferences settings;
	private TextView txtStatusIap;
	//button to buy item 1 
	private Button btnIap1;
	//button to buy item 2
	private Button btnIap2;
	private Button btnIap3;
	//text view item 1
	private TextView txtIap1;
	//text view item 2
	private TextView txtIap2;
	 // The helper object
    IabHelper mHelper;
    
    String base64EncodedPublicKey = "";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_app);
		settings = getSharedPreferences(PREFS_NAME, 0);
		base64EncodedPublicKey=getResources().getString(R.string.google_base64EncodedPublicKey);
		Log.d(TAG, "google base64 encoded "+base64EncodedPublicKey);
		//get font for buttons
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Font/game_font.ttf");
		
		
		btnIap1=(Button) findViewById(R.id.btn_iap_1);
		btnIap1.setTypeface(tf);
		btnIap1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//buy 100 coins
				buyCoins(getResources().getString(R.string.sku_inapp_100));
				
			}
		});
		btnIap2=(Button) findViewById(R.id.btn_iap_2);
		btnIap2.setTypeface(tf);
		btnIap2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// buy 300 coins
				buyCoins(getResources().getString(R.string.sku_inapp_300));
			}
		});
		
		disableSkuInterfaces();
		/*
		btnIapRate=(Button) findViewById(R.id.btn_iap_rate);
		btnIapRate.setTypeface(tf);
		if(getIsRated()){
			//disable rating button
			btnIapRate.setEnabled(false);
			btnIapRate.setBackgroundResource(R.drawable.button_shape_disabled);
		}
		
		btnIapRate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//on rating request first check if network is available
				if(isNetworkAvailable()){
					if(!getIsRated()){
						//starts rating dialog immediate (0 to min days, 0 to min launches)
						new AppRate(getContext())
							.setMinDaysUntilPrompt(0)
							.setMinLaunchesUntilPrompt(0)
							.init();
						
					}
				}else{
					Toast.makeText(getContext(), R.string.msg_connection_not_available, Toast.LENGTH_LONG).show();
				}
			}

		});
	*/
		btnIapCancel=(Button) findViewById(R.id.btn_iap_cancel);
		btnIapCancel.setTypeface(tf);
		btnIapCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(INAPP_RESULT_FINISH);
//				Intent intent;
//				intent = new Intent(getContext(), MainActivity.class);
//				startActivity(intent);
				finish();
			}
		});
		/*
		//display coins status
		btnIapUpdate=(Button) findViewById(R.id.btn_iap_update);
		btnIapUpdate.setTypeface(tf);
		btnIapUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//start loading items
				HashSet<String> skus=new HashSet<String>();
				skus.add(getResources().getString(R.string.sku_inapp_100));
				skus.add(getResources().getString(R.string.sku_inapp_300));
				 String requestId = PurchasingManager.initiateItemDataRequest(skus);
				
			}
		});
		*/
		txtIap1=(TextView) findViewById(R.id.txt_iap_1);
		txtIap1.setText(R.string.txt_inapp_100);
		txtIap2=(TextView) findViewById(R.id.txt_iap_2);
		txtIap2.setText(R.string.txt_inapp_300);
		txtStatusIap=(TextView) findViewById(R.id.txt_status_iap);
		txtStatusIap.setText(getResources().getString(R.string.txt_status_iap_1)+" "+getMoney()+" "+getResources().getString(R.string.txt_status_iap_2));
		
		
		if(getResources().getString(R.string.useIAP).equalsIgnoreCase("true")){
		 setWaitScreen(true);
		 // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
             // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG,"Problem setting up in-app billing: " + result);
                    return;
                }
                List<String> additionalSkuList = new ArrayList<String>();
                additionalSkuList.add(getResources().getString(R.string.sku_inapp_100));
                additionalSkuList.add(getResources().getString(R.string.sku_inapp_300));
                mHelper.queryInventoryAsync(true, additionalSkuList,
                   mQueryFinishedListener);
                
                
            }
        });
		}else{
			//hide first two rows for IAP
			LinearLayout ll1=(LinearLayout) findViewById(R.id.layout_iap_1);
			
			LinearLayout.LayoutParams lp1=new LinearLayout.LayoutParams(0, 0);
			ll1.setLayoutParams(lp1);
			
			LinearLayout ll2=(LinearLayout) findViewById(R.id.layout_iap_2);
			
			LinearLayout.LayoutParams lp2=new LinearLayout.LayoutParams(0, 0);
			ll2.setLayoutParams(lp2);
		}
		
		
		if(getResources().getString(R.string.useFacebook).equalsIgnoreCase("false")){
			LinearLayout ll4=(LinearLayout) findViewById(R.id.layout_iap_4);
			
			LinearLayout.LayoutParams lp4=new LinearLayout.LayoutParams(0, 0);
			ll4.setLayoutParams(lp4);
		}
	}
	
	 protected void buyCoins(String sku) {
		 setWaitScreen(true);
		 String payload ="";// UUID.randomUUID().toString(); //generate string for crt user
		 SharedPreferences.Editor editor=settings.edit();
		 //TODO getUser
		 try{
		 mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, 
	                mPurchaseFinishedListener, payload);
		 }catch(IllegalStateException ise){
			 //just wait to finish other tasks
			 Toast.makeText(this, R.string.wait_message, Toast.LENGTH_SHORT).show();
			 setWaitScreen(false);
Log.e(TAG," "+ ise.getMessage());
		 }
		 editor.putString(sku, payload);
		 editor.commit();
	}

    /**
     * When the application resumes the application checks which customer is signed in.
     */
    @Override
    protected void onResume() {
        super.onResume();
        txtStatusIap.setText(getResources().getString(R.string.txt_status_iap_1)+" "+getMoney()+" "+getResources().getString(R.string.txt_status_iap_2));
        setWaitScreen(false);
    };
    
    @Override
	public void onBackPressed() {
    	setResult(INAPP_RESULT_FINISH);
    	
//    		Intent intent;
//    		intent = new Intent(getContext(), MainActivity.class);
//    		startActivity(intent);
    		
		finish();
		
	}

	protected InAppActivity getContext(){
    	return this;
    }
	
	
	/**
	 * Updates available coins
	 * @param value
	 * @return coins
	 */
	public int modifyMoney(int value) {
		
		int money = settings.getInt(MONEY, 0);
		money = money + value;
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(MONEY, money);
		editor.commit();
		return money;
	}
	/**
	 * Remember if user has made any purchases
	 * @param value
	 * @return coins
	 */
	public void setItemPurchased() {
		
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ITEM_PURCHASED, true);
		editor.commit();
		return;
	}
	/**
	 * Money available
	 * @return coins
	 */
	public int getMoney() {
		
		int money = settings.getInt(MONEY, 0);
		return money;
	}
	 // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
        	try{
        		mHelper.dispose();
        	}catch(Exception e){
        		Log.e(TAG, " "+e.getMessage());
        	}
        	  mHelper = null;
        }
      
    }

	

		
        
   
    
	/**
	 * Page has been rated, save this status in order to avoid showing the dialog again, update coins amount and disable button
	 */
	public void setIsRated() {
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(APP_IS_RATED, true);
		editor.commit();
		modifyMoney(MONEY_RATE);
		txtStatusIap.setText(getResources().getString(R.string.txt_status_iap_1)+" "+getMoney()+" "+getResources().getString(R.string.txt_status_iap_2));
		//btnIapRate.setEnabled(false);
		//btnIapRate.setBackgroundResource(R.drawable.button_shape_disabled);
	}
	
	/**
	 * Check if Rate button has been clicked already
	 * @return
	 */
	protected boolean getIsRated() {
		return settings.getBoolean(APP_IS_RATED, false);
		
	}
	
	/**
	 * Checks if network is available
	 * @return boolean
	 */
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	 // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        	if (mHelper == null) return;
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
            	 Log.d(TAG,"Purchase failed");
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.d(TAG,"Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            Log.d(TAG, "purchase sku "+purchase.getSku());
            //if sku
            if (purchase.getSku().equals(getResources().getString(R.string.sku_inapp_100))||purchase.getSku().equals(getResources().getString(R.string.sku_inapp_300))) {
                //consume it 
                Log.d(TAG, "Starting  consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            
        }

	
    };
	
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            if (mHelper == null) return;
            //check sku
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                String sku=purchase.getSku();
               //increment coins
                int coins;
                if(sku.equalsIgnoreCase(getResources().getString(R.string.sku_inapp_100))){
        			setItemPurchased();
        			coins = modifyMoney(COINS_INAPP_ITEM_1);
        			txtStatusIap.setText(getResources().getString(R.string.txt_status_iap_1)+" "+coins+" "+getResources().getString(R.string.txt_status_iap_2));
        		}else if(sku.equalsIgnoreCase(getResources().getString(R.string.sku_inapp_300))){
        			setItemPurchased();
        			coins = modifyMoney(COINS_INAPP_ITEM_2);
        			txtStatusIap.setText(getResources().getString(R.string.txt_status_iap_1)+" "+coins+" "+getResources().getString(R.string.txt_status_iap_2));
        		}
            }
            else {
               Log.e(TAG,"Error while consuming: " + result);
            }
            
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }

		
    };

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (mHelper == null) return;
            if (result.isFailure()) {
                Log.d(TAG,"Failed to query inventory: " + result);
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");
            Log.d(TAG, "Check."+ getResources().getString(R.string.sku_inapp_100)+" "+inventory.hasPurchase(getResources().getString(R.string.sku_inapp_100)));
            Log.d(TAG, "Check."+ getResources().getString(R.string.sku_inapp_300)+" "+inventory.hasPurchase(getResources().getString(R.string.sku_inapp_300)));
            
           
            
            // Check for coins bought and process them immediately
            Purchase coins100Purchase = inventory.getPurchase(getResources().getString(R.string.sku_inapp_100));
            if (coins100Purchase != null && verifyDeveloperPayload(coins100Purchase)) {
                Log.d(TAG, "We have 100 coins purchase. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(getResources().getString(R.string.sku_inapp_100)), mConsumeFinishedListener);
                return;
            }
         // Check for coins bought and process them immediately
            Purchase coins300Purchase = inventory.getPurchase(getResources().getString(R.string.sku_inapp_300));
            if (coins100Purchase != null && verifyDeveloperPayload(coins300Purchase)) {
                Log.d(TAG, "We have 300 coins purchase. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(getResources().getString(R.string.sku_inapp_300)), mConsumeFinishedListener);
                return;
            }

            
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    
    // Listener that's called when we finish querying for items details
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        	
            Log.d(TAG, "Query sku items finished.");
            if (mHelper == null) return;
            if (result.isFailure()) {
                Log.d(TAG,"Failed to query inventory: " + result);
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Query sku items was successful.");
            
            SkuDetails coins100 =
                    inventory.getSkuDetails(getResources().getString(R.string.sku_inapp_100));
           
            if(coins100!=null) {
            	 Log.d(TAG, "sku available coins100 " +coins100.getDescription()+" "+coins100.toString());
            	enableSkuInterface(coins100);
            }
            SkuDetails coins300 =
                    inventory.getSkuDetails(getResources().getString(R.string.sku_inapp_300));
           
            if(coins300!=null) {
            	 Log.d(TAG, "sku available coins300 " +coins300.getDescription()+" "+coins300.toString());
            	enableSkuInterface(coins300);
            }
           
          // setWaitScreen(false);
            Log.d(TAG, "Initial sku items query finished; enabling main UI.");
         // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Querying inventory.");
   		    
            mHelper.queryInventoryAsync(mGotInventoryListener);
        }
    };

	/**
	 * Enable available purchases
	 * @param item
	 */
	protected void enableSkuInterface(SkuDetails item){

		if(item.getSku().equalsIgnoreCase(getResources().getString(R.string.sku_inapp_100))){
			//enable text view and button for first sku
			btnIap1.setEnabled(true);
			btnIap1.setBackgroundResource(R.drawable.selector_button);
			
			if(item!=null&&item.getPrice()!=null&&item.getPrice().length()>0){
				txtIap1.setText(getResources().getString(R.string.txt_inapp_100)+" "+item.getPrice());
			}else{
				btnIap1.setEnabled(false);
			}
		}else if(item.getSku().equalsIgnoreCase(getResources().getString(R.string.sku_inapp_300))){
			//enable text view and button for second sku
			btnIap2.setEnabled(true);
			btnIap2.setBackgroundResource(R.drawable.selector_button);
			if(item!=null&&item.getPrice()!=null&&item.getPrice().length()>0){
				txtIap2.setText(getResources().getString(R.string.txt_inapp_300)+" "+item.getPrice());
			}else{
				btnIap2.setEnabled(false);
			}
			
		}
	}
	
	/**
	 * 
	 * @param item
	 */
	protected void disableSkuInterfaces(){

		btnIap1.setEnabled(false);
		btnIap1.setBackgroundResource(R.drawable.button_shape_disabled);
		
		
	
		btnIap2.setEnabled(false);
		btnIap2.setBackgroundResource(R.drawable.button_shape_disabled);
	}	
   
	private boolean verifyDeveloperPayload(Purchase purchase) {
		String payload = settings.getString(purchase.getSku(), "");
		/*
		if(payload.equalsIgnoreCase(purchase.getDeveloperPayload())){
			return true;
		}else{
			return false;
		}
		*/
		return true;
	
	}

	private void setWaitScreen(boolean set) {
		findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
	}
	
	/**
     * Whenever the application regains focus, the observer is registered again.
     */
    @Override
    public void onStart() {
        super.onStart();
        setWaitScreen(false);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

}

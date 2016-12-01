package com.tedrasoft.templategoogle;

import com.tedrasoft.templategoogle.data.InitialSizeData;

import android.util.DisplayMetrics;
import android.util.Log;

/**
 * 
 * @author Dragos
 * 
 *         Takes care of resizing views and buttons for scene activity layout
 *         when necessary. When loaded we must set dimensions for word buttons
 *         (at most 8 per row - 7 letters for word + 1 for Facebook ) and
 *         character buttons (at most 7 per row - six for characters +1 for
 *         options button). Tries to fill all available space.
 * 
 * 
 */
public class ObjectsResizeManager {
	// actual screen dimensions
	private int screenWidthPx;
	private int screenHeightPx;
	// screen dpi
	private int dpi;
	// action bar size - mainly depends on icon for level displaying which is the biggest in action bar
	private int actionbarSize;

	// in pixels
	private int boxSize;
	// in dp
	private int boxInitialSize;
	//text box above picture
	private int textBoxSize=40;
	private int maximumBox=MAXIMUM_BOX_DIMENSION;

	//constants for actions to resize word buttons or pool buttons 27 combination
	private static final int ENLARGE_WORD = 1;//+_ _
	private static final int ENLARGE_POOL = 2;//_ + _
	private static final int ENLARGE_IMAGE = 4;//_ _ +
	private static final int ENLARGE_BOTH = 3;//+ + _
	
	private static final int ENLARGE_BOTH_BUTTONS_ENLARGE_IMAGE = 7;//+ + +
	private static final int ENLARGE_WORD_ENLARGE_IMAGE = 5;// + _ +
	private static final int ENLARGE_POOL_ENLARGE_IMAGE = 6;// _ + +
	private static final int ENLARGE_BOTH_BUTTONS_REDUCE_IMAGE =43;// _ + +
	private static final int ENLARGE_WORD_REDUCE_IMAGE = 41;// + _ +
	private static final int ENLARGE_POOL_REDUCE_IMAGE = 42;// _ + +
	
	private static final int REDUCE_WORD = 10;
	private static final int REDUCE_POOL = 20;
	private static final int REDUCE_BOTH = 30;
	private static final int REDUCE_IMAGE = 40;
	
	private static final int REDUCE_BOTH_BUTTONS_REDUCE_IMAGE = 70;
	private static final int REDUCE_BOTH_BUTTONS_ENLARGE_IMAGE = 34;
	private static final int REDUCE_WORD_REDUCE_IMAGE = 50;
	private static final int REDUCE_POOL_REDUCE_IMAGE = 60;
	private static final int REDUCE_WORD_ENLARGE_IMAGE = 14;
	private static final int REDUCE_POOL_ENLARGE_IMAGE = 24;
	private static final int ENLARGE_WORD_REDUCE_POOL= 21;
	private static final int REDUCE_WORD_ENLARGE_POOL= 12;
	private static final int ENLARGE_WORD_REDUCE_POOL_REDUCE_IMAGE = 61;
	private static final int REDUCE_WORD_ENLARGE_POOL_REDUCE_IMAGE = 52;
	private static final int ENLARGE_WORD_REDUCE_POOL_ENLARGE_IMAGE = 25;
	private static final int REDUCE_WORD_ENLARGE_POOL_ENLARGE_IMAGE = 16;
	private static final int ALL_OK = 0;
	
	//maximum buttons per word and pool rows
	private static final int MAXIMUM_WORD = AppConstants.WORD_MAX_SIZE;
	//public   int MAXIMUM_WORD = 3;
	private static final int MAXIMUM_POOL = AppConstants.ROW_POOL_SIZE;
	//Preferred free space percentage of width
	private static final float PREFERRED_FREE_PERCENTAGE = 0.05f;
	//minimum buffer for buttons row
	private static final int MINIMUM_BUFFER = 6;
	//Preferred free space percentage of width
	private static final float PREFERRED_FREE_HEIGHT_PERCENTAGE = 0.08f;
	//minimum buffer for buttons row
	private static final int MINIMUM_HEIGHT_BUFFER = 10;
	//maximum box dimension in pixels
	private static final int MAXIMUM_BOX_DIMENSION = 400;
	//maximum box dimension in pixels
	private static final int MAXIMUM_BOX_DIMENSION_BIG = 800;

	private static String TAG = "ObjectsResizeManager";
	//minimum buffer for buttons row
	private static final int BOX_STEP = 10;
	
	
	public ObjectsResizeManager(int screenWidthPx, int screenHeightPx, int dpi) {
		super();
		this.screenWidthPx = screenWidthPx;
		this.screenHeightPx = screenHeightPx;
		this.dpi = dpi;
		Log.d(TAG, "Screen height: "+screenHeightPx);
		Log.d(TAG, "Screen width: "+screenWidthPx);
		Log.d(TAG, "dpi: "+dpi);
		if(dpi>=320&&screenHeightPx>1200&&screenWidthPx>700){
			maximumBox=MAXIMUM_BOX_DIMENSION_BIG;
			Log.d(TAG, "Maximum box dimension");
		}
	}

	@Override
	public String toString() {
		
		return "ObjectResizerManager : screenWidth:" + screenWidthPx
				+ " screenHeight:" + screenHeightPx + " dpi:" + dpi
				+ " actionBarSize:" + actionbarSize;
	}

	public int getBoxSize() {
		return boxSize;
	}

	public void setBoxSize(int boxSize) {
		this.boxSize = boxSize;
	}

	public int getBoxInitialSize() {
		return boxInitialSize;
	}

	public void setBoxInitialSize(int boxInitialSize) {
		this.boxInitialSize = boxInitialSize;
	}

	public int getActionbarSize() {
		return actionbarSize;
	}

	public void setActionbarSize(int actionbarSize) {
		this.actionbarSize = actionbarSize;
	}

	public int getScreenWidthPx() {
		return screenWidthPx;
	}

	public void setScreenWidthPx(int screenWidthPx) {
		this.screenWidthPx = screenWidthPx;
	}

	public int getScreenHeightPx() {
		return screenHeightPx;
	}

	public void setScreenHeightPx(int screenHeightPx) {
		this.screenHeightPx = screenHeightPx;
	}

	public int getDpi() {
		return dpi;
	}

	public void setDpi(int dpi) {
		this.dpi = dpi; 
	}
	/**
	 * How to scale sizes depending on dpi
	 * @return scale ratio
	 */
	public float getScaleRatio() {
		return (float) dpi / DisplayMetrics.DENSITY_DEFAULT;
	}

	/**
	 * Calculates size of the box in order to fill most of the screen: width
	 * uses a 15 pixels buffer
	 * 
	 * @param isd
	 * 
	 */
	public void calculateNewSizes(InitialSizeData isd) {
		// based on isd verify with ObjectResizeManager if something must be
		// modified
		// check if there is any available space on the width
		// and redistribute to buttons
		int result = ALL_OK;
		while ((result = checkConditions(isd)) != ALL_OK) {
			modifyButtons(isd, result);
			modifyImageSize(isd, result);
		}

	}
	/**
	 * Modify calculated image size
	 * Uses 10 dpi step
	 * @param isd
	 */
	private void modifyImageSize(InitialSizeData isd, int type) {
		// TODO Auto-generated method stub
		if (type == REDUCE_BOTH_BUTTONS_REDUCE_IMAGE || type == REDUCE_POOL_REDUCE_IMAGE||type==REDUCE_WORD_REDUCE_IMAGE||type==REDUCE_WORD_ENLARGE_POOL_REDUCE_IMAGE||type==ENLARGE_BOTH_BUTTONS_REDUCE_IMAGE
				|| type == ENLARGE_WORD_REDUCE_POOL_REDUCE_IMAGE||type==ENLARGE_POOL_REDUCE_IMAGE||type==REDUCE_IMAGE||type==ENLARGE_WORD_REDUCE_IMAGE) {
			setBoxSize(boxSize-10);
		}else if (type == REDUCE_BOTH_BUTTONS_ENLARGE_IMAGE || type == REDUCE_POOL_ENLARGE_IMAGE||type==REDUCE_WORD_ENLARGE_IMAGE||type==REDUCE_WORD_ENLARGE_POOL_ENLARGE_IMAGE||type==ENLARGE_BOTH_BUTTONS_ENLARGE_IMAGE
				|| type == ENLARGE_WORD_REDUCE_POOL_ENLARGE_IMAGE||type==ENLARGE_POOL_ENLARGE_IMAGE||type==ENLARGE_IMAGE||type==ENLARGE_WORD_ENLARGE_IMAGE) {
			setBoxSize(boxSize+10);
		}
	}

	/**
	 * Modify calculated button size
	 * Uses 2 dpi step
	 * @param isd
	 */
	private void modifyButtons(InitialSizeData isd, int type) {

		if (type == REDUCE_BOTH ||type == REDUCE_BOTH_BUTTONS_ENLARGE_IMAGE||type == REDUCE_BOTH_BUTTONS_REDUCE_IMAGE|| type == REDUCE_POOL|| type == REDUCE_POOL_ENLARGE_IMAGE|| type == REDUCE_POOL_REDUCE_IMAGE
				|| type == ENLARGE_WORD_REDUCE_POOL|| type == ENLARGE_WORD_REDUCE_POOL_ENLARGE_IMAGE|| type == ENLARGE_WORD_REDUCE_POOL_REDUCE_IMAGE) {
			isd.setPoolButtonSize(isd.getPoolButtonSize() - 1);
			isd.setPoolTextSize(isd.getPoolTextSize() - 1);
		}
		if (type == REDUCE_BOTH ||type == REDUCE_BOTH_BUTTONS_ENLARGE_IMAGE||type == REDUCE_BOTH_BUTTONS_REDUCE_IMAGE|| type == REDUCE_WORD_ENLARGE_IMAGE || type == REDUCE_WORD || type == REDUCE_WORD_REDUCE_IMAGE
				|| type == REDUCE_WORD_ENLARGE_POOL || type == REDUCE_WORD_ENLARGE_POOL_ENLARGE_IMAGE || type == REDUCE_WORD_ENLARGE_POOL_REDUCE_IMAGE) {
			isd.setWordButtonSize(isd.getWordButtonSize() - 1);
			isd.setWordTextSize(isd.getWordTextSize() - 1);
		}
		if (type == ENLARGE_BOTH || type == ENLARGE_BOTH_BUTTONS_ENLARGE_IMAGE || type == ENLARGE_BOTH_BUTTONS_REDUCE_IMAGE || type == ENLARGE_POOL || type == ENLARGE_POOL_ENLARGE_IMAGE || type == ENLARGE_POOL_REDUCE_IMAGE
				|| type == REDUCE_WORD_ENLARGE_POOL || type == REDUCE_WORD_ENLARGE_POOL_ENLARGE_IMAGE || type == REDUCE_WORD_ENLARGE_POOL_REDUCE_IMAGE) {
			isd.setPoolButtonSize(isd.getPoolButtonSize() + 1);
			isd.setPoolTextSize(isd.getPoolTextSize() + 1);
		}
		if (type == ENLARGE_BOTH || type == ENLARGE_BOTH_BUTTONS_ENLARGE_IMAGE || type == ENLARGE_BOTH_BUTTONS_REDUCE_IMAGE|| type == ENLARGE_WORD || type == ENLARGE_WORD_ENLARGE_IMAGE || type == ENLARGE_WORD_REDUCE_IMAGE
				|| type == ENLARGE_WORD_REDUCE_POOL || type == ENLARGE_WORD_REDUCE_POOL_ENLARGE_IMAGE || type == ENLARGE_WORD_REDUCE_POOL_REDUCE_IMAGE) {
			isd.setWordButtonSize(isd.getWordButtonSize() + 1);
			isd.setWordTextSize(isd.getWordTextSize() + 1);
		}

	}

	/**
	 * difference<PREFERRED_FREE_PERCENTAGE or widthSum or 2*image size > screenWidth - 15 
	 * 
	 * @param isd
	 * @return
	 */
	private int checkConditions(InitialSizeData isd) {
		Log.d(TAG, "checkConditions");
		int widthSum = 0;
		int widthSumWord = 0;
		//total width of pool buttons row
		widthSum += (int) (MAXIMUM_POOL * isd.getPoolButtonSize()
				* getScaleRatio() + 2 * MAXIMUM_POOL * isd.getExtraspace()
				* getScaleRatio());
		//total space of word buttons row
		widthSumWord += (int) (MAXIMUM_WORD * isd.getWordButtonSize()
				* getScaleRatio() + 2 * MAXIMUM_WORD * isd.getExtraspaceWord()
				* getScaleRatio());

		Log.d(TAG, "widthSum=" + widthSum + " widthSumWord=" + widthSumWord);
 
		int resultWord = ALL_OK;
		int resultPool = ALL_OK;

		// use a  minimum buffer
		// desired width buffer is 5%

		if (widthSum + MINIMUM_BUFFER <= getScreenWidthPx()) {
			// check if difference between actual button row width and screen width is
			// bigger than preferred difference
			int difference = getScreenWidthPx() - widthSum;
			if (difference > PREFERRED_FREE_PERCENTAGE * getScreenWidthPx()) {
				resultPool = ENLARGE_POOL;
			} else {
				// if yes
				Log.d(TAG, "all ok with pool buttons");
				// all ok
			}
		} else if (widthSum > getScreenWidthPx()) {
			Log.d(TAG, "reduce pool buttons");
			resultPool = REDUCE_POOL;
		}

		// use a minimum buffer
		// desired width buffer is 5%

		if (widthSumWord + MINIMUM_BUFFER <= getScreenWidthPx()) {
			// check if difference between actual width and screen width is
			// bigger than  preferred difference
			int difference = getScreenWidthPx() - widthSumWord;
			if (difference > PREFERRED_FREE_PERCENTAGE * getScreenWidthPx()) {
				resultWord = ENLARGE_WORD;
				Log.d(TAG, "Enlarge word"); 
			} else {
				// if yes
				Log.d(TAG, "all ok with word buttons");
				// all ok
			}
		} else if (widthSumWord > getScreenWidthPx()) {
			Log.d(TAG, "reduce word buttons");
			resultWord = REDUCE_WORD;
		}
		
		//verify height - maximum allowed 400 pixels
		int resultImage=ALL_OK;
		int heightSum = calculateHeight(isd);
		int difference;
		difference=getScreenHeightPx() - heightSum;
		if((difference>PREFERRED_FREE_HEIGHT_PERCENTAGE * getScreenHeightPx()) && ((getBoxSize()+BOX_STEP)<getScreenWidthPx()) && (getBoxSize()+MINIMUM_HEIGHT_BUFFER)<=maximumBox){
			//we can enlarge image
			Log.d(TAG,"Enlarge image");
			resultImage=ENLARGE_IMAGE;
			
		}else if((heightSum+MINIMUM_HEIGHT_BUFFER> getScreenHeightPx()) || (getBoxSize()+BOX_STEP)>getScreenWidthPx()) {
			//we can reduce image
			Log.d(TAG,"Reduce image");
			resultImage=REDUCE_IMAGE;
		} else {
			//al ok
		}
		Log.d(TAG, "checkConditions res "+(resultPool + resultWord + resultImage));
		return resultPool + resultWord + resultImage;
	}
	
	/**
	 * Calculates action bar size depending on dpi as it uses different icons for level background
	 */
	public void calculateActionBarHeight() {
		if (dpi <= 120) {
			setActionbarSize(36);
		} else if (dpi > 120 && dpi <= 160) {
			setActionbarSize(48);
		} else if (dpi > 160 && dpi <= 260) {
			setActionbarSize(64);
		} else if (dpi > 260) {
			setActionbarSize(72);
		}
	}
	
	public int calculateHeight(InitialSizeData isd){
		Log.d(TAG,"Calculate  height");
		
		int height=0;
		//add action bar
		height+=getActionbarSize();
		//add buttons
		height+=(int) ( isd.getWordButtonSize()
				* getScaleRatio() + 2  * isd.getExtraspaceWord()* getScaleRatio());
		height+=(int)(2* isd.getPoolButtonSize() * getScaleRatio()+4*isd.getExtraspace()* getScaleRatio());
		//add image box height
		Log.d(TAG," box height "+boxSize);
		height+=(getBoxSize());
		//margins
		height+=20*getScaleRatio();
		height+=textBoxSize*getScaleRatio();
		Log.d(TAG,"Calculated  height="+height); 
		return height;
	}
}
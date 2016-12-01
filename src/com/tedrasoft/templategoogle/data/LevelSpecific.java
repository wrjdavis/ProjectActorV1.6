package com.tedrasoft.templategoogle.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.tedrasoft.templategoogle.AppConstants;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class LevelSpecific {
	@JsonProperty("image")
	String image;
	@JsonProperty("path")
	String path;
	@JsonProperty("word")
	String word;
	@JsonProperty("pool")
	String pool;
    @JsonProperty("imageText")
	String textPicture;
	@JsonProperty("hints")
	ArrayList<String> hints;
	@JsonProperty("depends")
	int dependsOn;

	private static final String TAG = "LevelSpecific";
	String[] processedWord;
	String[] charPool;

	
	
	public int getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(int dependsOn) {
		this.dependsOn = dependsOn;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<String> getHints() {
		return hints;
	}

	public void setHints(ArrayList<String> hints) {
		this.hints = hints;
	}

	public String getTextPicture() {
		return textPicture;
	}

	public void setTextPicture(String textPicture) {
		this.textPicture = textPicture;
	}

	public static String[] toSingleCharacterStringArray(String s) {
		if (s == null) {
			return null;
		}

		String[] array = new String[s.length()];
		for (int i = 0; i < s.length(); i++) {
			array[i] = s.substring(i, i + 1);
		}
		return array;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public String[] getProcessedWord() {
		return toSingleCharacterStringArray(word);
	}

	public void setProcessedWord(String[] processedWord) {
		this.processedWord = processedWord;
	}

	public String[] getCharPool(String alphabet) {
		if (pool.trim().length() > 0)
			return toSingleCharacterStringArray(pool);
		else {
			pool = randomize(alphabet,2*( AppConstants.ROW_POOL_SIZE-1));
			return toSingleCharacterStringArray(pool);
		}
	}
	/**
	 * Get a String containing word letters and random letters from alphabet
	 * @param alphabet
	 * @param size
	 * @return String
	 */
	private String randomize(String alphabet, int size) {
		StringBuffer sb = new StringBuffer();
		if(isWordNumerical()){
			//randomize using digits
			// get random positions for word strings
			int[] positions = new int[word.length()];
			for (int i = 0; i < word.length(); i++) {
				positions[i] = -1;
			}
			Random r = new Random((new Date().getTime()));
			for (int i = 0; i < word.length(); i++) {
				int crtPosition = -1;
				while (crtPosition < 0 || contains(positions, crtPosition)) {
					crtPosition = r.nextInt(size);

				}
				Log.d(TAG, "" + i + " " + crtPosition);
				positions[i] = crtPosition;

			}
			for (int i = 0; i < size; i++) {
				if (contains(positions, i)) {
					sb.append(word.substring(getWordPosition(positions, i),
							getWordPosition(positions, i) + 1));
					Log.d(TAG,
							"position in pool "
									+ i
									+ " = "
									+ word.substring(getWordPosition(positions, i),
											getWordPosition(positions, i) + 1));
				} else {
					int randomNumerical = r.nextInt(10);
					Log.d(TAG,
							"numerical in pool position "
									+ i
									+ " = "
									+ randomNumerical);
					sb.append(String.valueOf(randomNumerical));
				}
			}
		}else{
		
		
		// get random positions for word strings
		int[] positions = new int[word.length()];
		for (int i = 0; i < word.length(); i++) {
			positions[i] = -1;
		}

		Random r = new Random((new Date().getTime()));
		for (int i = 0; i < word.length(); i++) {
			int crtPosition = -1;
			while (crtPosition < 0 || contains(positions, crtPosition)) {
				crtPosition = r.nextInt(size);

			}
			Log.d(TAG, "" + i + " " + crtPosition);
			positions[i] = crtPosition;

		}

		for (int i = 0; i < size; i++) {
			if (contains(positions, i)) {
				sb.append(word.substring(getWordPosition(positions, i),
						getWordPosition(positions, i) + 1));
				Log.d(TAG,
						"position in pool "
								+ i
								+ " = "
								+ word.substring(getWordPosition(positions, i),
										getWordPosition(positions, i) + 1));
			} else {
				int randomStringLetterPosition = r.nextInt(alphabet.length());
				Log.d(TAG,
						"position in pool "
								+ i
								+ " = "
								+ alphabet.substring(
										randomStringLetterPosition,
										randomStringLetterPosition + 1));
				sb.append(alphabet.substring(randomStringLetterPosition,
						randomStringLetterPosition + 1));
			}
		}
		}
		return sb.toString();
	}
	/**
	 * Returns true if crtPosition is found in array
	 * @param positions
	 * @param crtPosition
	 * @return boolean
	 */
	private boolean contains(int[] positions, int crtPosition) {
		for (int i = 0; i < positions.length; i++) {
			if (positions[i] == crtPosition) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns position in word if > 0 or -1 if crtPosition is not found in array
	 * @param positions
	 * @param crtPosition
	 * @return position
	 */
	private int getWordPosition(int[] positions, int crtPosition) {
		for (int i = 0; i < positions.length; i++) {
			if (positions[i] == crtPosition) {
				return i;
			}
		}
		return -1;
	}

	public void setCharPool(String[] charPool) {
		this.charPool = charPool;
	}
	
	public boolean isWordNumerical(){
	//	String clone=word.replaceAll("[^A-Za-z0-9]", "");
		String clone=word.replaceAll("[^\\p{L}\\p{Nd}]", "");
		Log.d(TAG, "clone"+clone);
		if(clone.length()>0){
			Log.d(TAG, "without numerics "+clone.replaceAll("[0-9]", "")+" length "+(clone.replaceAll("[0-9]", "")).length());
			return (clone.replaceAll("[0-9]", "")).length()==0;
		}else return false;
	}
}

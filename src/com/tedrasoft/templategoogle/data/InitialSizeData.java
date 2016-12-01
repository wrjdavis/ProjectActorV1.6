package com.tedrasoft.templategoogle.data;



import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
/**
 * Initial preferred sizes depending on screen dimensions
 * @author Dragos
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.NONE,getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
public class InitialSizeData {

	@JsonProperty("size")
	private String size;
	@JsonProperty("word_text_size")
	private int wordTextSize; //sp
	@JsonProperty("pool_text_size")
	private int poolTextSize; //sp
	@JsonProperty("word_button_size")
	private int wordButtonSize; //dp
	@JsonProperty("pool_button_size")
	private int poolButtonSize; //dp
	@JsonProperty("extraspace")
	private int extraspace; //px
	@JsonProperty("extraspaceWord")
	private int extraspaceWord; //px
	public InitialSizeData(){
		
	}
	
	public InitialSizeData(InitialSizeData isd) {
		this.extraspace=isd.extraspace;
		this.extraspaceWord=isd.extraspaceWord;
		this.size=isd.size;
		this.poolButtonSize=isd.poolButtonSize;
		this.poolTextSize=isd.poolTextSize;
		this.wordButtonSize=isd.wordButtonSize;
		this.wordTextSize=isd.wordTextSize;
	}
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getWordTextSize() {
		return wordTextSize;
	}
	public void setWordTextSize(int wordTextSize) {
		this.wordTextSize = wordTextSize;
	}
	public int getPoolTextSize() {
		return poolTextSize;
	}
	public void setPoolTextSize(int poolTextSize) {
		this.poolTextSize = poolTextSize;
	}
	public int getWordButtonSize() {
		return wordButtonSize;
	}
	public void setWordButtonSize(int wordButtonSize) {
		this.wordButtonSize = wordButtonSize;
	}
	public int getPoolButtonSize() {
		return poolButtonSize;
	}
	public void setPoolButtonSize(int poolButtonSize) {
		this.poolButtonSize = poolButtonSize;
	}
	public int getExtraspace() {
		return extraspace;
	}
	public void setExtraspace(int extraspace) {
		this.extraspace = extraspace;
	}

	public int getExtraspaceWord() {
		return extraspaceWord;
	}

	public void setExtraspaceWord(int extraspaceWord) {
		this.extraspaceWord = extraspaceWord;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "InitialSizeData size:"+size+" wordTextSize:"+wordTextSize+" poolTextSize"+poolTextSize+" wordButtonSize"+wordButtonSize+" poolButtonSize"+poolButtonSize+" extraspace "+extraspace+" extraspaceWord "+extraspaceWord;
	}
	
	
	
}
	
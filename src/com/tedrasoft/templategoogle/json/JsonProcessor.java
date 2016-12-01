package com.tedrasoft.templategoogle.json;

import java.io.IOException;


import android.content.Context;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tedrasoft.templategoogle.data.ConfigData;
import com.tedrasoft.templategoogle.data.InitialSizeData;

/**
 * Helper class fro Json processing
 * @author Dragos
 *
 */
public class JsonProcessor {
	/**
	 * read configuration from json file
	 * @param context
	 * @return ConfigData
	 */
	public static ConfigData readConfig(Context context, String levels){
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			ConfigData config = mapper.readValue(LoaderHelper.parseFileToString(context, levels+".json"), ConfigData.class);
			return config;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * read initial sizes preferences
	 * 
	 * @param context
	 * @param size
	 * @return InitialSizeData
	 */
	public static InitialSizeData readSize(Context context,String size){
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			InitialSizeData config = mapper.readValue(LoaderHelper.parseFileToString(context, size+".json"), InitialSizeData.class);
			return config;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

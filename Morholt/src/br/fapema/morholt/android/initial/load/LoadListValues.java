package br.fapema.morholt.android.initial.load;

import java.util.Map;

import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

/**
 * it can be loaded from loader interface
 * @author pedro
 *
 */
public class LoadListValues  extends Values{
	
	public LoadListValues() {
		
	}
	public LoadListValues(Values values) {
		mValues = values.getMValues();
	}
	
	public String getColumnThree() {
    	return getAsString(LAST_UPDATE_ON_SYSTEM_COLUMN);
	}

	public boolean getColumnPhotoSentToServer() {
    	return mValues.get(br.fapema.morholt.android.basic.Creator.PHOTO_SENT_TO_SERVER).equals("1") ? true : false; 
	}

	public String getColumnPhotoSentToServerHeader() {
		return "foto"; 
	}
	
	public String getColumnDelete() {
		return "apagar"; 
	}
	
	public boolean getColumnSentToServer() {
    	return mValues.get(br.fapema.morholt.android.basic.Creator.SENT_TO_SERVER).equals("1") ? true : false; 
	}

	public void setColumnPhotoSentToServer(boolean sentToServer) {
		String sent = sentToServer ? "1" : "0"; 
		mValues.put(br.fapema.morholt.android.basic.Creator.PHOTO_SENT_TO_SERVER, sent);
	}

	
	public void setColumnSentToServer(boolean sentToServer) {
		String sent = sentToServer ? "1" : "0"; 
		mValues.put(br.fapema.morholt.android.basic.Creator.SENT_TO_SERVER, sent);
	}
	
	public String getColumnFourHeader() {
		return "enviado"; 
	}
    
	public String getColumnThreeHeader() {
		return "atualizado em"; 
	}
	
	public String getColumnTwo() {
		return getAsText("np");
	}
	
	public String getColumnTwoHeader() {
		return "NP";
	}

	public String getColumnOne() {
		return String.valueOf(get(ID)); 
	}

	public String getColumnOneHeader() {
		return "id"; 
	}
}

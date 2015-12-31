package br.fapema.morholt.android.wizardpager.wizard.model;

import java.util.ArrayList;

import android.text.TextUtils;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.ui.GPSFragment;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class GPSPage extends Page {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6022108917671809903L;

    private String columnXOnDB;
    private String columnYOnDB;
    

    private String columnLatOnDB;
    private String columnLongOnDB;
    
    private String columnZOnDB;
    
	public GPSPage(String title, String columnXOnDB, String columnYOnDB, String columnZOnDB,  String columnLatOnDB, String columnLongOnDB, String comment, ModelPath modelPath) {
		super(title, null, comment, modelPath);
		this.columnXOnDB = columnXOnDB;
		this.columnYOnDB = columnYOnDB;
		this.columnLatOnDB = columnLatOnDB;
		this.columnLongOnDB = columnLongOnDB;
		this.columnZOnDB = columnZOnDB;
	}

	@Override
	public PageInterface createFragment() {
		fragment = GPSFragment.create(title);
		return fragment;
	}

	@Override
	public void addReviewItemTo(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(getTitle(), getXValue(),
				getKey()));

		dest.add(new ReviewItem(getTitle()+"Y", getYValue(),
				getKey()));
		dest.add(new ReviewItem(getTitle()+"Lat", getLatValue(),
				getKey()));
		dest.add(new ReviewItem(getTitle()+"Long", getLongitudeValue(),
				getKey()));
	}

	@Override
	public boolean isCompleted() {
		return (!TextUtils.isEmpty(getXValue()) && !TextUtils.isEmpty(getYValue())) || (!TextUtils.isEmpty(getLatValue()) && !TextUtils.isEmpty(getLongitudeValue())) ;
	}

	public String getLongitudeValue() {
		 return super.getValue(columnLongOnDB);
	}

	public String getLatValue() {
		 return super.getValue(columnLatOnDB);
	}
	
	 public String getXValue() {
		 return super.getValue(columnXOnDB);
	 }
	 public String getYValue() {
		 return super.getValue(columnYOnDB);
	 }
	 public String getZValue() {
		 return super.getValue(columnZOnDB);
	 }
	 
	 public void saveXValue(String value) {
		 super.saveValue(value, columnXOnDB);
	 }
	 public void saveYValue(String value) {
		 super.saveValue(value, columnYOnDB);
	 }
	 public void saveZValue(String value) {
		 super.saveValue(value, columnZOnDB);
	 }
	 
	 public void saveLatValue(String value) {
		 super.saveValue(value, columnLatOnDB);
	 }
	 public void saveLongitudeValue(String value) {
		 super.saveValue(value, columnLongOnDB);
	 }
	 
	
	@Deprecated
	@Override
	 public String getValue() {
		 throw new RuntimeException("don´t use this");
	 }
	
	@Deprecated
	@Override 
	public void saveValue(String value) {
		 throw new RuntimeException("don´t use this");
    }
	
	public String getUniqueIdentifier() {
		return columnXOnDB;
	}


}

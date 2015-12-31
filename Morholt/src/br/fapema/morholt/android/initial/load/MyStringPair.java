package br.fapema.morholt.android.initial.load;

import java.util.ArrayList;
import java.util.List;

public class MyStringPair implements TwoColumnListViewInterface{

    private String columnOne;
    private String columnTwo;
    
    public MyStringPair(String columnOne, String columnTwo) {
        super();
        this.columnOne = columnOne;
        this.columnTwo = columnTwo;
    }
    
    public String getColumnOne() {
        return columnOne;
    }
    public void setColumnOne(String columnOne) {
        this.columnOne = columnOne;
    }
    public String getColumnTwo() {
        return columnTwo;
    }
    public void setColumnTwo(String columnTwo) {
        this.columnTwo = columnTwo;
    }
    
    public static List<TwoColumnListViewInterface> makeData(int n) {
        List<TwoColumnListViewInterface> pair = new ArrayList<TwoColumnListViewInterface>();
        for (int i=0;i<n;i++) {
            pair.add(new MyStringPair(Integer.toString(i), "Col 2 value "+Integer.toString(i)));
        }
        return pair;
    }

	@Override
	public String getColumnOneHeader() {
		// TODO Auto-generated method stub
		return "h1";
	}

	@Override
	public String getColumnTwoHeader() {
		// TODO Auto-generated method stub
		return "h2";
	}


}

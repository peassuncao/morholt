package br.fapema.morholt.android;

import android.view.View;

public interface SwipeContainer {
	public final int LEFT_RESULT = 66;
	public final int RIGHT_RESULT = 67;
	public void loadNextPage();
	public void loadPreviousPage();
	public View getContainerPanel();
}

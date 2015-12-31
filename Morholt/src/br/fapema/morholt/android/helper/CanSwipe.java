package br.fapema.morholt.android.helper;

import android.view.View;

public interface CanSwipe {
	public void onRightSwipe();
	public void onLeftSwipe();
	public View getContainerPanel();
}

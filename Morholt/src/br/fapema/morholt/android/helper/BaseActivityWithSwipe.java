package br.fapema.morholt.android.helper;

import android.os.Bundle;
import br.fapema.morholt.android.model.BaseActivity;

public abstract class BaseActivityWithSwipe extends BaseActivity implements CanSwipe{
	private Swiper swiper;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		swiper = new Swiper();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		swiper.start(getApplicationContext(), this);
	}
}

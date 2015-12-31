package br.fapema.morholt.android.listeners;

import android.text.Editable;
import android.text.TextWatcher;
import br.fapema.morholt.android.collectpages.interfaces.SetadorColeta;

public class TextChangedListener implements TextWatcher {
	private SetadorColeta setador;
	
	private int viewId;
	
	public TextChangedListener(SetadorColeta setador) {
		this.setador = setador;
	}
	
	public TextChangedListener(SetadorColeta setador, int viewId) {
		this.setador = setador;
		this.viewId = viewId;
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		setador.valueChanged(viewId);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}

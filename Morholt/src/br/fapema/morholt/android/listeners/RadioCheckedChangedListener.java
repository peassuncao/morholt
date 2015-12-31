package br.fapema.morholt.android.listeners;

import android.widget.RadioGroup;
import br.fapema.morholt.android.collectpages.interfaces.SetadorColeta;

public class RadioCheckedChangedListener implements
		RadioGroup.OnCheckedChangeListener {

	private SetadorColeta setador;

	public RadioCheckedChangedListener(SetadorColeta setador) {
		this.setador = setador;
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		setador.radioMudou(checkedId);
	}
}

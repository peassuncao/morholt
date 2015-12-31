package br.fapema.morholt.android.wizardpager.wizard.basic;

import br.fapema.morholt.android.wizardpager.wizard.model.DAOInterface;

public class LoadModelEvent {
	public DAOInterface baseModel;
	public LoadModelEvent(DAOInterface baseModel) {
		this.baseModel = baseModel;
	}
}

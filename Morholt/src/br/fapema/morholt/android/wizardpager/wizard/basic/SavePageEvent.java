package br.fapema.morholt.android.wizardpager.wizard.basic;

import br.fapema.morholt.android.wizardpager.wizard.model.Page;

public class SavePageEvent {
	public Page page;
	public SavePageEvent(Page page) {
		this.page = page;
	}
}

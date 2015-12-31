package br.fapema.morholt.android.wizardpager.wizard.model;

import br.fapema.morholt.android.wizardpager.wizard.model.Page;

public class PageChangeEvent {
	public Page page;
	public PageChangeEvent(Page page) {
		this.page = page;
	}
}

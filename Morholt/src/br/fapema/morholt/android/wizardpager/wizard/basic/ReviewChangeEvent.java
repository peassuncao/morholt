package br.fapema.morholt.android.wizardpager.wizard.basic;

import java.util.List;

import br.fapema.morholt.android.wizardpager.wizard.model.Page;

public class ReviewChangeEvent {
	public List<Page> mCurrentPageSequence;
	public ReviewChangeEvent(List<Page> mCurrentPageSequence) {
		this.mCurrentPageSequence = mCurrentPageSequence;
	}
}

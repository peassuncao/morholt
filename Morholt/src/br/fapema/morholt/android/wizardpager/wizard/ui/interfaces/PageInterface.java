package br.fapema.morholt.android.wizardpager.wizard.ui.interfaces;

import br.fapema.morholt.android.wizardpager.wizard.model.Page;



public interface PageInterface {
	public boolean validate();
	public void showErrors();
	public void clearErrors(); 
	public void markAsNotFirstOnResumeAnyomore();
	public void configureKeyboard(boolean hideKeyboard);
	public String getUniqueIdentifier();
	public Page getPage(String pageId);
	/**
	 * updates the fragment, based on the page
	 */
	public void update();
	public void disable();
	boolean checkIfCanChangeValues();
}
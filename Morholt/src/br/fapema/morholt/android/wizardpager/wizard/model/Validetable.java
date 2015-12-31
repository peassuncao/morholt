package br.fapema.morholt.android.wizardpager.wizard.model;



public interface Validetable {
	public boolean validate();
	public void showErrors();
	public void clearErrors(); 
	public void markAsNotFirstOnResumeAnyomore();
}
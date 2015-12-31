package br.fapema.morholt.android.collectpages.interfaces;



public interface Validetable {
	public boolean validate();
	public void showErrors();
	public void clearErrors(); 
	public void markAsNotFirstOnResumeAnyomore();
	public String getUniqueIdentifier();
}
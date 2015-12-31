package br.fapema.morholt.web.client.gui;

import com.google.gwt.user.client.ui.Panel;


public interface AuxiliarInterface {

	public void setRefinedSearchAuxiliarWidget(Panel refinedSearchAuxiliarWidget);
	
	/**
	 * prepare the panel set on setRefinedSearchAuxiliarWidget, set it´s visibility 
	 */
	void prepareAuxiliaryPanel();

}

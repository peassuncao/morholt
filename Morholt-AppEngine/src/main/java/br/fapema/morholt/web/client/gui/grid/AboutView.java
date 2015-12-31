package br.fapema.morholt.web.client.gui.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import br.fapema.morholt.web.client.gui.AuxiliarInterface;

public class AboutView  extends Composite implements AuxiliarInterface {


	  // @UiTemplate("AboutView.ui.xml")
	 interface MyUiBinder extends UiBinder<Widget, AboutView> {}
	  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	  private Panel auxPanel;


	  public AboutView(VerticalPanel westAuxiliarPanel) {
	    initWidget(uiBinder.createAndBindUi(this));
	    setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
	  }

	@Override
	public void setRefinedSearchAuxiliarWidget(Panel refinedSearchAuxiliarWidget) {
	    auxPanel = refinedSearchAuxiliarWidget;
		
	}

	@Override
	public void prepareAuxiliaryPanel() {
		auxPanel.setVisible(false);
		
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return this;
	}

}

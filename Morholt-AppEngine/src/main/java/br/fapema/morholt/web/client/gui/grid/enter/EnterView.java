package br.fapema.morholt.web.client.gui.grid.enter;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import br.fapema.morholt.web.client.gui.UsesAuxiliaryPanelInterface;
import br.fapema.morholt.web.client.gui.basic.BackCallback;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;

public class EnterView extends Composite  {
	private DockLayoutPanel aPanel = new DockLayoutPanel(Unit.EM);
	private BackCallback backCallback;
	private EnterContent enterContent;
	private Button backButton;
	private String selectedKeyValue;
	
	public EnterView(BackCallback backCallback, EnterContent enterContent, String selectedKeyValue) {
		this.backCallback = backCallback;
		this.enterContent = enterContent;
		this.selectedKeyValue = selectedKeyValue;
		initWidget(aPanel);
		aPanel.setStyleName("enterPanel");
		Label header = new Label (selectedKeyValue);
		header.addStyleName("labelHeader");
		backButton = new Button("Voltar");
		backButton.addClickHandler(new BackButtonClickHandler());

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(header);
		horizontalPanel.add(backButton);
		horizontalPanel.setSpacing(10);
		
		aPanel.addNorth(horizontalPanel, 4);
		aPanel.add(enterContent);
		

		
	}
	
	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		
	}
	
	private class BackButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			Widget enterCompositeSelectedTab = enterContent.getWidget(enterContent.getSelectedIndex());
			if(enterCompositeSelectedTab instanceof UsesAuxiliaryPanelInterface) {
				UsesAuxiliaryPanelInterface usesInterface =  (UsesAuxiliaryPanelInterface) enterCompositeSelectedTab;
				usesInterface.hideAuxiliaryPanel();
			}
			backCallback.back(CRUDEL.ENTER, null);
		}
	}
	
	
}
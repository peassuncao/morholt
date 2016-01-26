package br.fapema.morholt.web.client.gui.grid;


import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.basic.BackCallback;
import br.fapema.morholt.web.client.gui.grid.enter.EnterView;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;

public class DetailView extends Composite  {

	private DockLayoutPanel aPanel = new DockLayoutPanel(Unit.EM);
	//private VerticalPanel aPanel = new VerticalPanel();
	private BackCallback backCallback;
	private Composite enterView;
	private Model detailModel;
	private int numberOfDetailsColumns;
	private List<DataSource> dataSources;
	private Map<String, List<Model>> mapKindToSmallTables;
	private Profile profile;
	private MyServiceClientImpl myService;
	private int width;
	
	public DetailView(int width, Model selectedModel, List<DataSource> dataSources, BackCallback backCallback, Composite enterView, int numberOfDetailsColumns, Map<String, List<Model>> mapKindToSmallTables, Profile profile, MyServiceClientImpl myService) {
		this.width = width;
		this.profile = profile;
		this.backCallback = backCallback;
		this.enterView = enterView;
		this.numberOfDetailsColumns = numberOfDetailsColumns;
		this.dataSources = dataSources;
		this.mapKindToSmallTables = mapKindToSmallTables;
		this.myService = myService;
		detailModel = selectedModel;
		initWidget(aPanel);
		setWidth(width+"px");
		aPanel.addStyleName("detailPanel");
	
		showDetails();
	}
	
	
	private void showDetails() {
		aPanel.clear();
		HTMLPanel header = new HTMLPanel ("h1", "Detalhes");
		
		Button backButton = new Button("Voltar");
		backButton.addClickHandler(new BackButtonClickHandler());

		aPanel.addNorth(header, 3);
		aPanel.addSouth(backButton, 3);
		
		FlexTable flexTable = FlexTableHelper.createTable(width, "Detalhes", detailModel, dataSources, numberOfDetailsColumns, FlexTableEnum.Detail, mapKindToSmallTables, profile, myService);
		FlexTableHelper.disableAllChildren(flexTable);
		aPanel.add(new ScrollPanel(flexTable));
	}
	
	private class BackButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			backCallback.back(CRUDEL.READ, null);
		}
	}
	
	/*
	private class EnterButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			
			aPanel.clear();
			EnterView view = new EnterView(detailModel, DetailView.this, enterView);
			view.setHeight(Window.getClientHeight()-aPanel.getAbsoluteTop() + "px");
			view.setWidth(Window.getClientWidth()-aPanel.getAbsoluteLeft() + "px");
			
			aPanel.add(view); 
		}
	}*/
/*
	@Override
	public void back() {
		showDetails();
	}*/
}
package br.fapema.morholt.web.client.gui;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MenuView extends Composite  {
	private HorizontalPanel hPanel = new HorizontalPanel();
	private MainView mainView;
	
	public MenuView(MainView mainView) {
		initWidget(hPanel);
		this.mainView = mainView;

		/*
		Button botaoMapa = new Button("exibirMapa");
		botaoMapa.addClickHandler(new MapButtonClickHandler());
		hPanel.add(botaoMapa);
		*/
	}
	
	private class MapButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
		//	mainView.loadCollectData();
		}
		
	}
}

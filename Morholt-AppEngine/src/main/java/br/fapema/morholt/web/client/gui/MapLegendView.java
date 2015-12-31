package br.fapema.morholt.web.client.gui;

import java.util.HashMap;

import com.google.gwt.http.client.Header;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MapLegendView extends VerticalPanel {

	public MapLegendView(HashMap<String, String> mapProjectToIcon) {
		super();
		

		FlexTable flexTable = new FlexTable();
		flexTable.setBorderWidth(1);
		
		flexTable.addStyleName("mapLegendTable");
		
		Label header1 = new Label("Coleção");

		Label header2 = new Label("Ícone");

		flexTable.setWidget(0,  0, header1);
		flexTable.setWidget(0,  1, header2);
		
		int row = 1;
		for(String project : mapProjectToIcon.keySet()) {
			Label label = new Label(project);
			Image image = new Image(mapProjectToIcon.get(project));

			flexTable.setWidget(row,  0, label);
			flexTable.setWidget(row,  1, image);
			row++;
		}
		add(flexTable);
	}
}

package br.fapema.morholt.web.client.gui.grid;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import br.fapema.morholt.web.shared.FilterInterface;

public class ExportPopupPanel extends PopupPanel {

	private ListBox dropBoxLimit ;
	private FilterHelper filterHelper;
	
	public ExportPopupPanel(GridView grid, FilterHelper filterHelper, String chosenProject) {
		this.filterHelper = filterHelper;
		setModal(true);
		setTitle("Exportar");
		setPopupPosition(Window.getClientWidth()/3, Window.getClientHeight()/3);
		addStyleDependentName("exportPanel");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleDependentName("exportPanel");

		HTMLPanel header = new HTMLPanel ("h1", "Exportar (baseado nos campos de busca)");
		verticalPanel.add(header);
		
		Label labelGroup1 = new Label("Max resultados:");
		labelGroup1.addStyleName("marginRight1");
		verticalPanel.add(labelGroup1);
		
		dropBoxLimit = createDropBoxLimit();
		verticalPanel.add(dropBoxLimit);
		
		HorizontalPanel buttonsPanel = createExportButtonsPanel(grid, chosenProject);
	     verticalPanel.add(buttonsPanel);
		
		add(verticalPanel);
	}
	
	private HorizontalPanel createExportButtonsPanel(final GridView grid, final String chosenProject) {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
	      Button ok = createOkExportButton(grid, chosenProject);
	      Button cancel = createCancelExportButton();
	      buttonsPanel.add(ok);
	      buttonsPanel.add(cancel);
		return buttonsPanel;
	}

	private Button createCancelExportButton() {
		Button cancel = new Button("Cancelar");
	      cancel.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	        	hide();
	        }
	      });
		return cancel;
	}

	private Button createOkExportButton(final GridView grid, final String chosenProject) {
		Button ok = new Button("Exportar");
	      ok.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	        	FilterInterface filterInterface = filterHelper.obtainFilterInterface(chosenProject);
				List<String> sort = filterHelper.obtainSort();
				List<String> sortDirection = filterHelper.obtainSortDirection(); 
	        	grid.export(filterInterface, sort, sortDirection,
						dropBoxLimit.getSelectedValue());
				
	        	hide();
	        }
	      });
		return ok;
	}
	
	private ListBox createDropBoxLimit() {
		ListBox dropBoxLimit = new ListBox();
		dropBoxLimit.setMultipleSelect(false);
		dropBoxLimit.addItem("sem limite");
		dropBoxLimit.addItem("1000");
		dropBoxLimit.addItem("10000");
		dropBoxLimit.addItem("100000");
		dropBoxLimit.addItem("1000000");
		return dropBoxLimit;
	}

	public ExportPopupPanel(boolean autoHide) {
		super(autoHide);
	}

	public ExportPopupPanel(boolean autoHide, boolean modal) {
		super(autoHide, modal);
	}

}

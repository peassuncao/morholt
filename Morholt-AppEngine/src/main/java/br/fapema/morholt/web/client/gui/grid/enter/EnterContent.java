package br.fapema.morholt.web.client.gui.grid.enter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import br.fapema.morholt.web.client.gui.AuxiliarInterface;
import br.fapema.morholt.web.client.gui.grid.EnterInterface;
import br.fapema.morholt.web.client.gui.grid.GridView;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;

public class EnterContent extends TabLayoutPanel {
	
	private EnterTypes types;
	private MyServiceClientImpl myService;
	private Model selectedModel;
	private String keyName;
	private String kind;
	private Map<String, EnterInterface> mapTitleWidget;
	private Map<String, EnterInterface> collectTemplateWidgets;
	private String parentColumnLabel;
	private String parentColumn;
	

	private static final Logger log = Logger.getLogger(EnterContent.class.getName());
	
	public EnterContent(MyServiceClientImpl myService, EnterTypes enterTypes, String keyName, String kind, Map<String, EnterInterface> mapTitleWidget, HashMap<String, EnterInterface> collectTemplateWidgets, String parentColumn, String parentColumnLabel) {
		super(1.9, Unit.EM);
		this.types = enterTypes;
		this.myService = myService;
		this.keyName = keyName;
		this.kind = kind;
		this.mapTitleWidget = mapTitleWidget;
		this.collectTemplateWidgets = collectTemplateWidgets;
		this.parentColumn = parentColumn;
		this.parentColumnLabel = parentColumnLabel;
		
		addStyleName("enterContent");
	}
	
	public void initialize(Model selectedModel) {
		this.selectedModel = selectedModel;
		remove(0);
		removeAllTabs();
		String collectTemplateName = "";
		
		log.info("selectedModel: " + selectedModel);
		
		for (String template : collectTemplateWidgets.keySet()) {
			if (template.equals(selectedModel.get("androidTemplate"))) {
				EnterInterface enterInterface = collectTemplateWidgets.get(template);
				enterInterface.setEnterSelectedModel(selectedModel);
				enterInterface.setEnterSelectedModel(selectedModel);
				enterInterface.setParentColumn(parentColumn);
				enterInterface.setParentColumnLabel(parentColumnLabel);
				enterInterface.clear();
				add(enterInterface, template);
				collectTemplateName = template;
				break;
			}
		}

		if(mapTitleWidget != null)
		for (String title : mapTitleWidget.keySet()) {
			EnterInterface enterInterface = mapTitleWidget.get(title);
			enterInterface.setEnterSelectedModel(selectedModel);
			enterInterface.clear();
			add(enterInterface, title);
		}
		 EnterInterface enterInterface = collectTemplateWidgets.get(collectTemplateName);
		 enterInterface.prepareAuxiliaryPanel();
		 
		 addSelectionHandler(new SelectionHandler<Integer>() {
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					Integer selectedItem = event.getSelectedItem();
					Widget widget = getWidget(selectedItem);
					if(widget instanceof AuxiliarInterface) {
						AuxiliarInterface auxiliarInterface =  (AuxiliarInterface) widget;
						auxiliarInterface.prepareAuxiliaryPanel();
					}
					else {
						throw new RuntimeException("all tabs must implement AuxiliarInterface");
					}
				}
			});	

			log.log(Level.FINEST, "initialized");
	}

	private void removeAllTabs() {
		for(int i=getWidgetCount()-1; i>=0; i--) 
				remove(i); 
	}
}
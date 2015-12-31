package br.fapema.morholt.web.client.gui.grid;


import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.basic.BackCallback;
import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.exception.UserNotAllocatedToProjectException;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.gui.util.Toast;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

public class CreateModelView extends Composite  implements AsyncCallback<SimpleString> {

	private static final Logger log = Logger.getLogger(CreateModelView.class.getName());
	
	private static final int NUMBER_OF_COLUMNS = 1;
	private DockLayoutPanel aPanel = new DockLayoutPanel(Unit.EM);;
	private BackCallback backCallback;
	private List<DataSource> dataSources;
	private FlexTable table;
	
	private Model newModel;
	private MyServiceClientImpl myClientInterface;
	private String kind;
	private String ancestor;
	private String keyColumn;
	private Map<String, List<Model>> mapKindToSmallTables;

	private List<Model> models;
	private Profile profile;
	
	private String projectName;
	
	
	
	public CreateModelView(String kind, String keyColumn, String ancestor, List<DataSource> dataSources, List<Model> models, MyServiceClientImpl myService, BackCallback backCallback, Map<String, List<Model>> mapKindToSmallTables, Model enterSelectedModel, Profile profile) {
		this.profile = profile;
		this.backCallback = backCallback;
		this.dataSources = dataSources;
		this.kind = kind;
		this.ancestor = ancestor;
		this.keyColumn = keyColumn;
		this.mapKindToSmallTables = mapKindToSmallTables;
		this.models = models;
		
		myClientInterface = myService;
		initWidget(aPanel);

		aPanel.setWidth("100%");
		aPanel.addStyleName("detailPanel");

		aPanel.addNorth(new HTMLPanel ("h1", "Novo"), 3);
		aPanel.addSouth(createButtonsPanel(), 3);
		
		newModel = new Model(keyColumn, kind);
		if(enterSelectedModel != null) {
			projectName = enterSelectedModel.get("name");  //TODO this "name" should be a variable
			newModel.put("project_name", projectName); //should be a variable  "project_name"
			newModel.put("cidade",  enterSelectedModel.get("nome")); //should be a variable  "project_name"
			newModel.put("state",  enterSelectedModel.get("state")); //should be a variable  "project_name"
		}
		
		table = FlexTableHelper.createTable("Novo", newModel, dataSources, NUMBER_OF_COLUMNS, FlexTableEnum.New, mapKindToSmallTables, profile, myService);
		
		aPanel.add(new ScrollPanel(table));
	}
	
	private HorizontalPanel createButtonsPanel() {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(createButton("Salvar", new SaveButtonClickHandler()));
		horizontalPanel.add(createButton("Voltar", new BackButtonClickHandler()));
		return horizontalPanel;
	}
	
	private Button createButton(String label, ClickHandler clickHandler) {
		Button button = new Button(label);
		button.addClickHandler(clickHandler);
		return button;
	}
	
	private void refreshTable() {
		table.clear();
		table.removeAllRows();
		newModel = new Model(keyColumn, kind);
		if(projectName != null) {
			newModel.put("project_name", projectName); //should be a variable  "project_name"
		}
		FlexTableHelper.addDataToTable(newModel, dataSources, NUMBER_OF_COLUMNS, FlexTableEnum.New, table, mapKindToSmallTables, profile, myClientInterface);
	}
	
	private class SaveButtonClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			
			log.info("SaveButtonClickHandler.onClick size: " + newModel.getContentValues().size());
			
			for(String key : newModel.getContentValues().keySet()) {
			
				log.log(Level.INFO, "\nNEW_MODEL key: " + key + " value: " + newModel.getContentValues().get(key));
			}
			
			myClientInterface.insert(kind, keyColumn, newModel.getContentValues(), Ajax.call(CreateModelView.this, true, CreateModelView.class + " save"));
			
			log.info("SaveButtonClickHandler.onClick done");
		}
	}
	
	private class BackButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			backCallback.back(CRUDEL.CREATE, null);
		}
	}

	@Override
	public void onFailure(Throwable caught) {
		if(caught instanceof UserNotAllocatedToProjectException) {
			Toast.show("Não foi possível salvar, este usuário não está alocado para este projeto.");
		}
		else if(caught instanceof MyQuotaException) {
			Toast.show("Não foi possível salvar, quota diária do site ultrapassada.");
		}
		else if(caught instanceof AuthorizationException) {
			Toast.show("Não foi possível salvar, usuário nsão tem permissão.");
		}
		else 
			Toast.show("Falha ao tentar salvar, tente novamente.");
	}

	@Override
	public void onSuccess(SimpleString result) {

		newModel.put("Key", result.getString());
		models.add(0, newModel);
		refreshTable();
		Toast.show("sucesso");
	}
}
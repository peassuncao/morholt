package br.fapema.morholt.web.client.gui.grid;


import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
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

public class EditView extends Composite  {
	private DockLayoutPanel aPanel = new DockLayoutPanel(Unit.EM);
	private BackCallback backCallback;
	private FlexTable table;
	private Model model;
	private MyServiceClientImpl myServiceClientImpl;
	private String kind;
	private CallAfterEditionInterface callAfterEditionInterface;

	private Model lastSavedModel;
	
	private DialogBox dialogBox;
	
	public EditView(String kind, List<DataSource> dataSources, MyServiceClientImpl myService, BackCallback backCallback, Model selectedModel, int numberOfDetailsColumns, Map<String, List<Model>> mapKindToSmallTables, CallAfterEditionInterface callAfterEditionInterface, Profile profile) {
		this.backCallback = backCallback;
		this.kind = kind;
		this.callAfterEditionInterface = callAfterEditionInterface;
		myServiceClientImpl = myService;
		initWidget(aPanel);
		aPanel.addStyleName("detailPanel");
		
		HTMLPanel header = new HTMLPanel ("h1", "Editar");
		
		lastSavedModel = (Model) selectedModel.clone();
		model = selectedModel;  
		
		Button saveButton = new Button("Salvar");
		saveButton.addClickHandler(new SaveButtonClickHandler());
		

		Button backButton = new Button("Voltar");
		backButton.addClickHandler(new BackButtonClickHandler());

		aPanel.addNorth(header, 3);
		table = FlexTableHelper.createTable("Editar", model, dataSources, numberOfDetailsColumns, FlexTableEnum.Edit, mapKindToSmallTables, profile, myService);
		
		ScrollPanel tableScrollPanel = new ScrollPanel(table);
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(saveButton);
		horizontalPanel.add(backButton);
		aPanel.addSouth(horizontalPanel, 3);
		
		aPanel.add(tableScrollPanel);
	}

	
	private class SaveButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			myServiceClientImpl.insert(kind, "NP", model.getContentValues(), Ajax.call(new InsertCallback(), true, EditView.class + "save kind: " + kind));
		}
	}
	
	private class InsertCallback implements AsyncCallback<SimpleString> {

		@Override
		public void onFailure(Throwable caught) {

			if(caught instanceof UserNotAllocatedToProjectException) {
				Toast.show("Não foi possível editar, este usuário não está alocado para este projeto.");
			}
			else if(caught instanceof MyQuotaException) {
				Toast.show("Não foi possível editar, quota diária do site ultrapassada.");
			}
			else if(caught instanceof AuthorizationException) {
				Toast.show("Não foi possível editar, usuário nsão tem permissão.");
			}
			else 
				Toast.show("Falha ao tentar editar, tente novamente.");
			
			throw new RuntimeException(caught);
			
		}

		@Override
		public void onSuccess(SimpleString result) {
			model.put("Key", result.getString());
			if(callAfterEditionInterface!=null) callAfterEditionInterface.call(model);
			lastSavedModel = (Model) model.clone();
			Toast.show("sucesso");
		}
		
	}
	
	private class BackButtonClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			backCallback.back(CRUDEL.UPDATE, lastSavedModel);
		}
	}
}
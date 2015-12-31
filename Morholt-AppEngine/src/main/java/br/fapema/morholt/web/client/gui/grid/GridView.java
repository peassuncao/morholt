package br.fapema.morholt.web.client.gui.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import br.fapema.morholt.collect.SimpleString;
import br.fapema.morholt.web.client.gui.AuxiliarInterface;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.MyPaginationDataGrid;
import br.fapema.morholt.web.client.gui.basic.BackCallback;
import br.fapema.morholt.web.client.gui.basic.ConfirmDialog;
import br.fapema.morholt.web.client.gui.basic.DialogCallback;
import br.fapema.morholt.web.client.gui.grid.enter.EnterContent;
import br.fapema.morholt.web.client.gui.grid.enter.EnterView;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.FilterInterface;

public class GridView extends Composite implements BackCallback, EnterInterface{
	private static final String MSG_SELECT_ITEM = "Favor selecionar um item";
	private static final String GRID_SEARCH_TITLE = "busca no grid";
	private DockLayoutPanel aPanel = new DockLayoutPanel(Unit.EM);
	private AsyncCallback callback;
	private MyServiceClientImpl myService;
	protected List<DataSource> dataSources;
	private EnterContent enterComposite;
	private MyPaginationDataGrid pagingDataGrid;
	private String kind;
	private String startCursor;
	private int limit;
	private String keyColumn;
	protected Model enterSelectedModel;
	private int numberOfDetailsColumns;
	Map<String, List<Model>> mapKindToSmallTables;
	private Profile profile;
	private CallAfterEditionInterface callAfterEditionInterface;
	protected Panel refinedSearchAuxiliarWidget;
	protected boolean refinedSearchIsUsed = false;
	
	private FilterInterface filterGrouping;
	protected boolean enterInAction = false;
	
	private static final Logger log = Logger.getLogger(GridView.class.getName());
	private DoubleClickHandler enterDoubleClickHandler;
	private DoubleClickHandler showDetailsDoubleClickHandler;
	private boolean addFeaturedButton = false;
	private String parentColumn;
	private String parentColumnLabel;
	
	public GridView(String kind, String keyColumn, AsyncCallback callback, MyServiceClientImpl myService, List<DataSource> dataSources, EnterContent enterView, int heightPct, int numberOfDetailsColumns, CallAfterEditionInterface callAfterEditionInterface, Map<String, List<Model>> mapKindToSmallTables, String parentName, boolean addFeaturedButton) {
		this(kind, keyColumn, callback, myService, dataSources, enterView, heightPct, numberOfDetailsColumns, callAfterEditionInterface, mapKindToSmallTables, parentName);
		this.addFeaturedButton = addFeaturedButton;
	}
	/**
	 * 
	 * @param callback if null, callback is internal to GridView (RefreshCallback)
	 * @param myService
	 * @param dataSources
	 * @param enterComposite is optional, it let´s you go deeper adding a new composite from detailView
	 * 
	 * @param callAfterEditionInterface is optional, make a call after edition
	 * @param mapKindToSmallTables 
	 */
	public GridView(String kind, String keyColumn, AsyncCallback callback, MyServiceClientImpl myService, List<DataSource> dataSources, EnterContent enterView, int heightPct, int numberOfDetailsColumns, CallAfterEditionInterface callAfterEditionInterface, Map<String, List<Model>> mapKindToSmallTables, String parentName) {
		this.dataSources = dataSources;
		this.enterComposite = enterView;
		initWidget(aPanel);
		this.myService = myService;
		this.callback = callback;
		if(callback == null) callback = new RefreshCallback();
		this.kind = kind;
		this.keyColumn = keyColumn;
		this.numberOfDetailsColumns = numberOfDetailsColumns;
		this.callAfterEditionInterface = callAfterEditionInterface;
		this.mapKindToSmallTables = mapKindToSmallTables;
		profile = myService.getProfile();
		
		aPanel.setWidth("100%");
		
		startCursor = null; 
		limit = 0;
		 
		if(parentName == null) {
			new RefreshButtonClickHandler().onClick(null);
		}
		else {
			if(this.callback == null) this.callback = new RefreshCallback();
			updateGrid(new ArrayList<Model>());
		}
	}

	
	
	public void updateGrid(List<Model> models) {
		showGrid();
		pagingDataGrid.setDataList(models);
		log.info("updatedGrid");
	}
	
	
	public void showGrid() {
		aPanel.clear();
		aPanel.setStylePrimaryName("gridViewPanel");
		if(pagingDataGrid == null) {
			pagingDataGrid = new MyPaginationDataGrid(dataSources, this);
			setDoubleClickHandler();
		}
		
		pagingDataGrid.setHeight("100%");
		pagingDataGrid.setWidth("100%");
		addButtons();
		 ResizeLayoutPanel resizeLayoutPanel = new ResizeLayoutPanel();
	    	resizeLayoutPanel.setHeight("100%");
	    	resizeLayoutPanel.setWidth("100%");
	    	resizeLayoutPanel.add(pagingDataGrid);
	    	
		aPanel.add(resizeLayoutPanel);
	}
	
	public List<Model> getCurrentList() {
		return pagingDataGrid.getDataList();
	}

	@Override
	public void setRefinedSearchAuxiliarWidget(Panel refinedSearchAuxiliarWidget) {
		this.refinedSearchAuxiliarWidget = refinedSearchAuxiliarWidget;
		
	}
	
	
	public void setRefinedSearchAuxiliar(Panel widget, boolean used) {
		this.refinedSearchAuxiliarWidget = widget;
		this.refinedSearchIsUsed = used;
	}
	
	public Widget getRefinedSearchAuxiliarWidget() {
		return refinedSearchAuxiliarWidget;
	}
	
	private void addButtons() {
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.addStyleDependentName("flowPanelButtons");
		if(refinedSearchIsUsed == false)
			flowPanel.add(createSeachButton());
		
		if(profile.authenticate(kind, Profile.CRUDEL.READ))
			flowPanel.add(createDetailButton());
		
		if (enterComposite != null && profile.authenticate(kind, Profile.CRUDEL.ENTER)) 
			flowPanel.add(createEnterButton());
		
		if (profile.authenticate(kind, Profile.CRUDEL.DELETE)) 
			flowPanel.add(createDeleteButton());
		
		if(profile.authenticate(kind, Profile.CRUDEL.UPDATE))
			flowPanel.add(createEditButton() );
		
		if(profile.authenticate(kind, Profile.CRUDEL.CREATE))
			flowPanel.add(createNewButton());
		
		if(addFeaturedButton && profile.authenticate(kind, Profile.CRUDEL.FEATURE))
			flowPanel.add(createFeatureButton());
		
		
		aPanel.addSouth(flowPanel, 2);
	}

	private Widget createFeatureButton() {
		Button featureButton = new Button("Destacar");
		featureButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SingleSelectionModel<Model> singleSelectionModel = pagingDataGrid.getSelectionModel();
				if(singleSelectionModel == null) {
					Window.alert(MSG_SELECT_ITEM);
					return;
				}
				final Model selectedModel = pagingDataGrid.getSelectionModel().getSelectedObject();
				
				log.info("createFeaturedButton"+selectedModel);
				Boolean isFeatured = selectedModel.isFeatured() == null ? false : selectedModel.isFeatured();
				log.info("isFeatured: " + isFeatured);
				selectedModel.setFeatured(!isFeatured);
				log.info("selectedModel: " + selectedModel); // FIXME
				log.info("callback: " + callback);
				myService.insert(kind, "NP", selectedModel.getContentValues(), Ajax.call(new FeatureSettingCallback(), kind+"_feature")); 
				log.info("createFeaturedButton done" );
			}
		});
		return featureButton;
	}
	private Widget createDeleteButton() {
		Button deleteButton = new Button("Excluir");
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SingleSelectionModel<Model> singleSelectionModel = pagingDataGrid.getSelectionModel();
				if(singleSelectionModel == null) {
					Window.alert(MSG_SELECT_ITEM);
					return;
				}
				final Model selectedModel = pagingDataGrid.getSelectionModel().getSelectedObject();
				log.info(""+selectedModel);
				String nameToBeDeleted =  selectedModel.getKeyValue() == null ? "" : "'"+selectedModel.getKeyValue()+"'"; 
				new ConfirmDialog("Deseja realmente excluir " + nameToBeDeleted+ " ?", new DialogCallback() { // FIXME getKeyValue is null, can´t use it!
					@Override
					public void onDialogOk() {
						myService.delete(kind, selectedModel.get("Key"), Ajax.call(new DeleteCallback(), kind+"_delete"));
				//		pagingDataGrid.remove(selectedModel);
				//		pagingDataGrid.initializeGridSelection();
						
						
					}
				});
				
			}
		});
		return deleteButton;
	}


	private Button createNewButton() {
		Button newButton = new Button("Novo");
		newButton.addClickHandler(new NewButtonClickHandler());
		return newButton;
	}
	
	private class NewButtonClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			aPanel.clear();
			log.info("mapKindToSmallTables:" + mapKindToSmallTables);
			CreateModelView createModelView = new CreateModelView(kind, keyColumn, null, dataSources, getCurrentList(), myService, GridView.this, mapKindToSmallTables, enterSelectedModel, getProfile());
			createModelView.setWidth(Window.getClientWidth()-GridView.this.getAbsoluteLeft() + "px");
			createModelView.setHeight(Window.getClientHeight()-GridView.this.getAbsoluteTop() + "px");
			aPanel.add(createModelView);
		}
	}

	private Button createSeachButton() {
		Button searchButton = new Button("Listar");
		searchButton.addClickHandler(new RefreshButtonClickHandler());
		return searchButton;
	}

	
	private Button createDetailButton() {
		Button detailButton = new Button("Detalhes");
		detailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SingleSelectionModel<Model> singleSelectionModel = pagingDataGrid.getSelectionModel();
				if(singleSelectionModel == null) {
					Window.alert(MSG_SELECT_ITEM);
					return;
				}
				showDetails((Model)singleSelectionModel.getSelectedObject());
			}
		});
		return detailButton;
	}

	private Button createEditButton() {
		Button editButton = new Button("Editar");
		editButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SingleSelectionModel<Model> singleSelectionModel = pagingDataGrid.getSelectionModel();

				if(singleSelectionModel == null) {
					Window.alert(MSG_SELECT_ITEM);
					return;
				}
				edit((Model)singleSelectionModel.getSelectedObject());
			}
		});
		return editButton;
	}

	private Button createEnterButton() {
		
		Button enterButton = new Button("Entrar");
		enterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				SingleSelectionModel<Model> singleSelectionModel = pagingDataGrid.getSelectionModel();
				if(singleSelectionModel == null) {
					Window.alert(MSG_SELECT_ITEM);
					return;
				}
				final Model selectedModel = pagingDataGrid.getSelectionModel().getSelectedObject();
				
				
			
				enter((Model)selectedModel);
			}
		});
		return enterButton;
	}
	
	private void showDetails(Model selectedModel) {
		aPanel.clear();
		DetailView detailView = new DetailView(selectedModel, dataSources, this, enterComposite, numberOfDetailsColumns, mapKindToSmallTables, getProfile(), myService);
		detailView.setWidth(Window.getClientWidth()-this.getAbsoluteLeft() + "px");

		detailView.setHeight(Window.getClientHeight()-this.getAbsoluteTop() + "px");
		   
		aPanel.add(detailView);
	}
	
	protected void enter (Model selectedModel) {
		aPanel.clear();
		enterInAction = true;
		String selectedKeyValue = selectedModel.get(selectedModel.getKeyName());
		EnterView enterView = new EnterView(this, enterComposite, selectedKeyValue);
		enterView.setHeight(Window.getClientHeight()-this.getAbsoluteTop() + "px");
		enterView.setWidth(Window.getClientWidth()-this.getAbsoluteLeft() + "px");
		enterComposite.initialize(selectedModel);
		enterComposite.setHeight("100%");
		
		aPanel.add(enterView);
		
	}
	
	private void edit(Model selectedModel) {
		aPanel.clear();
		EditView detailView = new EditView(kind, dataSources, myService, GridView.this, selectedModel, numberOfDetailsColumns, mapKindToSmallTables, callAfterEditionInterface, getProfile());
		detailView.setWidth(Window.getClientWidth()-this.getAbsoluteLeft() + "px");

		detailView.setHeight(Window.getClientHeight()-this.getAbsoluteTop() + "px");
		
		aPanel.add(detailView);
	}

	private class RefreshButtonClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String key = null;
			if(callback == null) callback = new RefreshCallback();
			
//			if(enterSelectedModel == null) {
//			}
	//		else {
				log.info("refreshing->kind: " + kind);
				myService.listItem(kind, keyColumn, key, startCursor, limit, keyColumn, "GridViewRefreshButton" + kind, Ajax.call(callback, kind+"_refresh"));
		//	}
		}
	}
	
	
	public void export(FilterInterface filterGrouping, List<String> sortProperty, List<String> sortDirection, String limit) {
		String key = null;
		this.filterGrouping = filterGrouping;
		int limitNumber = 0;
		try {
			limitNumber = Integer.parseInt(limit);
		} catch (NumberFormatException e) {
			// no limits chosen (0)    
		}
		myService.export(kind, keyColumn, key, startCursor, limitNumber, filterGrouping, sortProperty, sortDirection, null); 	
	}
	
	public void list(FilterInterface filterGrouping, List<String> sortProperty, List<String> sortDirection, String limit) {
		String key = null;
		this.filterGrouping = filterGrouping;
		myService.listItemEqual(kind, keyColumn, key, startCursor, Integer.parseInt(limit), filterGrouping, sortProperty, sortDirection, Ajax.call(callback, kind+"_list")); 	
	}
	
	public void list(int start, int length, AsyncCallback<List<Model>> callback) {
		ArrayList<String> sortList = new ArrayList<String>();
		sortList.add(keyColumn);
		myService.listItemEqual(kind, keyColumn, null, String.valueOf(start), length, filterGrouping, sortList, null, Ajax.call(callback, kind+"_list")); 	
	}
	
	private class RefreshCallback implements AsyncCallback<List<Model>> {

		@Override
		public void onFailure(Throwable caught) {
			log.info("RefreshCallback on failure");
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("falha no refresh");
		}

		@Override
		public void onSuccess(List<Model> result) {
			log.info("RefreshCallback onSuccess");
			if (result != null) {
					List<Model> models = result;
					updateGrid(models);
			}
			log.info("RefreshCallback onSuccess done");
		}
	}
	
	private class DeleteCallback implements AsyncCallback<String> {

		@Override
		public void onFailure(Throwable caught) {
			log.info("DeleteCallback on failure");
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("falha ao tentar remover");
		}

		@Override
		public void onSuccess(String key) {
			log.info("DeleteCallback onSuccess");
			

			List<Model> dataList = pagingDataGrid.getDataList();
			for (Iterator<Model> iterator = dataList.iterator(); iterator.hasNext();) {
				Model model = iterator.next();
				if(model.get("Key").equals(key)) {
					iterator.remove();
					log.info("removed");
				}
			}
			
			
			
			
			updateGrid(dataList);

			pagingDataGrid.initializeGridSelection();
			log.info("DeleteCallback onSuccess done");
		}
	}
	
	private class FeatureSettingCallback implements AsyncCallback<SimpleString> {

		@Override
		public void onFailure(Throwable caught) {
			log.info("FeatureSettingCallback on failure");
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("falha ao tentar destacar");
		}

		@Override
		public void onSuccess(SimpleString result) {
			log.info("FeatureSettingCallback onSuccess");
			if (result != null) {
				//	List<Model> models = result;
				//	updateGrid(models);
			}
			log.info("FeatureSettingCallback onSuccess done");
		}
	}
	
	public void setDoubleClickHandler() {
		if (enterComposite != null && profile.authenticate(kind, Profile.CRUDEL.ENTER)) { 
		
			if(enterDoubleClickHandler == null)
			enterDoubleClickHandler = new DoubleClickHandler() {
				@SuppressWarnings("unchecked")
				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					DataGrid<Model> grid = (DataGrid<Model>) event.getSource();
					int row = grid.getKeyboardSelectedRow();
					
					Model model = grid.getVisibleItem(row);
					GridView.this.enter(model);
				}
			};
			
			pagingDataGrid.setDoubleClickHandler(enterDoubleClickHandler);
		}
		else {
			if(showDetailsDoubleClickHandler == null)
			showDetailsDoubleClickHandler = new DoubleClickHandler() {
				@SuppressWarnings("unchecked")
				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					DataGrid<Model> grid = (DataGrid<Model>) event.getSource();
					int row = grid.getKeyboardSelectedRow();
					Model model = grid.getVisibleItem(row);
					GridView.this.showDetails(model);
				}
			};
			pagingDataGrid.setDoubleClickHandler(showDetailsDoubleClickHandler);
			
		}
	}
	

	@Override
	public void back(CRUDEL crudel, Model model) {
		List<Model> models = new ArrayList<Model>();

		 models.addAll(getCurrentList());
		switch (crudel) {
			 
		case UPDATE:
			for (Model m : models) {
				if(m.equals(model)) { 
					m = model;
					break;
				}
			}
			
			break;
		case ENTER:
		case READ:
		case CREATE:
			// do nothing
			break;
		default:
			throw new IllegalArgumentException("GridView.back " + crudel);
		}
		enterInAction = false;
		
		showGrid();
		prepareAuxiliaryPanel();
		updateGrid(models);
	}
	
	@Override
	public void setEnterSelectedModel(Model model) {
		enterSelectedModel = model;
	}


	public Map<String, List<Model>> getMapKindToSmallTables() {
		return mapKindToSmallTables;
	}


	public void setMapKindToSmallTables(
			Map<String, List<Model>> mapKindToSmallTables) {
		this.mapKindToSmallTables = mapKindToSmallTables;
	}
	
	public void clear() {
		pagingDataGrid = null; //TODO check before if profile is the same as the current user´s
		showGrid();
	}


	public Profile getProfile() {
		return profile;
	}

	public EnterContent getEnterComposite() {
		return enterComposite;
	}

	@Override
	public void prepareAuxiliaryPanel() {
			if(enterInAction) {
				EnterContent enterComposite = getEnterComposite();
				Widget enterSelectedTab = enterComposite.getWidget(enterComposite.getSelectedIndex());

				if(enterSelectedTab instanceof AuxiliarInterface) {
					AuxiliarInterface auxiliarInterface =  (AuxiliarInterface) enterSelectedTab;
					auxiliarInterface.prepareAuxiliaryPanel();
				}
				else {
					throw new RuntimeException("should implement AuxiliarInterface");
				}
			}
			else  {

				if(refinedSearchIsUsed) {
					doPrepareAuxiliaryPanel();
				}
				refinedSearchAuxiliarWidget.setVisible(refinedSearchIsUsed);
			}
			
	}

	private void doPrepareAuxiliaryPanel() {
		FilterHelper searchAuxiliarHelper = new FilterHelper();
		searchAuxiliarHelper.prepareSearchAuxiliar(refinedSearchAuxiliarWidget, dataSources, this, enterSelectedModel.get(enterSelectedModel.getKeyName()), profile, kind, parentColumn, parentColumnLabel, myService);
	}
	@Override
	public void setParentColumnLabel(String parentColumnLabel) {
		this.parentColumnLabel = parentColumnLabel;
		
	}
	@Override
	public void setParentColumn(String parentColumn) {
		this.parentColumn = parentColumn;
		
	}
	public String getParentColumn() {
		return parentColumn;
	}
	public String getParentColumnLabel() {
		return parentColumnLabel;
	}


}

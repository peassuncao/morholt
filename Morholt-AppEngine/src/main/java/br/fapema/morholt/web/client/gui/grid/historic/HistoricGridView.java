package br.fapema.morholt.web.client.gui.grid.historic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ListDataProvider;

import br.fapema.morholt.web.client.gui.AuxiliarInterface;
import br.fapema.morholt.web.client.gui.grid.DataGridResource;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.FilterInterface;
import br.fapema.morholt.web.shared.MyCompositeFilter;
import br.fapema.morholt.web.shared.MyFilterPredicate;
import br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;
import br.fapema.morholt.web.shared.TableEnum;
import br.fapema.morholt.web.shared.util.ComparatorUtils;

public class HistoricGridView extends Composite implements AuxiliarInterface {

    private static final String HISTORIC_SEARCH_TITLE = "busca de histórico";

	private DataGrid<Model> dataGrid;
	private ListDataProvider<Model> dataProvider;
	private ArrayList dataList;

    private DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);

	private Panel refinedSearchAuxiliarWidget;

	private boolean refinedSearchUsed;

	private MyServiceClientImpl myService;

	private ListHandler<Model> sortHandler;

	private Map<String, List<Model>> mapKindToSmallTables;
	
	public HistoricGridView(MyServiceClientImpl myService) {
		this.refinedSearchAuxiliarWidget = refinedSearchAuxiliarWidget;
		this.myService = myService;
    	initWidget(dock);
    	
    	DataGridResource.INSTANCE.dataGridStyle().ensureInjected();

        dataGrid = new DataGrid<Model>(20, DataGridResource.INSTANCE, null);
        dataGrid.setStylePrimaryName("dataGridList"); 
        
        dataProvider = new ListDataProvider<Model>();
        dataList = new ArrayList<Model>(); 
        dataProvider.setList(dataList);
        dataProvider.addDataDisplay(dataGrid);


		sortHandler = new ListHandler<Model>(dataProvider.getList());
        dataGrid.addColumnSortHandler(sortHandler);


        dataGrid.setEmptyTableWidget(new HTML("Nenhum dado encontrado"));
        
        MySimplePager.Resources pagerResources = GWT
                .create(MySimplePager.Resources.class);
        MySimplePager pager = new MySimplePager(TextLocation.CENTER, pagerResources, false, 0,
                true);
        pager.setDisplay(dataGrid);
    	
    	dataGrid.setWidth("100%");
		dataGrid.setTableWidth(100, Unit.PCT);
		
        ResizeLayoutPanel resizeLayoutPanel = new ResizeLayoutPanel();
    	resizeLayoutPanel.setHeight("100%");
    	resizeLayoutPanel.setWidth("100%");
    	resizeLayoutPanel.add(dataGrid);
        dock.addSouth(pager, 3);
    	dock.setWidth("100%");
    	dock.add(resizeLayoutPanel);
	}
	
	public void setRefinedSearchAuxiliarWidget(Panel refinedSearchAuxiliarWidget) {
		this.refinedSearchAuxiliarWidget = refinedSearchAuxiliarWidget;
	}
	

	public void setRefinedSearchAuxiliarWidget(Panel widget, boolean used) {
		this.refinedSearchAuxiliarWidget = widget;
		this.refinedSearchUsed = used;
	}
	
	@Override
	public void prepareAuxiliaryPanel() {
    	refinedSearchAuxiliarWidget.clear();
		refinedSearchAuxiliarWidget.add(new Label("Campos de busca:")); 
		
		FlexTable flexTable = new FlexTable();
		int row = 0;

		 final ListBox dropBoxDate = new ListBox();
		 dropBoxDate.setMultipleSelect(false);
		 	
		 dropBoxDate.addItem("lastUpdateOnSystem");
		 flexTable.setWidget(row,  0, dropBoxDate);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		 row++;
			
		Label yearLabel = new Label("Ano");
	    flexTable.setWidget(row,0, yearLabel);
	    final TextBox yearText = new TextBox();
	    flexTable.setWidget(row,  1, yearText);
	    row++;
	    
	    Label monthLabel = new Label("Mês");
	    flexTable.setWidget(row,0, monthLabel);
	    final TextBox monthText = new TextBox();
	    flexTable.setWidget(row,  1, monthText);
	    row++;
	    
	    Label dayLabel = new Label("Dia");
	    flexTable.setWidget(row,0, dayLabel);
	    final TextBox dayText = new TextBox();
	    flexTable.setWidget(row,  1, dayText);
	    row++;
	    
	    
	    Label tableLabel = new Label("Tabela");
	    flexTable.setWidget(row,0, tableLabel);
	    
	    
	    final ListBox dropBoxTable = new ListBox();
	    dropBoxTable.setMultipleSelect(false);

	    dropBoxTable.addItem("");
	   
		for(TableEnum tableEnum : TableEnum.values()) {
			dropBoxTable.addItem(tableEnum.kind);
		}
		
		flexTable.setWidget(row, 1, dropBoxTable); 
		row++;

		Label historicLabel = new Label("historic_id");
	    flexTable.setWidget(row,0, historicLabel);
	    final TextBox historicText = new TextBox();
	    flexTable.setWidget(row,  1, historicText);
	    row++;
		
		Label userBeforeLabel = new Label("Usuário anterior"); //TODO gravar primeiro usuario no historico e poder pequisar por ele tb
	    flexTable.setWidget(row,0, userBeforeLabel);

		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
	    final ListBox dropBoxUserBefore = new ListBox();
	    dropBoxUserBefore.setMultipleSelect(false);
	    
	    List<Model> users = mapKindToSmallTables.get("users");
	    dropBoxUserBefore.addItem("");
	    for (Model user : users) {
	    	dropBoxUserBefore.addItem(user.get("Name"), user.get("email")); 
		}
		
		flexTable.setWidget(row, 0, dropBoxUserBefore); 
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		
		Label userLabel = new Label("Usuário da mudança"); 
	    flexTable.setWidget(row,0, userLabel);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
	    
	    row++;
	    
		final ListBox dropBoxUserActual = new ListBox();
		dropBoxUserActual.setMultipleSelect(false);

		dropBoxUserActual.addItem(""); 
	    for (Model user : users) {
	    	dropBoxUserActual.addItem(user.get("Name"), user.get("email")); 
		}
		
		flexTable.setWidget(row, 0, dropBoxUserActual); 
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
	    
		refinedSearchAuxiliarWidget.add(flexTable);
		//TODO filter by operation: update or delete
		
		 Button searchButton = new Button("buscar");
		    searchButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					
					MyFilterPredicate yearFilter = new MyFilterPredicate(dropBoxDate.getSelectedValue()+"_year", MyFilterOperator.EQUAL, yearText.getValue());
					MyFilterPredicate monthFilter = new MyFilterPredicate(dropBoxDate.getSelectedValue()+"_month", MyFilterOperator.EQUAL, monthText.getValue());
					MyFilterPredicate dayFilter = new MyFilterPredicate(dropBoxDate.getSelectedValue()+"_day", MyFilterOperator.EQUAL, dayText.getValue());
					 
					FilterInterface grouping = group (yearFilter, MyCompositeFilter.Type.AND, monthFilter);
					FilterInterface dateFilter = group (grouping, MyCompositeFilter.Type.AND, dayFilter);
					
					MyFilterPredicate tableFilterPredicate = new MyFilterPredicate("kind", MyFilterOperator.EQUAL, dropBoxTable.getSelectedValue());

					MyFilterPredicate idFilterPredicate = new MyFilterPredicate("historic_id", MyFilterOperator.EQUAL, historicText.getValue());
					
					MyFilterPredicate userBeforeFilterPredicate = new MyFilterPredicate("user_old", MyFilterOperator.EQUAL, dropBoxUserBefore.getSelectedValue());
					MyFilterPredicate userAfterFilterPredicate = new MyFilterPredicate("user_new", MyFilterOperator.EQUAL, dropBoxUserActual.getSelectedValue());
					
					FilterInterface filterGrouping2 = group (idFilterPredicate, MyCompositeFilter.Type.AND, dateFilter);
					FilterInterface filterGrouping3 = group (filterGrouping2, MyCompositeFilter.Type.AND, tableFilterPredicate);
					FilterInterface filterGrouping4 = group (filterGrouping3, MyCompositeFilter.Type.AND, userBeforeFilterPredicate);
					FilterInterface filterGrouping5 = group (filterGrouping4, MyCompositeFilter.Type.AND, userAfterFilterPredicate);
					
					myService.listItemEqual("historic", null, null, null, Integer.parseInt("10000"), filterGrouping5, null, null, Ajax.call(new HistoricCallback(), HistoricGridView.class + " Auxiliar search")); 	
				}
				
			}
		    ); 
		    refinedSearchAuxiliarWidget.add(searchButton);
		    
		    refinedSearchAuxiliarWidget.setVisible(true);
	}
	
	private class HistoricCallback implements AsyncCallback {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Error getting historic data");
			throw new RuntimeException(caught);
		}

		private List<String> putTogeteherNewAndOldKeys(List<Model> models) {
			HashSet<String> newSet = new HashSet<String>();
			HashSet<String> oldSet = new HashSet<String>();
			for (Model model : models) {
				Map<String, Object> contentValues = model.getContentValues();
				for (String key : contentValues.keySet()) {

					if(key.endsWith("_new")) {
						newSet.add(key);
					}
					else if(key.endsWith("_old")) {
						oldSet.add(key);
					}
				}
			}
			
			ArrayList<String> newModelsKeys = new ArrayList<String>(newSet);
			ArrayList<String> oldModelsKeys = new ArrayList<String>(oldSet);
			
			Collections.sort(newModelsKeys);
			Collections.sort(oldModelsKeys);
			
			ArrayList<String> resultKeys = new ArrayList<String>((newModelsKeys.size() * 2)+2);
			resultKeys.add("historic_id");
			resultKeys.add("lastUpdateOnSystem");
			resultKeys.add("type");
			resultKeys.add("kind");
			
			for(int i=0; i<newModelsKeys.size(); i++) {
					resultKeys.add(newModelsKeys.get(i));
					resultKeys.add(oldModelsKeys.get(i));
			}
			
			return resultKeys;
		}
		
		@Override
		public void onSuccess(Object result) {
			if (result != null && result instanceof List<?>) {
					List<Model> models = (List<Model>) result;
					
					dataList.clear();
			    	if(dataList != null) {
			    		dataList.addAll(models); 
			    	}
			        dataProvider.setList(dataList);

					sortHandler = new ListHandler<Model>(dataProvider.getList());
			        dataGrid.addColumnSortHandler(sortHandler);
			        
			    	for(int i=dataGrid.getColumnCount() -1; i >= 0; i--) {
			    		dataGrid.removeColumn(i);
			    	}

			    	if(models.size() > 0) {
						List<String> keys = putTogeteherNewAndOldKeys(models);
						for (final String key : keys) {
							Column<Model, String> tableColumn = new Column<Model, String>(new TextCell()) {
								@Override
								public String getValue(Model object) {
									return object.get(key);
								}
							};
					    	dataGrid.addColumn(tableColumn, key); 
					    	tableColumn.setSortable(true);
							sortHandler.setComparator(tableColumn, new Comparator<Model>() {
								public int compare(Model o1, Model o2) {
									return ComparatorUtils.nullSafeStringComparator(o1.get(key), o2.get(key));
								}
							})
							;
	
							dataGrid.setColumnWidth(tableColumn, 15, Unit.EM);
						}
			    	}
			        dataProvider.refresh();
			}
		}
	}
	
	private FilterInterface group(FilterInterface f1, MyCompositeFilter.Type type, FilterInterface f2) {
		if(isEmpty(f1) && isEmpty(f2) ) {
			return null;
		}
		else if(isEmpty(f1) && !isEmpty(f2) ) {
			return f2;
		}
		else if(!isEmpty(f1) && isEmpty(f2) ) {
			return f1;
		}
		else {
			return new MyCompositeFilter(f1, type, f2);
		}
	}
	
	private boolean isEmpty(FilterInterface f1) {
		return f1 == null || (f1 instanceof MyFilterPredicate && ((MyFilterPredicate) f1).isValueEmpty());
	}

	public void setMapKindToSmallTables(
			Map<String, List<Model>> mapKindToSmallTables) {
		this.mapKindToSmallTables = mapKindToSmallTables;
		
	}
}
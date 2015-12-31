package br.fapema.morholt.web.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.Resources;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import br.fapema.morholt.web.client.gui.grid.DataGridResource;
import br.fapema.morholt.web.client.gui.model.Model;

import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.cellview.client.DataGrid;
/**
 * Abstract PaggingDataGrid class to set initial GWT DataGrid and Simple Pager with ListDataProvider
 *
 * @author Ravi Soni
 *
 * @param <T>
 */
public abstract class PagingDataGrid<T> extends Composite {
 
	private static final Logger log = Logger.getLogger(PagingDataGrid.class.getName()); 

    protected DataGrid<T> dataGrid;
    private MySimplePager<T> pager;
    private String height;
    private ListDataProvider<T> dataProvider;
    
    
    private List<T> dataList;
    private DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
    private List<DataSource> dataSources;
   protected  ListHandler<T> sortHandler;

	private String startCursor;
	private int limit;
	
    public PagingDataGrid(List<DataSource> dataSources, String startCursor, int limit) {
        this.dataSources = dataSources;
        this.startCursor = startCursor;
        this.limit = limit;
        
    	initWidget(dock);
    	dock.addStyleDependentName("gridDockPanel");
    	
    	SimpleLayoutPanel simpleLayoutPanel = new SimpleLayoutPanel();
    	
    	DataGridResource.INSTANCE.dataGridStyle().ensureInjected();
    	
    	ProvidesKey<T> keyProvider = new ProvidesKey<T>() {
  	      public Object getKey(T item) {
  	        return (item == null || !(item instanceof Model)) ? null : ((Model)item).getKeyValue();
  	      }
  	    };
  	    
        dataGrid = new DataGrid<T>(20, DataGridResource.INSTANCE, keyProvider);
        simpleLayoutPanel.add(dataGrid);
        dataGrid.setWidth("100%");
        dataGrid.setStylePrimaryName("dataGridList"); 
        
        MySimplePager.Resources pagerResources = GWT.create(MySimplePager.Resources.class);
        pager = new MySimplePager(TextLocation.CENTER, pagerResources, false, 0,
                true);
        pager.setDisplay(dataGrid);
        pager.setStylePrimaryName("dataGridPager");
        dataProvider = new ListDataProvider<T>();
        dataList = new ArrayList<T>(); 
        dataProvider.setList(dataList);

        dataGrid.setEmptyTableWidget(new HTML("Nenhum dado encontrado")); //TODO
 
    //    ColumnSortEvent.AsyncHandler sortHandler= new ColumnSortEvent.AsyncHandler(dataGrid);
        
        sortHandler = new ListHandler<T>(dataProvider.getList());
        
        initTableColumns(dataGrid, sortHandler, dataSources);
        

        dataGrid.addColumnSortHandler(sortHandler);
        
       // dataGrid.getColumnSortList().push(new ColumnSortList.ColumnSortInfo(column, true));

        dataProvider.addDataDisplay(dataGrid);
        pager.setVisible(true);
        pager.setWidth("100%");
        dataGrid.setVisible(true);
        dock.addSouth(pager, 3);
        dock.add(simpleLayoutPanel);
        dock.setWidth("100%");
        
    }

    public String getStartCursor() {
    	return startCursor;
    }
    public int getLimit() {
    	return limit;
    }

    

	 
	 
	 
	public class MySimplePager<T> extends SimplePager {

		public MySimplePager() {
			this.setRangeLimited(true);
		}

		@Override
		protected String createText() {
			NumberFormat formatter = NumberFormat.getFormat("#,###");
			HasRows display = getDisplay();
			Range range = display.getVisibleRange();
			int pageStart = range.getStart() + 1;
			int pageSize = range.getLength();
			int dataSize = display.getRowCount();
			int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
			endIndex = Math.max(pageStart, endIndex);
			boolean exact = display.isRowCountExact();
			return formatter.format(pageStart) + "-"
					+ formatter.format(endIndex)
					+ (exact ? " de " : " de mais de ")
					+ formatter.format(dataSize); // TODO localize
		}

		 
		public MySimplePager(TextLocation location, Resources resources,
				boolean showFastForwardButton, int fastForwardRows,
				boolean showLastPageButton) {
			super(location, resources, showFastForwardButton, fastForwardRows,
					showLastPageButton);
			this.setRangeLimited(true);
		}

		public void setPageStart(int index) {

			if (this.getDisplay() != null) {
				Range range = getDisplay().getVisibleRange();
				int pageSize = range.getLength();
				if (!isRangeLimited() && getDisplay().isRowCountExact()) {
					index = Math.min(index, getDisplay().getRowCount()
							- pageSize);
				}
				index = Math.max(0, index);
				if (index != range.getStart()) {
					getDisplay().setVisibleRange(index, pageSize);
				}
			}

		}
	}
    
    public SingleSelectionModel<T> getSelectionModel() {
    	return (SingleSelectionModel<T>) dataGrid.getSelectionModel();
    }
 

    public void setEmptyTableWidget() {
        dataGrid.setEmptyTableWidget(new HTML(
                "The current request has taken longer than the allowed time limit. Please try your report query again."));
    }
 
    /**
     *
     * Abstract Method to implements for adding Column into Grid
     *
     * @param dataGrid
     * @param sortHandler
     */
    public abstract void initTableColumns(DataGrid<T> dataGrid,   ListHandler<T> sortHandler, List<DataSource> dataSources);
 
    public String getHeight() {
        return height;
    }
 
    public void setHeight(String height) {
        super.setHeight(height);
    	this.height = height;
        dataGrid.setHeight("100%");
    }
 
    public List<T> getDataList() {
        return dataList;
    }
    
    public void initializeGridSelection() {
    	if(dataList.isEmpty()) {
    		dataGrid.setSelectionModel(null);
    		return;
    	}
    	int keyboardSelectedRow = dataGrid.getKeyboardSelectedRow();
    	if(keyboardSelectedRow == -1 || keyboardSelectedRow >= dataList.size()) {
    		dataGrid.setKeyboardSelectedRow(0);
    		keyboardSelectedRow = 0;
    	}
    	
    	
    	ProvidesKey<T> keyProvider = new ProvidesKey<T>() {
    	      public Object getKey(T item) {
    	        // Always do a null check.
    	//    	Window.alert(    ((Model)item).getKeyName());  
    	        return (item == null || !(item instanceof Model)) ? null : ((Model)item).getKeyValue();
    	      }
    	    };
    	    
  		final SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>(keyProvider);
  		
  		
  	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
  	        public void onSelectionChange(SelectionChangeEvent event) {
  	        	//super.o
  	          Model selected = (Model)selectionModel.getSelectedObject();
  	          if (selected != null) {
//  	            Window.alert("You selected("+selected+ "): " + selected.getKeyValue());
  	          }
  	        }
  	      });
  	    

  		dataGrid.setSelectionModel(selectionModel, 
                DefaultSelectionEventManager.<T>createDefaultManager() );
    	
  		selectionModel.setSelected(dataList.get(keyboardSelectedRow), true);
  		
    }
    
    public void setDoubleClickHandler(DoubleClickHandler doubleClickHandler) {
    	dataGrid.addDomHandler(doubleClickHandler, DoubleClickEvent.getType());
    }
 
    public void setDataList(List<T> dataList) {
    	this.dataList = new ArrayList<T>();
    	if(dataList != null) {
    		this.dataList.addAll(dataList);
    	}
    	
    	List<T> dataProviderList = dataProvider.getList();
    	dataProviderList.clear();
    	dataProviderList.addAll(dataList);
    	
        initializeGridSelection();

        dataProvider.refresh();
    }
 
    public ListDataProvider<T> getDataProvider() {
        return dataProvider;
    }
 
    /*
    public void setDataProvider(ListDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }*/
}
package br.fapema.morholt.web.client.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;

import br.fapema.morholt.web.client.gui.grid.GridView;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.shared.util.ComparatorUtils;

import com.google.gwt.user.cellview.client.DataGrid;

public class MyPaginationDataGrid extends PagingDataGrid<Model>{
     
	private static final Logger log = Logger.getLogger(MyPaginationDataGrid.class.getName()); 

	private GridView gridView;
    public MyPaginationDataGrid(List<DataSource> dataSources, GridView gridView) {
		super(dataSources, null, 0); 
		this.setDataList(new ArrayList<Model>());
		this.gridView = gridView;

		if(gridView.getParentColumn() != null)
			addParentNameColumn(dataGrid, sortHandler);
	}

	@Override
	public void initTableColumns(DataGrid<Model> dataGrid, ListHandler<Model> sortHandler,
			List<DataSource> dataSources) {
		log.info(MyPaginationDataGrid.class.getCanonicalName() + ".initTableColumns");
		
		
		for (final DataSource dataSource : dataSources) {
			addColumn(dataGrid, sortHandler, dataSource);
		}
	}

	private void addParentNameColumn(DataGrid<Model> dataGrid, ListHandler<Model> sortHandler) {
		DataSource projectNameDataSource = new DataSource(DataSourceTypeEnum.Text, gridView.getParentColumn(), gridView.getParentColumnLabel(), true, false, false);
		addColumn(dataGrid, sortHandler, projectNameDataSource);
		
		Column<Model, String> column = createColumn(sortHandler, projectNameDataSource);
		dataGrid.insertColumn(0, column, projectNameDataSource.getLabel());
		dataGrid.setColumnWidth(column, 15, Unit.EM);
	}

	private void addColumn(DataGrid<Model> dataGrid, ListHandler<Model> sortHandler, final DataSource dataSource) {
		log.info("" + dataSource);
		if (!dataSource.isShowOnList())
			return;
		
		Column<Model, String> column = createColumn(sortHandler, dataSource);

		dataGrid.addColumn(column, dataSource.getLabel());
		dataGrid.setColumnWidth(column, 15, Unit.EM);
	}

	private Column<Model, String> createColumn(ListHandler<Model> sortHandler, final DataSource dataSource) {
		Column<Model, String> column;
		column = new Column<Model, String>(new TextCell()) {
			@Override
			public String getValue(Model model) {
				if(DataSourceTypeEnum.Checkbox.equals(dataSource.getType())) {
					return model.getBoolean(dataSource.getName()).equals(Boolean.TRUE) ? "sim" : "não"; 
				}
				return model.get(dataSource.getName());
			}
		};
		column.setSortable(true);

		sortHandler.setComparator(column, new Comparator<Model>() {
			public int compare(Model o1, Model o2) {
				return ComparatorUtils.nullSafeStringComparator(o1.get(dataSource.getName()),
						o2.get(dataSource.getName()));
			}
		});
		return column;
	}

	public void remove(Model model) {
		log.info("model k: " + model.getKeyValue());
		for (Iterator<Model> iterator = getDataList().iterator(); iterator.hasNext();) {
			Model amodel =  iterator.next();
			log.info("amodel k: " + amodel.getKeyValue());
			if(model.equals(amodel)) {
				log.info("model removed from dataList");
				iterator.remove();
				break;
			}
		}
	}
	
	
}
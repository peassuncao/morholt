package br.fapema.morholt.web.client.gui.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.DataSourceTypeEnum;
import br.fapema.morholt.web.client.gui.MapView;
import br.fapema.morholt.web.client.gui.grid.enter.EnterContent;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.FilterInterface;
import br.fapema.morholt.web.shared.MyCompositeFilter;
import br.fapema.morholt.web.shared.MyFilterPredicate;
import br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;
import br.fapema.morholt.web.shared.util.DateHelper;
import br.fapema.morholt.web.shared.util.StringUtils;

public class FilterHelper {


	private static final Logger log = Logger.getLogger(FilterHelper.class.getName());
	
	private int row;
	private List<DataSource> dataSources;
	private TextBox text2;
	private ListBox dropBoxAdditional2;
	private Boolean featured;

	private ListBox dropBoxOrderDirection;
	private ListBox dropBoxLimit;

	private Panel horizontalPanel;
	private Button mapButton;

	private MapView mapView;
	private Button exportButton;
	private DateBox dateBoxFrom;
	private DateBox dateBoxTo;
	private CheckBox featureCheckBox;
	private MyServiceClientImpl myService;

	private MyFilterPredicate equalityMyFilterPredicateChosenProject;
	
	public void prepareSearchAuxiliar(final Panel refinedSearchAuxiliarWidget,
			final List<DataSource> dataSources, final GridView grid,
			final String chosenProject, Profile profile, String kind, String parentColumn, String parentColumnLabel, MyServiceClientImpl myService) {
		this.dataSources = obtainSortedDataSources(dataSources);
		this.myService = myService;
		refinedSearchAuxiliarWidget.clear();
		refinedSearchAuxiliarWidget.add(new Label("Pesquisa:"));

		FlexTable flexTable = createFilterTable(dataSources, chosenProject, parentColumn, parentColumnLabel);
		refinedSearchAuxiliarWidget.add(flexTable);

		horizontalPanel = createButtonsHorizontalPanel(refinedSearchAuxiliarWidget, dataSources, grid, chosenProject, profile, kind);
		refinedSearchAuxiliarWidget.add(horizontalPanel);
	}
	
	

	public FilterInterface obtainFilterInterface(final String chosenProject) {
	//	MyFilterPredicate projectFilter = new MyFilterPredicate(
	//			"project_name", MyFilterOperator.EQUAL, chosenProject);
		
		MyFilterPredicate featureFilter = null;
		if(featureCheckBox.getValue() == true)
			featureFilter = new MyFilterPredicate(
					Model.FEATURED, MyFilterOperator.EQUAL, featureCheckBox.getValue());
		
		MyFilterPredicate fromFilter = new MyFilterPredicate(
				"data", MyFilterOperator.GREATER_EQUAL, //TODO fixed data
				DateHelper.convertDate(dateBoxFrom.getValue(), "yyyy/MM/dd"));
		
		MyFilterPredicate toFilter = new MyFilterPredicate(
				"data", MyFilterOperator.LESS_EQUAL, //TODO fixed data
				DateHelper.convertDate(dateBoxTo.getValue(), "yyyy/MM/dd"));
		
		FilterInterface dateFilter = group(fromFilter,
				MyCompositeFilter.Type.AND, toFilter);

		FilterInterface projectDateFilter = group(equalityMyFilterPredicateChosenProject,
				MyCompositeFilter.Type.AND, dateFilter);

		FilterInterface comboFilter = equalityMyFilterPredicate1;
		FilterInterface projectPlusComboFilter = group(projectDateFilter,
				MyCompositeFilter.Type.AND, comboFilter);
		
		FilterInterface oneMore = group(projectPlusComboFilter,
				MyCompositeFilter.Type.AND, equalityMyFilterPredicate1);
		FilterInterface filter1 = group(oneMore,
				MyCompositeFilter.Type.AND, equalityMyFilterPredicate2);
		FilterInterface filter2 = group(filter1,
				MyCompositeFilter.Type.AND, equalityMyFilterPredicate3);
		FilterInterface filter3= group(filter2,
				MyCompositeFilter.Type.AND, equalityMyFilterPredicate4);
		FilterInterface filter4= group(filter3,
				MyCompositeFilter.Type.AND, equalityMyFilterPredicate5);
		
		FilterInterface resultFilter = group(filter4, MyCompositeFilter.Type.AND, featureFilter);
		
		sort = new ArrayList<String>();
		if(!isEmpty(equalityMyFilterPredicate1) &&equalityMyFilterPredicate1.getType().equals(MyFilterOperator.NOT_EQUAL))
			sort.add(equalityMyFilterPredicate1.getName());
		else if(!isEmpty(fromFilter) || !isEmpty(toFilter))
			sort.add("data"); 
		else if(!isEmpty(equalityMyFilterPredicate1)) 
			sort.add(equalityMyFilterPredicate1.getName());
		log.info("sort: " + sort); //FIXME log
		return resultFilter;
	}
	
	

	private List<DataSource> obtainSortedDataSources(final List<DataSource> dataSources) {
		ArrayList<DataSource> sortedDataSources = new ArrayList<DataSource>();
		sortedDataSources.addAll(dataSources);
		Collections.sort(sortedDataSources);
		return sortedDataSources;
	}


	private FlexTable createFilterTable(final List<DataSource> dataSources, String chosenProject, String parentColumn, String parentColumnLabel) {
		FlexTable flexTable = new FlexTable();
		row = 0;

		addDateInequality(flexTable, "Filtrar por data"); 
		createSort(flexTable, dataSources);
		addFeatureCheckbox(flexTable, "Apenas destaques");
		dropBoxAdditional2 = new ListBox();
		

		equalityMyFilterPredicateChosenProject = new MyFilterPredicate();
		equalityMyFilterPredicate1 = new MyFilterPredicate();
		equalityMyFilterPredicate2 = new MyFilterPredicate();
		equalityMyFilterPredicate3 = new MyFilterPredicate();
		equalityMyFilterPredicate4 = new MyFilterPredicate();
		equalityMyFilterPredicate5 = new MyFilterPredicate();
		addSelectedProjectEquality(chosenProject, flexTable, parentColumnLabel, equalityMyFilterPredicateChosenProject, parentColumn);
		addEquality(flexTable, "Filtro 1:", dropBoxAdditional2, equalityMyFilterPredicate1);
		addEquality(flexTable, "Filtro 2:", new ListBox(), equalityMyFilterPredicate2);
		addEquality(flexTable, "Filtro 3:", new ListBox(), equalityMyFilterPredicate3);
		addEquality(flexTable, "Filtro 4:", new ListBox(), equalityMyFilterPredicate4);
		addEquality(flexTable, "Filtro 5:", new ListBox(), equalityMyFilterPredicate5);
		addLimit(flexTable); // TODO allow admin to set biggest limit allowed for each user
		return flexTable;
	}

	private void addSelectedProjectEquality(String chosenProject, final FlexTable flexTable, String label, final MyFilterPredicate equalityMyFilterPredicate, final String parentColumn) {
		Label labelAdditional1 = new Label(label);
		flexTable.setWidget(row, 0, labelAdditional1);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		
		final TextBox textBox = new TextBox();
		textBox.setValue(chosenProject);
		textBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				equalityMyFilterPredicate.initialize(parentColumn, MyFilterOperator.EQUAL, textBox.getValue());
			}
		});
		flexTable.setWidget(row, 0, textBox);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), textBox);
		row++;
	}


	private HorizontalPanel createButtonsHorizontalPanel(final Panel refinedSearchAuxiliarWidget,
			final List<DataSource> dataSources, final GridView grid, final String chosenProject, Profile profile,
			String kind) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Button searchButton = createSearchButton(grid, chosenProject);
		horizontalPanel.add(searchButton);

		exportButton = createExportButton(grid, chosenProject);
		if (profile.authenticate(kind, Profile.CRUDEL.EXPORT)) {
			horizontalPanel.add(exportButton);
		}

		mapButton = createMapButton(refinedSearchAuxiliarWidget, dataSources, grid);
		horizontalPanel.add(mapButton);
		return horizontalPanel;
	}
	
	


	private Button createMapButton(final Panel refinedSearchAuxiliarWidget, final List<DataSource> dataSources,
			final GridView grid) {
		Button mapButton = new Button("mapa");
		mapButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean sensor = true;

				// load all the libs for use in the maps
				ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
				loadLibraries.add(LoadLibrary.DRAWING);
				loadLibraries.add(LoadLibrary.GEOMETRY);
				loadLibraries.add(LoadLibrary.VISUALIZATION);

				Runnable onLoad = new Runnable() {

					@Override
					public void run() {
						mapView = new MapView(grid.getCurrentList(),
								dataSources, Window.getClientWidth()
										- grid.getAbsoluteLeft() - 5, Window
										.getClientHeight()
										- grid.getAbsoluteTop() - 5, myService);
						mapView.setRefinedSearchAuxiliarWidget(refinedSearchAuxiliarWidget);
						EnterContent parent= (EnterContent) grid
								.getParent().getParent().getParent();
						Widget nextWidget = parent.getWidget(0);
						if (nextWidget instanceof MapView) {
							parent.remove(0);
							nextWidget = null;
							parent.insert(mapView, "mapa", 0);
						} else
							parent.insert(mapView, "mapa", 0);
						parent.selectTab(0);
					}
				};
				LoadApi.go(onLoad, loadLibraries, sensor);

			}
		});
		return mapButton;
	}

	private List<String>sort;
	private Button createSearchButton(final GridView grid,
			final String chosenProject) {
		Button searchButton = new Button("buscar");
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FilterInterface filterInterface = obtainFilterInterface(chosenProject);
				List<String> sortDirection = obtainSortDirection();
				grid.list(filterInterface, sort, sortDirection,
						dropBoxLimit.getSelectedValue());
			}
		});
		return searchButton;
	}
	
	private Button createExportButton(final GridView grid,
			final String chosenProject) {
		Button exportButton = new Button("exportar");
		exportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ExportPopupPanel exportPopupPanel = new ExportPopupPanel(grid, FilterHelper.this, chosenProject);
			      exportPopupPanel.show();
			}
		});
		return exportButton;
	}
	
	List<String> obtainSortDirection() {
		List<String> sortDirection = new ArrayList<String>();
		sortDirection.add(dropBoxOrderDirection.getSelectedValue());
		return sortDirection;
	}

	

	private void addLimit(FlexTable flexTable) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label labelGroup1 = new Label("Max resultados");

		dropBoxLimit = new ListBox();
		dropBoxLimit.setMultipleSelect(false);

		dropBoxLimit.addItem("100");
		dropBoxLimit.addItem("500");
		dropBoxLimit.addItem("1000");
		dropBoxLimit.addItem("2000");
		dropBoxLimit.addItem("5000");

		horizontalPanel.add(labelGroup1);
		horizontalPanel.add(dropBoxLimit);
		labelGroup1.addStyleName("marginRight1");

		flexTable.setWidget(row, 0, horizontalPanel);

		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
	}

	private void createSort(FlexTable flexTable, List<DataSource> dataSources) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		dropBoxOrderDirection = new ListBox();
		dropBoxOrderDirection.setMultipleSelect(false);
		dropBoxOrderDirection.addItem("Desc", "DESCENDING");
		dropBoxOrderDirection.addItem("Asc", "ASCENDING");

		horizontalPanel.add(dropBoxOrderDirection);

		flexTable.setWidget(row, 0, horizontalPanel);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
	}

	private FilterInterface group(FilterInterface f1, MyCompositeFilter.Type type,
			FilterInterface f2) {
		if (isEmpty(f1) && isEmpty(f2)) {
			return null;
		} else if (isEmpty(f1) && !isEmpty(f2)) {
			return f2;
		} else if (!isEmpty(f1) && isEmpty(f2)) {
			return f1;
		} else {
			return new MyCompositeFilter(f1, type, f2);
		}
	}

	private boolean isEmpty(FilterInterface f1) {
		return f1 == null
				|| (f1 instanceof MyFilterPredicate && ((MyFilterPredicate) f1)
						.isValueEmpty());
	}

	private void addToTable(FlexTable flexTable, Widget widget, int row, int column, int colspan) {
		flexTable.setWidget(row, column, widget);
		flexTable.getFlexCellFormatter().setColSpan(row, column, 2);
	}
	
	private DateBox createDateBox() {
		DefaultFormat format = new DateBox.DefaultFormat 
				 (DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM)); 
		
		DateBox dateBox = new DateBox();
		dateBox.setFormat(format);
		return dateBox;
	}
	
	private HorizontalPanel createHorizontalPanel(Widget ...widgets) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		for (Widget widget : widgets) {
			horizontalPanel.add(widget);
		}
		horizontalPanel.setSpacing(5);
		return horizontalPanel;
	}
	
	private void addDateInequality(FlexTable flexTable, String label) {
		Label labelDateInequality = createLabel(label);
		addToTable(flexTable, labelDateInequality, row, 0, 2);
		row++;

		dateBoxFrom = createDateBox();
		Label labelAnd = createLabel("e");
		HorizontalPanel date1HorizontalPanel = createHorizontalPanel(dateBoxFrom, labelAnd);
		addToTable(flexTable, date1HorizontalPanel, row, 0, 2);
		row++;
		
		dateBoxTo = createDateBox();
		HorizontalPanel date2HorizontalPanel = createHorizontalPanel(dateBoxTo);
		addToTable(flexTable, date2HorizontalPanel, row, 0, 2);
		row++;
	}


	private Label createLabel(String label) {
		Label labelAdditional1 = new Label(label);
		labelAdditional1.addStyleName("paddingTop");
		return labelAdditional1;
	}
	
	private void addFeatureCheckbox(FlexTable flexTable, String label) {
		featureCheckBox = new CheckBox(label);
		//featureCheckBox.setValue(false);
		featureCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
			//	featured = featureCheckBox.getValue();
			}
		});
		flexTable.setWidget(row,  0, featureCheckBox);
		row++;
	}

	private MyFilterPredicate equalityMyFilterPredicate1;
	private MyFilterPredicate equalityMyFilterPredicate2;
	private MyFilterPredicate equalityMyFilterPredicate3;
	private MyFilterPredicate equalityMyFilterPredicate4;
	private MyFilterPredicate equalityMyFilterPredicate5;
	
	private void addEquality(final FlexTable flexTable, String label, final ListBox dropBox, final MyFilterPredicate equalityMyFilterPredicate) {
		Label labelAdditional1 = new Label(label);
		flexTable.setWidget(row, 0, labelAdditional1);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;

		dataSources.add(new DataSource(DataSourceTypeEnum.Text, "cidade", "cidade", false, false, true)); // TODO fixed data
		dropBox.addItem("");
		
		for (DataSource dataSource : dataSources) {
			if(Model.FEATURED.equals(dataSource.getName()) || "data".equals(dataSource.getName())) continue; //TODO fixed data
			if(!dataSource.getType().equals(DataSourceTypeEnum.ImageBoolean) && !dataSource.isFilterBy()) continue;
			if(dataSource.getType().equals(DataSourceTypeEnum.Image))continue;
			if(dataSource.getType().equals(DataSourceTypeEnum.ImageBoolean)) {
				String name = dataSource.getName();
				dropBox.addItem(name.replace("Boolean",""), dataSource.getName());
			}
			else
				dropBox.addItem(dataSource.getName(), dataSource.getName());
		}


		flexTable.setWidget(row, 0, dropBox);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		final int changeRow = row;
		dropBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				final String selectedValue = dropBox.getSelectedValue();
				DataSource dataSource = getDataSource(selectedValue);
				if(dataSource == null) {
					flexTable.setWidget(changeRow, 0, null);
					equalityMyFilterPredicate.initialize(null, null, true);
					return;
				}
				switch(dataSource.getType()) {
					case Text:
					addText(flexTable, equalityMyFilterPredicate, changeRow, selectedValue);
						break;
					case Combobox:	
					addCombobox(flexTable, equalityMyFilterPredicate, changeRow, selectedValue, dataSource);
						break;
					case Checkbox:
						final CheckBox checkBox = new CheckBox(dataSource.getLabel());
						checkBox.setValue(false);
						checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								equalityMyFilterPredicate.initialize(selectedValue, MyFilterOperator.EQUAL, checkBox.getValue());
							}
						});
						flexTable.setWidget(changeRow, 0, checkBox);
						flexTable.getFlexCellFormatter().setColSpan(changeRow, 0, 2);
						DomEvent.fireNativeEvent(Document.get().createChangeEvent(), checkBox);
					break;
					case Image:
						// see ImageBoolean
						break;
					case ImageBoolean:	
					//	final CheckBox imageCheckBox = new CheckBox(dataSource.getLabel());
					//	imageCheckBox.setValue(false);
					//	imageCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					//		@Override
					//		public void onValueChange(ValueChangeEvent<Boolean> event) {
								String value = null;
					//			if(imageCheckBox.getValue()==true)
									equalityMyFilterPredicate.initialize(selectedValue, MyFilterOperator.EQUAL, true);
					//		}
					//	});
						flexTable.setWidget(changeRow, 0, null);
						flexTable.getFlexCellFormatter().setColSpan(changeRow, 0, 2);
						break;
						
					default:
						Window.alert("type:" + dataSource.getType()); //FIXME remove
				}
			}

			private void addCombobox(final FlexTable flexTable, final MyFilterPredicate equalityMyFilterPredicate,
					final int changeRow, final String selectedValue, DataSource dataSource) {
				final ListBox listBox = new ListBox();
				listBox.setMultipleSelect(false);
				for (String value : dataSource.getComboValues()) {
					listBox.addItem(value);
				}
				listBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						equalityMyFilterPredicate.initialize(selectedValue, MyFilterOperator.EQUAL, listBox.getSelectedValue());
					}
				});
				flexTable.setWidget(changeRow, 0, listBox);
				flexTable.getFlexCellFormatter().setColSpan(changeRow, 0, 2);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
			}

			private void addText(final FlexTable flexTable, final MyFilterPredicate equalityMyFilterPredicate,
					final int changeRow, final String selectedValue) {
				final TextBox textBox = new TextBox();
				textBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						equalityMyFilterPredicate.initialize(selectedValue, MyFilterOperator.EQUAL, textBox.getValue());
					}
				});
				flexTable.setWidget(changeRow, 0, textBox);
				flexTable.getFlexCellFormatter().setColSpan(changeRow, 0, 2);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), textBox);
			}
			
			private DataSource getDataSource(String name) {
				if(StringUtils.isEmpty(name)) return null;
				for(DataSource dataSource : dataSources) {
					if(name.equals(dataSource.getName())) {
						return dataSource;
					}
				}
				throw new IllegalArgumentException("name: " + name);
			}
		});
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), dropBox);
		row++;
	}



	public List<String> obtainSort() {
		return sort;
	}
}

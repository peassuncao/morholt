package br.fapema.morholt.web.client.gui.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import br.fapema.morholt.web.client.gui.DataSource;
import br.fapema.morholt.web.client.gui.DataSourceTypeEnum;
import br.fapema.morholt.web.client.gui.basic.MyDateBox;
import br.fapema.morholt.web.client.gui.basic.MyDialogBox;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.BlobURL;
import br.fapema.morholt.web.shared.MyFilterPredicate;
import br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;
import br.fapema.morholt.web.shared.TableEnum;
import br.fapema.morholt.web.shared.util.DateHelper;
import br.fapema.morholt.web.shared.util.StringUtils;

public class FlexTableHelper {
	private static final Logger log = Logger.getLogger(FlexTableHelper.class.getName());
	
	// A panel where the thumbnails of uploaded images will be shown
	 private static FlowPanel panelImages = new FlowPanel(); 
	 
	 private final static  int TABLE_BORDER = 0;
	 
	public static FlexTable createTable(int width, String title, Model selectedModel,  List<DataSource> dataSources, int numberOfDetailsColumns, FlexTableEnum flexTableEnum, Map<String, List<Model>> mapKindToSmallTables, Profile profile, MyServiceClientImpl myService) {
		if(numberOfDetailsColumns != 1 && numberOfDetailsColumns != 2) throw new RuntimeException("Number of columns of table can only be 1 or 2");
		final FlexTable flexTable = new FlexTable();
	    flexTable.setBorderWidth(TABLE_BORDER); //FIXME
	   // flexTable.setWidth(width-(TABLE_BORDER*2)+"px"); //FIXME
	    flexTable.setCellSpacing(0);
	    flexTable.setCellPadding(1);
	  //  flexTable.addStyleName("table");
		flexTable.setWidth("100%");
	    flexTable.setTitle(title);
	    
	    flexTable.getColumnFormatter().addStyleName(0, "flexTableLabelColumnFormatter");
	    flexTable.getColumnFormatter().addStyleName(1, "flexTableValueColumnFormatter");
	    flexTable.getColumnFormatter().addStyleName(2, "flexTableLabelColumnFormatter");
	    flexTable.getColumnFormatter().addStyleName(3, "flexTableValueColumnFormatter");
		
		addDataToTable(selectedModel, dataSources, numberOfDetailsColumns,
				flexTableEnum, flexTable, mapKindToSmallTables, profile, myService, width);
		
		
		HTMLTable.RowFormatter rf = flexTable.getRowFormatter();
		for (int i=0; i<dataSources.size()/2;i++) {
			 rf.addStyleName(i, "FlexTable-Row");
		}
		
		
		return flexTable;
	}

	public static void addDataToTable(Model selectedModel,
			List<DataSource> dataSources, int numberOfDetailsColumns,
			FlexTableEnum flexTableEnum, final FlexTable flexTable, Map<String, List<Model>> mapKindToSmallTables, Profile profile, MyServiceClientImpl myService,  int width) {
		
		Map<String, Object> contentValues = removePrivateColumns(selectedModel.getContentValues());
		int dataSourcesSize = obtainDataSourcesSizeCountingDependents(dataSources);
		
		int halfSize = (dataSourcesSize / numberOfDetailsColumns) + (dataSourcesSize % numberOfDetailsColumns);
		
		
		
	
		List<MyFlexTableCell> cells = null;
		if(numberOfDetailsColumns == 2) {
			cells = obtainCells(dataSources, flexTableEnum, contentValues, dataSourcesSize);
			for (MyFlexTableCell c : cells) {
				int i=0;
				addNameValueToTable(flexTable, c.name, c.value, c.dependentValue, c.row, c.column, flexTableEnum, c.dataSource, selectedModel, mapKindToSmallTables, numberOfDetailsColumns, profile, myService, width, c.colspan);
				//FIXME datasource dependent + 1
				i++;
			}
		}
		
		else {
		
		
		
		addNameValuesToColumn(0, selectedModel, dataSources, numberOfDetailsColumns, flexTableEnum, flexTable,
				mapKindToSmallTables, contentValues, 0, halfSize, profile, myService, width);
		
		addNameValuesToColumn(2, selectedModel, dataSources, numberOfDetailsColumns, flexTableEnum, flexTable,
				mapKindToSmallTables, contentValues, halfSize, dataSourcesSize, profile, myService, width);
		}
		
		//addSpaceColumn(2, dataSourcesSize/numberOfDetailsColumns, numberOfDetailsColumns>1, flexTable);
	}

	/**
	 * each cell contains a pair name/value, so in fact two columns
	 * @param dataSources
	 * @param flexTableEnum
	 * @param contentValues
	 * @param dataSourcesSize
	 * @return cells <MyFlexTableCell>
	 */
	private static List<MyFlexTableCell> obtainCells(List<DataSource> dataSources, 
			FlexTableEnum flexTableEnum, Map<String, Object> contentValues, int dataSourcesSize) {
		List<MyFlexTableCell> cells;
		cells = new ArrayList<MyFlexTableCell>(); ;
		int row = 0;
		int column = 0;
		int rowNeedingToBeFilled = 0;
		boolean thereIsARowNeedingToBeFilled = false;
		
		for (int i = 0; i < dataSources.size(); i++) {
			DataSource dataSource = dataSources.get(i);
			String name = (String) dataSource.getName();
			Object value =  contentValues.get(name);
			
			Object dependentValue = null;
			if(dataSource.hasDependent()) 
				dependentValue = contentValues.get( (String) dataSource.getDependent().getName());
			int colspan;
			MyFlexTableCell cell;
			if(value instanceof String  && ((String)value).length() > 15) {
				colspan = 3; //FIXME use this on text..;
				
				if(column == 2) {
					column = 0;
					cell = new MyFlexTableCell(column, row+1, colspan, flexTableEnum, name, value, dependentValue, dataSource);
					thereIsARowNeedingToBeFilled = true;
					rowNeedingToBeFilled = row;
				}
				else {
					cell = new MyFlexTableCell(0, row, colspan, flexTableEnum, name, value, dependentValue, dataSource);
				}
				row++;
			}
			else {
				colspan = 1;
				if(thereIsARowNeedingToBeFilled) {
					column = 2;
					cell = new MyFlexTableCell(2, rowNeedingToBeFilled, colspan, flexTableEnum, name, value, dependentValue, dataSource);
					thereIsARowNeedingToBeFilled = false;
				}
				else {
					
					cell = new MyFlexTableCell(column, row, colspan, flexTableEnum, name, value, dependentValue, dataSource);
					
				}
				column+=2;  // each row is: label value label value or if colspan=2, label value
				if(column > 2) {
					column = 0;
					row++;
				}
			}
			cells.add(cell);
		}
		return cells;
	}
	

	
	private static int obtainDataSourcesSizeCountingDependents(List<DataSource> dataSources) {
		int i =0;
		for (DataSource dataSource : dataSources) {
			i++;
			if(dataSource.hasDependent()) i++;
		}
		return i;
	}
	
	private static int spaceColumnWidth = 0;

	private static void addSpaceColumn(int column, int numberRows, boolean needsSpace,
			final FlexTable flexTable) {
		for (int i=0; i < numberRows; i++) {
			Label nameLabel = new Label("");
			if(needsSpace)nameLabel.setWidth(spaceColumnWidth+"px");
//			flexTable.setWidget(i, column, nameLabel);
		}
	}

	private static int addNameValuesToColumn(int column, Model selectedModel, List<DataSource> dataSources,
			int numberOfDetailsColumns, FlexTableEnum flexTableEnum, final FlexTable flexTable,
			Map<String, List<Model>> mapKindToSmallTables, Map<String, Object> contentValues, int dataSourceBegginingIndex, int dataSourceEndIndex, Profile profile, MyServiceClientImpl myService, int width) {
		int i;
		for (i = dataSourceBegginingIndex; i < dataSourceEndIndex; i++) {
			DataSource dataSource = dataSources.get(i);
			String name = (String) dataSource.getName();
			Object value =  contentValues.get(name);
			
			Object dependentValue = null;
			if(dataSource.hasDependent()) 
				dependentValue = contentValues.get( (String) dataSource.getDependent().getName());
			
			addNameValueToTable(flexTable, name, value, dependentValue, i-dataSourceBegginingIndex, column, flexTableEnum, dataSource, selectedModel, mapKindToSmallTables, numberOfDetailsColumns, profile, myService, width, 1);
			if(dataSource.hasDependent())
				i++;
		}
		return i;
	}
	private static final DialogBox dialogBox = new MyDialogBox(true);
	
	private static void addNameValueToTablePrint(FlexTable flexTable, String name, final Object value, Object dependentValue, int rowIndex, int initalColumnIndex, FlexTableEnum flexTableEnum, final DataSource dataSource, final Model model, Map<String, List<Model>> mapKindToSmallTables, int numberOfDetailsColumns, Profile profile, MyServiceClientImpl myService, int width, int colspan) {

		String stringValue = null;
		if(value != null && value instanceof String) {
				stringValue = (String) value;
		}
		Label nameLabel;
			nameLabel = new Label(dataSource.getLabel());
	//		if(numberOfDetailsColumns>1)nameLabel.setWidth("10em");

			nameLabel.addStyleName("myLabel");
			Label dependentNameLabel = null;
			if(dataSource.getDependent() != null) {
				dependentNameLabel = new Label(dataSource.getDependent().getLabel());
	//			if(numberOfDetailsColumns>1)dependentNameLabel.setWidth("10em");
			}
			
			int columnWidth = (width-spaceColumnWidth-(TABLE_BORDER*2))/2; //FIXME numberOfDetailsColumns;
			DataSourceTypeEnum type = dataSource.getType();
			
			if(DataSourceTypeEnum.Label.equals(type)) {
				addLabel(flexTable, name, value, rowIndex, initalColumnIndex, numberOfDetailsColumns, columnWidth);
			}
			else {
				addTextPrint(flexTable, rowIndex, initalColumnIndex, dataSource, model, stringValue, nameLabel,  columnWidth, colspan);
			}
	}
	private static void addNameValueToTable(FlexTable flexTable, String name, final Object value, Object dependentValue, int rowIndex, int initalColumnIndex, FlexTableEnum flexTableEnum, final DataSource dataSource, final Model model, Map<String, List<Model>> mapKindToSmallTables, int numberOfDetailsColumns, Profile profile, MyServiceClientImpl myService, int width, int colspan) {
		if(FlexTableEnum.Print.equals(flexTableEnum)) {
			 addNameValueToTablePrint(flexTable, name, value, dependentValue, rowIndex, initalColumnIndex, flexTableEnum, dataSource, model, mapKindToSmallTables, numberOfDetailsColumns, profile, myService, width, colspan);
			 return;
		}
		String stringValue = null;
		if(value != null && value instanceof String) {
				stringValue = (String) value;
		}
		Label nameLabel;
			nameLabel = new Label(dataSource.getLabel());
	//		if(numberOfDetailsColumns>1)nameLabel.setWidth("10em");

			nameLabel.addStyleName("myLabel");
			Label dependentNameLabel = null;
			if(dataSource.getDependent() != null) {
				dependentNameLabel = new Label(dataSource.getDependent().getLabel());
	//			if(numberOfDetailsColumns>1)dependentNameLabel.setWidth("10em");
			}
			
			int columnWidth = (width-spaceColumnWidth-(TABLE_BORDER*2))/2; //FIXME numberOfDetailsColumns;
			
			DataSourceTypeEnum type = dataSource.getType();
			switch (type) {
			case Checkbox:
				addCheckBox(flexTable, rowIndex, initalColumnIndex, dataSource, model, value, profile);
				break;
			case Date:
				nameLabel = new Label(name);
		    	addDate(nameLabel, flexTable, stringValue, rowIndex, initalColumnIndex, dataSource, model);
				break;	
			case Combobox:
				  addCombobox(flexTable, rowIndex, initalColumnIndex, dataSource, model, stringValue, mapKindToSmallTables, name, value, dependentValue, nameLabel, dependentNameLabel, myService);
				break;
			case Image:
			     addImage(flexTable, stringValue, rowIndex, initalColumnIndex, dataSource, model, myService, flexTableEnum);
				break;
			case Text:
				addText(flexTable, rowIndex, initalColumnIndex, dataSource, model, stringValue, nameLabel,  columnWidth);
			    break;
			case TextArea :
				addTextArea(flexTable, rowIndex, initalColumnIndex, dataSource, model, stringValue, nameLabel);
			    break;
			case Label:
				addLabel(flexTable, name, value, rowIndex, initalColumnIndex, numberOfDetailsColumns, columnWidth);
			    break;
			case ImageBoolean:
				// do nothing
				break;
			
			default:
				throw new RuntimeException("FlexTableHelper.addNameValueToTable: type " + type.toString() + " not implemented");
			}
	}

 	private static void addLabel(FlexTable flexTable, String name, final Object value, int rowIndex,
			int initalColumnIndex, int numberOfDetailsColumns, int columnWidth) {
		Label nameLabel;
		nameLabel = new Label(name);
//		if(numberOfDetailsColumns>1) nameLabel.setWidth((columnWidth-TABLE_BORDER*2)*35/100+"px");
		Label valueLabel = new Label((String)value);
		nameLabel.addStyleName("myLabel");
		flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
		flexTable.setWidget(rowIndex, initalColumnIndex + 1, valueLabel);
	}

	private static void addTextArea(FlexTable flexTable, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model, String stringValue, Label nameLabel) {
		final TextArea textArea = new TextArea();
		textArea.setWidth("800px");
		textArea.setHeight("250px");
		textArea.setValue(stringValue);
		textArea.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				model.put(dataSource.getName(), textArea.getText());
			}
		});
		flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
		flexTable.setWidget(rowIndex, initalColumnIndex + 1, textArea);
	}

	private static void addText(FlexTable flexTable, int rowIndex, int initalColumnIndex, final DataSource dataSource,
			final Model model, String stringValue, Label nameLabel, int columnWidth) {
		final TextBox textBox = new TextBox();
		//textBox.setWidth((columnWidth-TABLE_BORDER*2)*65/100+"px");   //FIXME width
		//nameLabel.setWidth((columnWidth-TABLE_BORDER*2)*35/100+"px");
		
//		nameLabel.setWidth("15%");
//		textBox.setWidth("35%");
		
//		Window.alert("text: " + columnWidth*65/100+"px" + " label: " + columnWidth*35/100+"px");
		
		textBox.setValue(stringValue);
		textBox.setWidth("95%");
		
		
		
		flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
		
		
		
		flexTable.setWidget(rowIndex, initalColumnIndex + 1, textBox); //FIXME
	//	label2.setWidth("35%");
		textBox.addStyleName("flexTableText");
		
	//	flexTable.setWidget(rowIndex, initalColumnIndex + 1, textBox); FIXME
		

	    flexTable.getCellFormatter().addStyleName(rowIndex, initalColumnIndex + 0,"flexTableCellFormatterLabel");
	    flexTable.getCellFormatter().addStyleName(rowIndex, initalColumnIndex + 1,"flexTableCellFormatterText");
		
//	flexTable.getColumnFormatter().setWidth(initalColumnIndex + 0, "15%"); //FIXME width
//	flexTable.getColumnFormatter().setWidth(initalColumnIndex + 1, "35%");
		

		//flexTable.getColumnFormatter().setWidth(initalColumnIndex + 0, (columnWidth-TABLE_BORDER*2)*35/100+"px"); //FIXME width
		//flexTable.getColumnFormatter().setWidth(initalColumnIndex + 1, (columnWidth-TABLE_BORDER*2)*65/100+"px");
	}
	
	private static void addTextPrint(FlexTable flexTable, int rowIndex, int initalColumnIndex, final DataSource dataSource,
			final Model model, String stringValue, Label nameLabel, int columnWidth, int colspan) {
		
		flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
		
		Label label2=  new Label(stringValue); 
		label2.setWordWrap(true);
		flexTable.setWidget(rowIndex, initalColumnIndex + 1, label2); 
		label2.addStyleName("myLabel");
	    flexTable.getCellFormatter().addStyleName(rowIndex, initalColumnIndex + 0,"flexTableLabel");
	    

		flexTable.getFlexCellFormatter().setColSpan(rowIndex, initalColumnIndex + 1, colspan);		
	    flexTable.getCellFormatter().addStyleName(rowIndex, initalColumnIndex + 1,"flexTableText");
	}
	
	
	private static ListBox createCombobox(FlexTable flexTable, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model, String stringValue, Map<String, List<Model>> mapKindToSmallTables, String name, Object value, Widget nameLabel) {
		final ListBox dropBox = new ListBox();
		dropBox.setMultipleSelect(false);
		dropBox.setName(name);
		List<String> listValues = dataSource.getComboValues();

		if (listValues == null || listValues.isEmpty()) {
			listValues = new ArrayList<String>();
			if (mapKindToSmallTables != null) {
				List<Model> comboModel = mapKindToSmallTables.get(name);
				if(comboModel != null)
				for (Model m : comboModel) {
					listValues.add(m.getKeyValue());
				}
			} else {
				log.info("map null");
			}
		}

		dropBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				model.put(dataSource.getName(), dropBox.getSelectedValue());
				
				log.info("onChange-> dataSource.name: " + dataSource.getName() + " value: " + dropBox.getSelectedValue()); //FIXME
				
			}
		});

		model.put(dataSource.getName(), dropBox.getSelectedValue());

		flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
		flexTable.setWidget(rowIndex, initalColumnIndex + 1, dropBox);
		
		if (listValues == null || listValues.isEmpty()) {
			log.info("combobox with name: " + name + ", listValue isEmpty");
			return dropBox;
		}
		for (int i = 0; i < listValues.size(); i++) {
			dropBox.addItem(listValues.get(i));
		}
		int selectedIndex = obtainSelectedDropboxIndex(dropBox, listValues, (String) value);
		dropBox.setSelectedIndex(selectedIndex);
		model.put(dataSource.getName(), dropBox.getSelectedValue());
		
		return dropBox;
	}
	
	private static void addCombobox(FlexTable flexTable, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model, String stringValue,
			Map<String, List<Model>> mapKindToSmallTables, String name, Object value, final Object dependentValue,
			Widget nameLabel, Label dependentNameLabel, final MyServiceClientImpl myService) {

		final ListBox combobox = createCombobox(flexTable, rowIndex, initalColumnIndex, dataSource, model, stringValue,
				mapKindToSmallTables, name, value, nameLabel);

		final DataSource dependentDataSource = dataSource.getDependent();
		if (dependentDataSource != null) {
			final ArrayList<String> dependentValues = new ArrayList<String>();
			final ListBox dependentCombobox = createCombobox(flexTable, rowIndex + 1, initalColumnIndex,
					dependentDataSource, model, (String) dependentValue, mapKindToSmallTables,
					(String) dependentDataSource.getName(), dependentValue, dependentNameLabel);

			combobox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					model.put(dataSource.getName(), combobox.getSelectedValue());

					MyFilterPredicate myFilterPredicate = new MyFilterPredicate("UF", MyFilterOperator.EQUAL,
							combobox.getSelectedValue());
					ListItemCallback listItemCallback = new ListItemCallback(dependentCombobox, "nome"); // FIXME
																											// this
																											// is
																											// fixed!!!
					myService.listItemEqual("cidade", "nome", null, null, 0, myFilterPredicate, Arrays.asList("nome"),  Arrays.asList("ASCENDING"),
							Ajax.call(listItemCallback, "city" + "_list"));

					// dependentCombobox.addItem("São Paulo");
				}

				class ListItemCallback implements AsyncCallback<List<Model>> {
					private ListBox listBox;
					private String name;
					public ListItemCallback(ListBox listBox, String name) {
						this.listBox = listBox;
						this.name = name;
					}
					@Override
					public void onFailure(Throwable caught) {
						log.info(caught.getMessage());
						throw new RuntimeException(caught);
					}
					@Override
					public void onSuccess(List<Model> result) {
						dependentValues.clear();
						if (result != null) {
							listBox.clear();
							for (Model model : result) {
								listBox.addItem(model.get(name));
								dependentValues.add(model.get(name));
							}
							int selectedIndex = obtainSelectedDropboxIndex(dependentCombobox, dependentValues, (String) dependentValue);
							dependentCombobox.setSelectedIndex(selectedIndex);
							model.put(dependentDataSource.getName(), dependentCombobox.getSelectedValue());
						}
					}
				}
			});
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), combobox);
			
			
		}
	}

	
	
	
		
	
	private static void addCheckBox(FlexTable flexTable, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model, Object value, Profile profile) {
		final CheckBox checkBox = new CheckBox(dataSource.getLabel());
		boolean checkValue = ( value==null ? false : (Boolean) value);
		checkBox.setValue(checkValue);
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				model.putObject(dataSource.getName(), checkBox.getValue());
				
			}
		});
		flexTable.setWidget(rowIndex, initalColumnIndex + 0, checkBox);
		if(profile != null && dataSource.getName().equals(Model.FEATURED) &&  !profile.authenticate(TableEnum.COLLECT.kind, CRUDEL.FEATURE)) {
			checkBox.setEnabled(false);
		}
	}

	private static void addDate(Label nameLabel, FlexTable flexTable, String value, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model) {
			flexTable.setWidget(rowIndex, initalColumnIndex + 0, nameLabel);
			final MyDateBox dateBox = new MyDateBox();
			Date date = StringUtils.isBlank(value) ? new Date() : DateHelper.convertYMDString(value);
			model.put(dataSource.getName(), DateHelper.convertDate(date, "yyyy/MM/dd"));
			dateBox.setValue(date);
			dateBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
				   @Override
				   public void onValueChange(ValueChangeEvent<String> event) {
						model.put(dataSource.getName(), DateHelper.convertDate(dateBox.getValue(), "yyyy/MM/dd")); 
				  }
				});
			dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
						model.put(dataSource.getName(), DateHelper.convertDate(dateBox.getValue(), "yyyy/MM/dd")); 
				}
			});
			DefaultFormat format = new DateBox.DefaultFormat 
					 (DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM)); 
			dateBox.setFormat(format);
			flexTable.setWidget(rowIndex, initalColumnIndex + 1, dateBox);
	}

    private static void startNewBlobstoreSession(final FormPanel formPanel, final Button uploadButton, MyServiceClientImpl myService) {
    	myService.getBlobURL( new AsyncCallback<BlobURL>() {
            @Override
            public void onSuccess(BlobURL result) {
            	formPanel.setAction(result.getUrl());
            	uploadButton.setText("Enviar");
            	uploadButton.setEnabled(true);
            }
            @Override
            public void onFailure(Throwable caught) {
                // We probably want to do something here FIXME
            	Window.alert("Erro ao tentar obter chave-blob.");
            }
        });
    }
    
    private static HorizontalPanel imagePanel = new HorizontalPanel();
    
	private static void addImage(FlexTable flexTable, final String value, int rowIndex, int initalColumnIndex,
			final DataSource dataSource, final Model model, MyServiceClientImpl myService, FlexTableEnum flexTableEnum) {
		Label customButton = new Label("editar");
		
		final FormPanel formPanel = new FormPanel();
		formPanel.add(customButton);

	    Button uploadButton = new Button("carregando..");
	    uploadButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				formPanel.submit();
			}
		});
	    uploadButton.setEnabled(false);

		//formPanel.setAction("/myFormHandler"); //FIXME
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		

	    final HorizontalPanel panel = new HorizontalPanel();
	    panel.setWidth("100%");
	    panel.setHeight("100%");
	    formPanel.setWidget(panel);

		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("image"); //FIXME add random
		panel.add(fileUpload);
		panel.add(uploadButton);

		imagePanel.setWidth("100%");
		imagePanel.setHeight("100%");
		imagePanel.clear();
		final Image image = new Image();
		if (value != null) {
			image.setUrl(value);
			image.setWidth("75px");
			image.addDoubleClickHandler(new DoubleClickHandler() {
				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					dialogBox.clear();
					Image image = new Image(value);
					dialogBox.add(image);
					dialogBox.setText("Foto");
					dialogBox.show();
				}
			});
			panelImages.add(image);
			 panel.add(image);	 
		}
		
		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
		      public void onSubmitComplete(SubmitCompleteEvent event) {
		    	     formPanel.reset();
		    	    String imageUrl = event.getResults();
		    	    image.setUrl(imageUrl);
			        model.put(dataSource.getName(), imageUrl);
			        model.putObject(dataSource.getName()+"Boolean", true);
				      image.setWidth("75px");
				      image.addDoubleClickHandler(new DoubleClickHandler() {
							@Override
							public void onDoubleClick(DoubleClickEvent event) {
								dialogBox.clear();
								Image fullImage = new Image(image.getUrl());
								dialogBox.add(fullImage);
								dialogBox.setText("Foto");
								dialogBox.show();
							}
						});
				      panelImages.clear();
				      panelImages.add(image);
				      imagePanel.clear();
				      imagePanel.add(image);
		    	    panel.add(imagePanel);
		      }
		    });
		
		if(!FlexTableEnum.Detail.equals(flexTableEnum))
			startNewBlobstoreSession(formPanel, uploadButton, myService);

		flexTable.setWidget(rowIndex, initalColumnIndex + 0, formPanel);
		flexTable.getFlexCellFormatter().setColSpan(rowIndex, initalColumnIndex, 3);		
		
	}

	  
	private static int obtainSelectedDropboxIndex(ListBox dropBox,
			List<String> listValues, String value) {
		for (int i = 0; i < listValues.size(); i++) {
			if(value.equals(listValues.get(i))) 
				return i;
		}
		return 0;
	}
	
	/**
	 *  removes Key
	 * @param contentValues
	 * @return
	 */
	private static Map<String, Object> removePrivateColumns(Map<String, Object> contentValues) {
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		Object[] keySet = (contentValues.keySet().toArray());
		
		for (int i = 0; i < keySet.length; i++) {
			String name = (String) keySet[i];
			Object value =  contentValues.get(name);
			if(!"Key".equals(name) ) {
				result.put(name, value);
			}
		}
		return result;
	}
	
	public static void disableAllChildren(Widget widget)
	{
	    if (widget instanceof HasWidgets)
	    {
	        Iterator<Widget> iter = ((HasWidgets)widget).iterator();
	        while (iter.hasNext())
	        {
	            Widget nextWidget = iter.next();
	            disableAllChildren(nextWidget);
	            if (nextWidget instanceof FocusWidget)
	            {
	                ((FocusWidget)nextWidget).setEnabled(false);
	            }
	            else  if (nextWidget instanceof DateBox)
	            {
	                ((DateBox)nextWidget).setEnabled(false);
	            }
	            	
	        }
	    }
	}
}
package br.fapema.morholt.web.client.gui.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;

public interface DataGridResource extends DataGrid.Resources {
	  public static DataGridResource INSTANCE = GWT.create(DataGridResource.class);
	  interface DataGridStyle extends DataGrid.Style {}

	  @Override
	  @Source({ DataGrid.Style.DEFAULT_CSS, "dataGridStyle.css" })
	  DataGridStyle dataGridStyle();
	}

package br.fapema.morholt.web.client.gui.grid;

import com.google.gwt.user.client.ui.IsWidget;

import br.fapema.morholt.web.client.gui.AuxiliarInterface;
import br.fapema.morholt.web.client.gui.model.Model;

public interface EnterInterface extends IsWidget, AuxiliarInterface{
	public void setEnterSelectedModel(Model model);
	public void clear();
	public void setParentColumnLabel(String parentColumnLabel);
	public void setParentColumn(String parentColumn);
}

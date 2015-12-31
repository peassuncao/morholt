package br.fapema.morholt.web.client.gui.util;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public final class LoadingPopup extends PopupPanel
{
    private final FlowPanel container = new FlowPanel();

    /**
     * 
     * @param modal
     */
    public LoadingPopup(boolean modal)
    {       
    	setModal(modal);
        final Image ajaxImage = new Image("img/ajax-loader.gif");
        final Grid grid = new Grid(1, 2); 
        grid.setWidget(0, 0, ajaxImage);
        grid.setText(0, 1, "Processando...");   
        this.container.add(grid);
        add(this.container);      
    }
 
 
}

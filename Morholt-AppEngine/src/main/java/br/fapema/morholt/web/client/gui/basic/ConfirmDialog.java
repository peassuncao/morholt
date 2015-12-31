package br.fapema.morholt.web.client.gui.basic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ConfirmDialog extends DialogBox {

	
		  public ConfirmDialog(String text, final DialogCallback callback) {
		    //setText(text);

		    Button yesButton = createYesButton(callback);
		    Button noButton = createNoButton();
		    
		    HorizontalPanel horizontalPanel = new HorizontalPanel();
		    horizontalPanel.add(yesButton);
		    horizontalPanel.add(noButton);
		    horizontalPanel.setWidth("100%");
		    HTML msg = new HTML("<center>"+text+"</center>",true);
		    msg.setWidth("100%");
		    DockPanel dock = new DockPanel();
		    dock.setWidth("100%");
		    //dock.setSpacing(4);

		    dock.add(horizontalPanel, DockPanel.SOUTH);
		    dock.add(msg, DockPanel.NORTH);

		    dock.setCellHorizontalAlignment(horizontalPanel, DockPanel.ALIGN_CENTER);
		    setWidget(dock);
		    setModal(true);
		    //setSize("100px", "100px");
		   // setPixelSize(100, 20);
		    setPopupPosition(Window.getClientWidth()/2, Window.getClientHeight()/2);
		    show();
		  }

		private Button createNoButton() {
			Button noButton = new Button("Não");
		    noButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
			});
			return noButton;
		}

		private Button createYesButton(final DialogCallback callback) {
			Button yesButton = new Button("Sim");
		    yesButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					callback.onDialogOk();
					hide();
				}
			});
			return yesButton;
		}

	
	
}

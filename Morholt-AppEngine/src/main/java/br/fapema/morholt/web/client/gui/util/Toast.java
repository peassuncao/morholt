package br.fapema.morholt.web.client.gui.util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;


public class Toast {
	
	
	public static void show(String what) 
	{
	    PopupPanel pop = new PopupPanel(true);
	    pop.setWidget(new Label(what));

	    final PopupPanel p = pop;
	    p.addStyleName("toast");

	    RootPanel.get().add(pop);

	    pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 2;
	            int top = (Window.getClientHeight() - offsetHeight) / 2;
	            p.setPopupPosition(left, top);
	          }
	        });

	    new Timer() {
	      @Override
	      public void run() 
	      {
	        System.out.println("timer repeated");
	        RootPanel.get().remove(p);
	        this.cancel();
	      }
	    }.scheduleRepeating(2000);
	}
	
}
package br.fapema.morholt.web.client.gui.basic;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;

public class MyDialogBox extends DialogBox{

	public MyDialogBox(boolean autoHide) {
		super(autoHide);
		addStyleName("dialogVPanel");
	}

	public boolean onEventPreview(Event event)
	{
	  if ((DOM.eventGetType(event) & Event.MOUSEEVENTS) != 0)
	  {
	    /*
	     * Firefox: the client area stops at the scroll bar. If a mouse
	     * event is outside the client area, it is targeted to the
	     * scrollbar. Override default behavior to allow these events. 
	     */
	    int clientX = DOM.eventGetClientX(event);
	    int clientY = DOM.eventGetClientY(event);
	    if (clientX > Window.getClientWidth() || clientY > Window.getClientHeight())
	    {
	      return true;
	    }
	  }
	  return super.onEventPreview(event);
	}
	
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
//		super.onPreviewNativeEvent(event);
		event.consume();
	}
}

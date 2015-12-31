package br.fapema.morholt.web.client.gui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

import br.fapema.morholt.web.client.service.MyServiceClientImpl;

public class Ajax {

    private static final PopupPanel loadingIndicator = new LoadingPopup(false);
    private static final PopupPanel loadingIndicatorModal = new LoadingPopup(true);
    private static final List<AsyncCallback<?>> callstack = new ArrayList<AsyncCallback<?>>();
    private static final HashMap<AsyncCallback<?>, String> mapCallbackToCallerName = new HashMap<AsyncCallback<?>, String>();
	public static PopupPanel popupPanel; 


	private static final Logger log = Logger.getLogger(Ajax.class.getName());
	
    /**
     * @param callback
     * @param modal
     * @return
     */
    public static <T> AsyncCallback<T> call(final AsyncCallback<T> callback, boolean modal, String callerName) {
    	popupPanel = (modal == true) ? loadingIndicatorModal : loadingIndicator;
    	
        if(!popupPanel.isShowing()){
        	popupPanel.center();
        }
        mapCallbackToCallerName.put(callback, callerName);
    //    callstack.add(callback);
   //     callerNames.add(callerName);
        return new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                handleReturn();
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(T result) {
                handleReturn();
                callback.onSuccess(result);
            }

            private void handleReturn(){
            	mapCallbackToCallerName.remove(callback);
               // callstack.remove(callback);
                if(mapCallbackToCallerName.size() < 1) {
                	popupPanel.hide();
                }
                else {
                	log.info("callstack.size: " + mapCallbackToCallerName.size());
                	for(String callerName : mapCallbackToCallerName.values())
                	  log.info("caller: " + callerName);
                }
            }
        };
    }

    /**
     * 
     * @param callback
     * @param callerName
     * @return
     */
    public static <T> AsyncCallback<T> call(final AsyncCallback<T> callback, String callerName) {
    	return call(callback, false, callerName);
    }
   
    public void limitWaiting(Request request) {
    	
    }
}

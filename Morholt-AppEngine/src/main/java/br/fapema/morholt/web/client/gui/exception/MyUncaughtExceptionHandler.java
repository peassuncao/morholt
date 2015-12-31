package br.fapema.morholt.web.client.gui.exception;

import java.util.logging.Logger;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;

public class MyUncaughtExceptionHandler implements GWT.UncaughtExceptionHandler {

	private static final Logger log = Logger.getLogger(MyUncaughtExceptionHandler.class.getName());
	
	 public void onUncaughtException(Throwable e) {
         ensureNotUmbrellaError(e);
         //log.info("ERROR-> message: " + e.getMessage() + " stacktrace: " + getCustomStackTrace(e));
       }
       
       private  void ensureNotUmbrellaError(Throwable e) {
       	if(e == null) {
     	      alertError(e);
       		return;
       	}
       	if(!(e instanceof UmbrellaException)) {
       		e.printStackTrace();
       	    log.info("ERROR-> message: " + e.getMessage() + " stacktrace: " + getCustomStackTrace(e));
     	      alertError(e);
       	    return;
       	}
       	  for (Throwable th : ((UmbrellaException) e).getCauses()) {
       	    if (th instanceof UmbrellaException) {
       	      ensureNotUmbrellaError(th);
       	    } else {
       	      th.printStackTrace();
       	      log.info("ERROR-> message: " + th.getMessage() + " stacktrace: " + getCustomStackTrace(th));
       	      alertError(e);
       	    }
       	  }
       	}
       
       private void alertError(Throwable e) {
    	   if(e instanceof AuthorizationException) {
    		   Window.alert("Operação não realizada. Este usuário não têm permissão para esta operação.");
    	   }
    	   else   if(e instanceof AuthenticationException) {
    		   Window.alert("Operação não realizada. Falha de autenticação, usuário não reconhecido.");
    	   }
    	   else   if(e instanceof UserNotAllocatedToProjectException) {
    		   Window.alert("Operação não realizada. Este usuário não está alocado neste projeto.");
    	   }
    	   else 
        	      Window.alert("Erro no processamento.");
    		   
    		   
       }
       
       private  String getCustomStackTrace(Throwable aThrowable) {
           //add the class name and any message passed to constructor
           StringBuilder result = new StringBuilder(  );
           result.append(aThrowable.toString());
           String NEW_LINE = System.getProperty("line.separator", "\n");
           result.append(NEW_LINE);

           //add each element of the stack trace
           for (StackTraceElement element : aThrowable.getStackTrace()){
             result.append(element);
             result.append(NEW_LINE);
           }
           return result.toString();
         }
}
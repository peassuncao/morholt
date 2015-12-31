package br.fapema.morholt.web.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DoNothingCallback implements AsyncCallback{

	@Override
	public void onFailure(Throwable caught) {
		throw new RuntimeException(caught);
	}

	@Override
	public void onSuccess(Object result) {
		//do nothing
	}

}

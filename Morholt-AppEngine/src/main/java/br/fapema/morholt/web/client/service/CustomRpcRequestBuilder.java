package br.fapema.morholt.web.client.service;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

public class CustomRpcRequestBuilder extends RpcRequestBuilder {

 private int timeout = 61000;
 
 public CustomRpcRequestBuilder() {
	 super();
 }
 
 public CustomRpcRequestBuilder(int timeout) {
	 super();
	 this.timeout = timeout;
 }
 
 @Override
 protected RequestBuilder doCreate(String serviceEntryPoint) {
	 RequestBuilder builder = super.doCreate(serviceEntryPoint); 
	 builder.setTimeoutMillis(this.timeout); 
  
	 return builder;
 }
}

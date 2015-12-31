package br.fapema.morholt.web.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;

import gwtupload.server.gae.BlobstoreUploadAction;

public class MyBlobstoreUploadAction extends BlobstoreUploadAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9127297325504229622L;


	public MyBlobstoreUploadAction()  {
		super();
	}
	

	  @Override
	  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		  super.doGet(request, response);
	  }


	  @Override
	  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		  super.doPost(request, response);
	  }
}

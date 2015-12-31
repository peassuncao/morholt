package br.fapema.morholt.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.server.mapper.CSVMapper;
import br.fapema.morholt.web.shared.Authority;
import br.fapema.morholt.web.shared.TableEnum;
 
@SuppressWarnings("serial")
public class UploadCSVServlet extends SecureHttpServlet {
    

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
		super.doPost(req, resp);
    }

	@Override
	protected void afterLogin(HttpServletRequest req, HttpServletResponse resp, WebUser user) throws IOException {
		 BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	        BlobKey blobKey = blobs.get("csvdata");
	        String kind = req.getParameter("kind");
	        String columns = req.getParameter("columns"); 
	        String separator = req.getParameter("separator"); 
	        if (blobKey == null) {
	            resp.sendRedirect("/");
	        } else {
	        	
	        	ArrayList<String> columnsList = new ArrayList<String>(); 
		        String[] splittedColumns = columns.split(separator);
		        for (String column : splittedColumns) {
		        	columnsList.add(column);
		        	log.info("coluna: " + column); //FIXME
				}
		        log.info("columns length: " + columnsList.size()); //FIXME
		        CSVMapper csvMapper = new CSVMapper();
		        String jobId = csvMapper.run(kind, separator, columnsList, blobKey.getKeyString());
		        
		        resp.setContentType("text/html");
		        resp.getWriter().println("Successfully uploaded file data. Inserting data on database. Job id: ");
		        resp.getWriter().println( 
		                jobId);
	        	
	        	
	         //   resp.sendRedirect("/uploadcsv-success?blob-key=" + blobKey.getKeyString()+"&kind="+kind+"&columns="+columns); FIXME if this works, delete SuccessulUpload
	        }
		
	}

	@Override
	protected void authorize(WebUser user) throws AuthorizationException {
		Authority.authenticate(user);
		Authority.authorize(user.getProfile(), TableEnum.UPLOAD_CSV.kind, CRUDEL.CREATE);
	}
 
}

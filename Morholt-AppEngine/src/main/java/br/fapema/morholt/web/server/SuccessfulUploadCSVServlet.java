package br.fapema.morholt.web.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.fapema.morholt.web.client.gui.exception.AuthorizationException;
import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.server.mapper.CSVMapper;
import br.fapema.morholt.web.shared.Authority;
import br.fapema.morholt.web.shared.TableEnum;
 
@SuppressWarnings("serial")
public class SuccessfulUploadCSVServlet extends SecureHttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
		super.doGet(req, resp);
    }

	@Override
	protected void authorize(WebUser user) throws AuthorizationException {
		Authority.authenticate(user);
		Authority.authorize(user.getProfile(), TableEnum.UPLOAD_CSV.kind, CRUDEL.CREATE);
	}

	@Override
	protected void afterLogin(HttpServletRequest req, HttpServletResponse resp, WebUser user) throws IOException {
		  String blobKey = req.getParameter("blob-key");
	       String kind = req.getParameter("kind");
	        String columns = req.getParameter("columns");
	        ArrayList<String> columnsList = new ArrayList<String>(); 
	        String[] splittedColumns = columns.split("%3B");
	        for (String column : splittedColumns) {
	        	columnsList.add(column);
			}
	        
	        CSVMapper csvMapper = new CSVMapper();
	//FIXME class needed?        String jobId = csvMapper.run(kind, columnsList, blobKey);
	        
	        resp.setContentType("text/html");
	        resp.getWriter().println("Successfully uploaded file data. Inserting data on database. Job id: ");
//	        resp.getWriter().println( 
//	                jobId);
	}
}

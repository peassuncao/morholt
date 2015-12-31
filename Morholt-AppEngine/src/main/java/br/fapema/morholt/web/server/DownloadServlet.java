package br.fapema.morholt.web.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.shared.util.StringUtils;

public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -3587175672159893800L;

	private static final Logger log = Logger.getLogger(DownloadServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("doGet export");
		Object parameter = request.getParameter("exportId");
		ModelList modelList = (ModelList) MyServiceImpl.mapTicketModel.get(parameter);

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
		try {
			OutputStream outputStream = response.getOutputStream();
			
			writeCsv(modelList, ';', outputStream);
			
			outputStream.flush();
			outputStream.close();
			modelList.remove(parameter);
		} catch (Exception e) {
			log.log(Level.SEVERE, "error generating csv", e);
		}
		log.info("doGet export done");
	}

	public static <T> void writeCsv(ModelList modelList, char separator, OutputStream output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "ISO-8859-1"));
		addCSVHeader(modelList, separator, writer);
		addCsvBody(modelList, separator, writer);
		writer.flush();
	}

	private static void addCsvBody(ModelList modelList, char separator, BufferedWriter writer) throws IOException {
		
		for (Model model : modelList) {
			Map<String, Object> contentValues = model.getContentValues();
			
			for (Iterator<String> iter = modelList.getColumns().iterator(); iter.hasNext();) {
				String key = iter.next();
				String value = String.valueOf(contentValues.get(key)).equals("null") ? "" : String.valueOf(contentValues.get(key));
				String field = value.replace("\"", "\"\"");
				if (field.indexOf(separator) > -1 || field.indexOf('"') > -1) {
					field = '"' + field + '"';
				}
				writer.append(field);
				if (iter.hasNext()) {
					writer.append(separator);
				}
			}
			writer.newLine();
		}
	}

	private static void addCSVHeader(ModelList modelList, char separator, BufferedWriter writer) throws IOException {
		for (Iterator<String> iter = modelList.getColumns().iterator(); iter.hasNext();) {
			String field = String.valueOf(iter.next()).replace("\"", "\"\"");
			if (field.indexOf(separator) > -1 || field.indexOf('"') > -1) {
				field = '"' + field + '"';
			}
			writer.append(field);
			if (iter.hasNext()) {
				writer.append(separator);
			}
		}
		writer.newLine();
	}
}

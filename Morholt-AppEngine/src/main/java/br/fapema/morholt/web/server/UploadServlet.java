package br.fapema.morholt.web.server;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import br.fapema.morholt.web.client.gui.MyEntryPoint;

/**
 * this is used by android
 * @author bhola
 *
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 4491205697805341438L;
	private static final String SEPARA = "SEPARA";
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		log("UploadServlet doPost");
		Map<String, List<BlobKey>> mapNameBlobKeys = blobstoreService
				.getUploads(req);

		Set<String> keySet = mapNameBlobKeys.keySet();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		String keyPlusName = keySet.iterator().next();

		String key = (keyPlusName.split(SEPARA))[0];
		log("upload key: " + key);

		Key entityKey = KeyFactory.stringToKey(key);
		
		Transaction transaction = datastore.beginTransaction();
		try {
			Entity storedEntity = datastore.get(entityKey);
			log("upload size: " + keySet.size());
			
			for (String name : keySet) {
				String columnName = (name.split(SEPARA))[1];

				ImagesService imagesService = ImagesServiceFactory
						.getImagesService();

				ServingUrlOptions servingOptions = ServingUrlOptions.Builder
						.withBlobKey(mapNameBlobKeys.get(name).get(0));
				
				String servingUrl = imagesService.getServingUrl(servingOptions);
				if(servingUrl.startsWith("http://0.0.0.0:8888")) {
					servingUrl = servingUrl.replace("http://0.0.0.0:8888", MyEntryPoint.URL_STRING + ":8888");
				}
				storedEntity.setProperty(columnName, servingUrl);
				datastore.put(storedEntity);
			}
			transaction.commit();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			log("Error on upload: " + e.getMessage());
			throw e;
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
			log("Error on upload: " + e.getMessage());
			throw e;
		} catch (DatastoreFailureException e) {
			e.printStackTrace();
			log("Error on upload: " + e.getMessage());
			throw e;
		} catch (EntityNotFoundException e) {
			log("Error on upload: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
		log("UploadServlet done!");
	}
}
package br.fapema.morholt.android.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
*/

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import br.fapema.morholt.android.CloudEndpointUtils;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.collect.collectendpoint.Collectendpoint;
import br.fapema.morholt.collect.collectendpoint.model.BlobURL;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;
import br.fapema.morholt.android.wizardpager.wizard.model.CameraPage;

public class PhotoSendEndpointTask extends AsyncTask<Context, Integer, Long> {

	private static final String SEPARA = "SEPARA";
	private EndpointCallback endpointCallback;
	private Integer listPosition;
	private Values values;
	
	/**
	 * @param photoEndpointCallback
	 * @param listPosition
	 * @param values
	 */
	public PhotoSendEndpointTask(EndpointCallback photoEndpointCallback, Integer listPosition, Values values) {
		this.endpointCallback = photoEndpointCallback;
		this.listPosition = listPosition;
		this.values = values;
	}

	protected Long doInBackground(Context... contexts) {
		Log.i("PhotoSendEndpointTask", "doInBackground start"); 
		GoogleAccountCredential credential = MyApplication.getCredential();
		
		Collectendpoint.Builder endpointBuilder = new Collectendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new com.google.api.client.json.jackson2.JacksonFactory(), credential);
		Collectendpoint endpoint = CloudEndpointUtils.updateBuilder(
				endpointBuilder).build();
		Log.i(this.getClass().getSimpleName(), "Endpoint: " + endpoint.getRootUrl());
		try {
			 
		    BlobURL blobURL = endpoint.getBlobURL().execute();
			String url = blobURL.getUrl();
			if(url.contains("brandi-i7:8888"))  // FIXME local hostname 
				url = url.replace("brandi-i7:8888",  Collectendpoint.LOCAL_DEFAULT_IP); 
			try {
				Map<String, File> files = new HashMap<String, File>();
				List<String> fileNames = getCameraColumnNames();
				for (String fileName : fileNames) {
					final File file = new File(getPhotoUrl(fileName));  
					files.put(fileName, file);
				}
				
				
				String x = postFile(url, files);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("ReviewFragment", "GetURLEndpointsTask, error on endpoint: " + e.getMessage());
				return (long) 1;
			} 
		} catch (IOException e) {
			
			e.printStackTrace();
			Log.e("ReviewFragment", "GetURLEndpointsTask, error2 on endpoint: " + e.getMessage());
			return (long) 1;
		}
		Log.i("PhotoSendEndpointTask", "doInBackground end"); 
		return (long) 0;
	}
	
	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		endpointCallback.endpointCallback(new TaskResult(listPosition, result));
	}


	private String getPhotoUrl(String column) {
		return values.getAsString(column); 
	}
	
	private List<String> getCameraColumnNames() {
		return MyApplication.pagelist.obtainColumnNameFromPages(CameraPage.class);
	}
	

	   
	public String postFile(String url, Map<String, File> files) throws Exception {
		
		String key = values.getAsString("key"); 
		MultipartUtility multipartUtility = new MultipartUtility(url, "UTF-8");
		  for (String fileName : files.keySet()) {
		//   	builder.addBinaryBody(key+SEPARA + fileName, files.get(fileName), ContentType.create("image/jpeg"), key+SEPARA + fileName);    
			
		  
		multipartUtility.addFilePart(key+SEPARA + fileName, files.get(fileName));
		  }
		  List<String> response = multipartUtility.finish();
		
		
	/* FIXME desde que mudei pra ultima versao do android sdk	
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	    for (String fileName : files.keySet()) {
	    	builder.addBinaryBody(key+SEPARA + fileName, files.get(fileName), ContentType.create("image/jpeg"), key+SEPARA + fileName);    
		}
	    
	    final HttpEntity yourEntity = builder.build();

	    class ProgressiveEntity implements HttpEntity {
	        @Override
	        public void consumeContent() throws IOException {
	            yourEntity.consumeContent();                
	        }
	        @Override
	        public InputStream getContent() throws IOException,
	                IllegalStateException {
	            return yourEntity.getContent();
	        }
	        @Override
	        public Header getContentEncoding() {             
	            return yourEntity.getContentEncoding();
	        }
	        @Override
	        public long getContentLength() {
	            return yourEntity.getContentLength();
	        }
	        @Override
	        public Header getContentType() {
	            return yourEntity.getContentType();
	        }
	        @Override
	        public boolean isChunked() {             
	            return yourEntity.isChunked();
	        }
	        @Override
	        public boolean isRepeatable() {
	            return yourEntity.isRepeatable();
	        }
	        @Override
	        public boolean isStreaming() {             
	            return yourEntity.isStreaming();
	        } 

	        @Override
	        public void writeTo(OutputStream outstream) throws IOException {

	            class ProxyOutputStream extends FilterOutputStream {
	            
	            */
	                /**
	                 * @author Stephen Colebourne
	                 */

		/*FIXME desque mudou versao do android
	                public ProxyOutputStream(OutputStream proxy) {
	                    super(proxy);    
	                }
	                public void write(int idx) throws IOException {
	                    out.write(idx);
	                }
	                public void write(byte[] bts) throws IOException {
	                    out.write(bts);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {
	                    out.write(bts, st, end);
	                }
	                public void flush() throws IOException {
	                    out.flush();
	                }
	                public void close() throws IOException {
	                    out.close();
	                }
	            } // CONSIDER import this class (and risk more Jar File Hell)

	            class ProgressiveOutputStream extends ProxyOutputStream {
	                public ProgressiveOutputStream(OutputStream proxy) {
	                    super(proxy);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {

	                    out.write(bts, st, end);
	                }
	            }

	            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
	        }

	    };
	    ProgressiveEntity myEntity = new ProgressiveEntity();

	    post.setEntity(myEntity);
	    org.apache.http.HttpResponse response = client.execute(post);        

	    return getContent(response);
*/
		return null;
	} 

	/* FIXME desde que mudou android
	public static String getContent(org.apache.http.HttpResponse response) throws IOException {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    String body = "";
	    String content = "";

	    while ((body = rd.readLine()) != null) 
	    {
	        content += body + "\n";
	    }
	    return content.trim();
	}
	*/
	
}

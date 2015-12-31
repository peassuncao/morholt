package br.fapema.morholt.web.server.mapper;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jetty.util.log.Log;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.MapJob;
import com.google.appengine.tools.mapreduce.MapOnlyMapper;
import com.google.appengine.tools.mapreduce.MapSettings;
import com.google.appengine.tools.mapreduce.MapSpecification;
import com.google.appengine.tools.mapreduce.inputs.BlobstoreInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;

import br.fapema.morholt.web.client.gui.MyEntryPoint;

public class CSVMapper implements Serializable{

	private static final long serialVersionUID = 6507526152487435520L;


	static final Logger log = Logger.getLogger(CSVMapper.class.getName());
	
	public CSVMapper() {
	}
	
	private String kind;
	private String separator;
	
	public String run(String kind, String separator, List<String> columns, String blobkey) {
		this.kind = kind;
		this.separator = separator;
		Byte lineSeparator = (byte) '\n';
		int shardCount = 20;
		 MapSpecification<byte[], Entity, Void> mapSpecification = new MapSpecification.Builder<byte[], Entity, Void>(
			        new BlobstoreInput(blobkey, lineSeparator, shardCount),
			        new EntityCreator(columns),
			        new DatastoreOutput())
			        .setJobName("Create CSMapReduce entities")
			        .build();
		 MapSettings settings = getSettings();

		String id = MapJob.start(mapSpecification, settings);
		return id;
		
	}
	
	
	  private MapSettings getSettings() {
		    MapSettings settings = new MapSettings.Builder()
		        .setWorkerQueueName("mapreducecsv-workers")
		        .build();
		    return settings;
		  }
	
	private class EntityCreator extends MapOnlyMapper<byte[], Entity> {
		private static final long serialVersionUID = -6672229528187690669L;
		private List<String> columns;
		public EntityCreator (List<String> columns) {
			this.columns = columns;
		}
		@Override
		public void map(byte[] segment) {
	        String line;
			try {
				line = new String(segment, "UTF-8");
				Entity entity = new Entity(kind);
				
				String[] splitLine = line.split(separator);
				
				log.info("columns: " +columns.size() + " splitLine: " + splitLine.length);
				
				for(int i=0; i < splitLine.length; i++) {  //FIXME ;
					entity.setProperty(columns.get(i), splitLine[i]);
	//				log.info("columns.get(i):" + columns.get(i) + " splitLine[i]: " + splitLine[i]);
				}
				emit(entity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				log.info("error: " + e.getMessage());
				throw new RuntimeException(e);
			}
			
		}
		
	}

}

package br.fapema.morholt.web.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.apphosting.api.ApiProxy.OverQuotaException;

import br.fapema.morholt.web.client.gui.model.WebUser;
import br.fapema.morholt.web.shared.exception.MyQuotaException;

public class DAO {

	private static final String UPDATE = "Update";
	private static final Logger log = Logger.getLogger(DAO.class.getName());

	//TODO key not being used
	public static Key insert(String kind, String parentKey,
			Map<String, Object> newProperties) throws MyQuotaException {
		log.info("insertMy parentKey: " + parentKey);

		Entity entity = null;
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		  
		Key key = null;
		try {
			if (newProperties.get("id") != null) {
				entity = new Entity(kind, (String) newProperties.get("id"));
			} else if (newProperties.get("Key") != null) {

				entity = datastore.get(KeyFactory.stringToKey((String) newProperties
						.get("Key")));
			} else {
				entity = new Entity(kind);
			}
			for (String mapKey : newProperties.keySet()) {
				if (mapKey.equals("Key"))
					continue;
				if ((mapKey.toLowerCase().contains("date") || mapKey.toLowerCase().contains("data"))
						&& newProperties.get(mapKey) instanceof Long) { // TODO locale
					Date date = new Date((Long) newProperties.get(mapKey));
					
					/*
					GregorianCalendar gregorianCalendar = new GregorianCalendar();
					gregorianCalendar.setTime(date);
					GregorianCalendar newGregorianCalendar = new GregorianCalendar();
					newGregorianCalendar.set(gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DAY_OF_MONTH));
					newGregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
					newGregorianCalendar.set(GregorianCalendar.MINUTE, 0);
					newGregorianCalendar.set(GregorianCalendar.SECOND, 0);
					newGregorianCalendar.set(GregorianCalendar.MILLISECOND, 0);
					*/
					entity.setProperty(mapKey, date);
				} else
					entity.setProperty(mapKey, newProperties.get(mapKey));
			}
			
			if(entity.getKey() != null && entity.getKey().getId() != 0) {
				
				Key entityKey = entity.getKey();
				saveHistoricChanges(newProperties, datastore, entityKey, kind, UPDATE);
			}
			
			key = datastore.put(entity);
			txn.commit();
		} 
		catch(OverQuotaException e) {
			log.info("over quota: " + e.getMessage());
			throw new MyQuotaException(e);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return null;
		} catch (DatastoreFailureException e) {
			e.printStackTrace();
			return null;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return key;
	}

	/**
	 * 
	 * @param newProperties
	 * @param datastore
	 * @param entityKey
	 * @param kind
	 * @param type Update or Delete
	 * @throws MyQuotaException 
	 */
	private static void saveHistoricChanges(Map<String, ? > newProperties,
			DatastoreService datastore, Key entityKey, String kind, String type) throws MyQuotaException
			 {
		log.info("saveHistoricChanges kind: " + kind);
		Entity oldEntity;
		try {
			oldEntity = datastore.get(entityKey);
		
		Entity historicEntity = new Entity("historic");
		for (String mapKey : newProperties.keySet()) {
			if(mapKey.equals("Key") || mapKey.equals("user") || mapKey.equals("lastUpdateOnSystem") || mapKey.equals("lastUpdateOnSystem_day") || mapKey.equals("lastUpdateOnSystem_month") || mapKey.equals("lastUpdateOnSystem_year")) {
				// do nothing
			}
			else if(newProperties.get(mapKey) != null && !newProperties.get(mapKey).equals(oldEntity.getProperty(mapKey))) {
				historicEntity.setProperty(kind+"__"+mapKey+"_old", oldEntity.getProperty(mapKey));
				historicEntity.setProperty(kind+"__"+mapKey+"_new", newProperties.get(mapKey));
			}
		}

		Map<String, Object> oldProperties = oldEntity.getProperties();
		for (String propertiesKey : oldProperties.keySet()) {
			if(propertiesKey.equals("Key") || propertiesKey.equals("user") || propertiesKey.equals("lastUpdateOnSystem") || propertiesKey.equals("lastUpdateOnSystem_day") || propertiesKey.equals("lastUpdateOnSystem_month") || propertiesKey.equals("lastUpdateOnSystem_year")) {
				// do nothing
			}
			else if(newProperties.get(propertiesKey) == null && oldProperties.get(propertiesKey) != null) {
				historicEntity.setProperty(kind+"__"+propertiesKey+"_old", oldProperties.get(propertiesKey));
				historicEntity.setProperty(kind+"__"+propertiesKey+"_new", null);
			}
		}

		historicEntity.setProperty("lastUpdateOnSystem_day", newProperties.get("lastUpdateOnSystem_day"));
		historicEntity.setProperty("lastUpdateOnSystem_month", newProperties.get("lastUpdateOnSystem_month"));
		historicEntity.setProperty("lastUpdateOnSystem_year", newProperties.get("lastUpdateOnSystem_year"));

		historicEntity.setProperty("lastUpdateOnSystem", newProperties.get("lastUpdateOnSystem"));

		historicEntity.setProperty("user_new", newProperties.get("user"));
		historicEntity.setProperty("user_old", oldProperties.get("user"));
		

        historicEntity.setProperty("historic_id", String.valueOf(oldEntity.getKey().getId()));
		historicEntity.setProperty("kind", kind);
		historicEntity.setProperty("type", type);
		datastore.put(historicEntity);
		
		} 
		catch(OverQuotaException e) {
			throw new MyQuotaException(e);
		}
		catch (EntityNotFoundException e) {
			throw new RuntimeException("on saveHistoricChange entity not found for key: " + entityKey);
		}
	}
	
	public static List<String> insertMult(String kind, String keyName,
			ArrayList<Map<String, String>> rows) throws MyQuotaException {
		List<String> stringKeys = null;
		List<Key> keys = new ArrayList<Key>();
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			ArrayList<Entity> entities = new ArrayList<Entity>();
			for (Map<String, String> row : rows) {
				Entity entity = new Entity(kind, row.get(keyName));
				for (String rowKey : row.keySet()) {
					log.info("key: " + rowKey);
					entity.setProperty(rowKey, row.get(rowKey));
				}
				entities.add(entity);
				
				if(entity.getKey() != null && entity.getKey().getId() != 0) {
					saveHistoricChanges(entity.getProperties(), datastore, entity.getKey(), kind, UPDATE);
				}
			}
			
			
			keys = datastore.put(entities);
			txn.commit();
			
		   stringKeys = new ArrayList<String>();
			for (Key key : keys) {
				stringKeys.add(KeyFactory.keyToString(key));
			}
		}
		catch(OverQuotaException e) {
			throw new MyQuotaException(e);
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return stringKeys;
	}

	public static void removeMult(String kind, 
			List<String> stringKeys) throws MyQuotaException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			ArrayList<Key> keys = new ArrayList<Key>();
			for (String stringKey : stringKeys) {
				keys.add(KeyFactory.stringToKey(stringKey));
			}
				datastore.delete(keys);
			txn.commit();
		} 
		catch(OverQuotaException e) {
			throw new MyQuotaException(e);
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	public static void delete(String kind, String keyValue, WebUser user) throws MyQuotaException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key key = KeyFactory.stringToKey(keyValue);
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			
			int style = java.text.DateFormat.MEDIUM;

			java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(style);
			
			Date date = null;
				date = new Date();
				
				
				Calendar calendar = GregorianCalendar.getInstance();
				dateFormat.setCalendar(calendar);
				
				calendar.setTime(date);
				
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1;
				int year = calendar.get(Calendar.YEAR);
				
				hashMap.put("lastUpdateOnSystem_day", String.valueOf(day));
				hashMap.put("lastUpdateOnSystem_month", String.valueOf(month));
				hashMap.put("lastUpdateOnSystem_year", String.valueOf(year));
				
				hashMap.put("lastUpdateOnSystem", dateFormat.format(date));
				
				hashMap.put("user", user.getEmail());
			
			
			
			saveHistoricChanges(hashMap, datastore, key, kind, "Delete");
			datastore.delete(key);
			txn.commit();
		}
		catch(OverQuotaException e) {
			throw new MyQuotaException(e);
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
}
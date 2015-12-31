package br.fapema.morholt.android.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.grilledmonkey.niceql.helpers.SchemeOpenHelper;
import com.grilledmonkey.niceql.structs.Column;
import com.grilledmonkey.niceql.structs.Scheme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.parser.Database;
import br.fapema.morholt.android.parser.ModelPath;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

public  class DBHelper  {
	

	private static int dbversion = 1;
	
	private static String dbName;
	
	private static Database database;
	
	private static SQLiteDatabase db;
	
	
	private static Long id = -1l;
	
	public static void initialize(Context _context, Database _database) {
		if(db != null) db.close(); 
		
		database = _database;
		Scheme scheme = SchemaBuilder.createSchema(database);
		
		dbName = ProjectInfo.getProjectName();
		
		SchemeOpenHelper helper = new SchemeOpenHelper(_context, dbName, scheme);

		// Get database connection object
		// This line will check if database exists and will make sure it does
		db = helper.getWritableDatabase();
		id = -1l;
	}
	
	private DBHelper(Context context, String tableName, List<Column> columns) {
		

	}
	
	/**
	 * 
	 * @param orderByClause
	 *            eg.: id DESC
	 * @return
	 */
	public static Values load(String id) {
		
		Cursor cursor = db.query(dbName, null, "_id = ?", new String[] { id },
				null, null, "1");
		cursor.moveToFirst();

		ContentValues contentValues = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
		
		
		
		return new Values(contentValues);
	}
	
	/**
	 * 
	 * @param orderByClause eg.: id DESC
	 * @return
	 */
	public static List<Values> list(String tableName, String orderByClause) {
		List<Values> contentValuesList = new ArrayList<Values>();

		Cursor cursor = db.query(tableName, null, null, null, null, null,
				orderByClause);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentValues contentValues = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
			cursor.moveToNext();
			contentValuesList.add(new Values(contentValues));
		}
		// make sure to close the cursor
		cursor.close();
		return contentValuesList;
	}
	
	/**
	 * 
	 * @param orderByClause eg.: id DESC
	 * @return
	 */
	public static List<Values> listRelatedToId(String relatedTableName, String referenceKeyName, String referenceKeyValue, String orderByClause) {
		List<Values> contentValuesList = new ArrayList<Values>();

		Cursor cursor = db.query(relatedTableName, null, referenceKeyName + "=" + referenceKeyValue, null, null, null,            
				orderByClause);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentValues contentValues = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
			cursor.moveToNext();
			contentValuesList.add(new Values(contentValues));
		}
		// make sure to close the cursor
		cursor.close();
		return contentValuesList;
	}
	
	/**
	 * list all values and the values that reference these.
	 * TODO optimize
	 * @param orderByClause eg.: id DESC
	 * @return
	 */
	public static List<Values> listAll(String tableName, String orderByClause) { 
		List<Values> mainValues = list(tableName, orderByClause);
		
		Set<String> relatedTableNames = database.getRelatedTables(tableName);
		
		for (String relatedTableName : relatedTableNames) {
			for (Values row : mainValues) {
				String id = row.getAsString("_id");
		//	basetable_id nao esta sendo salvo
				List<Values> relatedValues = listRelatedToId(relatedTableName, tableName+"_id", id, null);
			
				row.put(relatedTableName, relatedValues);
			}
		}
		
		return mainValues;
	}
	
	
	public static void saveAll(List<Values> listValues, ModelPath modelPath) {
		db.beginTransaction();
		try {
			for (Values values : listValues) {
				save(values, modelPath);
			}
			db.setTransactionSuccessful();
		} 
		
		catch (SQLException e) {
			// this will automatically rollback (see db.beginTransaction())
			e.printStackTrace();
		}
		finally {
			db.endTransaction();
		}
		
	}
	
	public static void save(Values contentValues, ModelPath modelPath) {
		id = contentValues.getAsLong("_id"); 
		
		ModelPath parentModelPath = modelPath.getParentModelPath();
		if(parentModelPath != null) {
			addParentReferenceIdToValues(contentValues, modelPath.getParentModelPath());
		}
		
		try {
			if(id == null || id == -1) {
				id = db.insertOrThrow(modelPath.obtainKind(), null, contentValues.toContentValues());
				contentValues.put("_id", id);
			}
			else {
				db.update(modelPath.obtainKind(), contentValues.toContentValues(), "_id = " + id, null);
			}
		} catch (SQLException e) {
			Log.e("Error trying to save.", e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private static void addParentReferenceIdToValues(Values contentValues,
			ModelPath parentModelPath) {
		if(parentModelPath == null) return; 
		                                                            
		contentValues.put(parentModelPath.obtainKind() + "_id", MyApplication.getCurrentContentValues(parentModelPath).getId());
	}

	public static long getId() {
		return id;
	}

	public static void setId(long id) {
		DBHelper.id = id;
	}

	public static void delete(Long id, ModelPath modelPath) {
		String where = "_id = ?";
		try {
			db.delete(modelPath.obtainKind(), where, new String[] { String.valueOf(id) });
		}
		catch (SQLException e) {
			Log.e("Error trying to delete.", e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

}

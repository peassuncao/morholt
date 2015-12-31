package br.fapema.morholt.android.parser;

import java.util.List;

import com.grilledmonkey.niceql.structs.Column;
import com.grilledmonkey.niceql.structs.Index;

public class DataStructure {
	public String tableName;
	public List<Column> columns;
	public List<Index> indexes;
	public String keyColumn;
	private boolean denormalizeBeforeSendToServer; 
	
	public DataStructure(String tableName, String keyColumn, List<Column> columns,
			List<Index> indexes, boolean denormalizeBeforeSendToServer) {
		this.tableName = tableName;
		this.columns = columns;
		this.indexes = indexes;
		this.keyColumn = keyColumn;
		this.denormalizeBeforeSendToServer = denormalizeBeforeSendToServer;
	}
	
	/**
	 * 
	 * @param tableName
	 * @param keyColumn
	 * @param columns
	 * @param indexes
	 * @param denormalizeBeforeSendToServer only working for denormalize for now
	 * @return
	 */
	public static DataStructure c(String tableName, String keyColumn, List<Column> columns,
			List<Index> indexes, boolean denormalizeBeforeSendToServer) {
		return new DataStructure(tableName, keyColumn, columns, indexes, denormalizeBeforeSendToServer);
	}

	public boolean shouldDenormalizeBeforeSendToServer() {
		return denormalizeBeforeSendToServer;
	}
}
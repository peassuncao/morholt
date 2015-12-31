package br.fapema.morholt.android.db;

import com.grilledmonkey.niceql.structs.Column;
import com.grilledmonkey.niceql.structs.Index;
import com.grilledmonkey.niceql.structs.Scheme;
import com.grilledmonkey.niceql.structs.Table;

import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.android.parser.DataStructure;
import br.fapema.morholt.android.parser.Database;

public class SchemaBuilder {
	public static final String DENORMILIZED = "_DENORMILIZED";

	public static Scheme createSchema(Database database) {
		Scheme scheme = new Scheme();
		for (DataStructure dataStructure : database) {
			Table table = new Table(dataStructure.tableName);
			if (dataStructure.indexes != null) {
				for (Index index : dataStructure.indexes) {
					table.addIndex(index);
				}
			}
			for (Column column : dataStructure.columns) {
				table.addColumn(column);
			}
			scheme.addTable(table);
		}
		
	//	addDenormalizedTable(scheme, database);
		return scheme;
	}
	
	private static Scheme addDenormalizedTable(Scheme scheme, Database database) {
		DataStructure dataStructure = database.get(0);
		Table denormilizedTable = new Table(dataStructure.tableName + DENORMILIZED);
		for (int i = 0; i < database.size(); i++) {
			dataStructure = database.get(i);
			if (dataStructure.indexes != null) {
				for (Index index : dataStructure.indexes) {
					denormilizedTable.addIndex(index);
				}
			}
			
			for (Column column : dataStructure.columns) {//photo
				if(i>0 && (column.getName().equals("lastUpdateOnSystem") || column.getName().equals(Creator.SENT_TO_SERVER) )) continue;
				
				denormilizedTable.addColumn(column);

				
			}
			if(i>0) {
				denormilizedTable.addColumn(new Column(dataStructure.tableName, Column.INTEGER)); //this will add the indice of the repeat
			}
		}
		denormilizedTable.addColumn(new Column("total", Column.INTEGER));
		scheme.addTable(denormilizedTable);
		return scheme;
	}
}
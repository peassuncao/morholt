package br.fapema.morholt.android.parser;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.squareup.otto.Bus;

import android.test.AndroidTestCase;
import br.fapema.morholt.android.basic.Creator;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.parser.XSLReader;
import br.fapema.morholt.android.parser.unit.Unit;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;
// just slow with AndroidTestCase(instrumentation)..

public class XSLReaderTest extends TestCase {

	private String columnNames;
	private List<String> lines;
	private Map<String, List<String>> parsedColumns;
	private List<Unit> units;
	private String[] columnsNamesSplitted;

	//@InjectMocks
    //final Bus bus = Mockito.spy(new Bus());
	
	protected void setUp() throws Exception {
		super.setUp();

		Bus mockedBus = Mockito.mock(Bus.class);
		

		//PowerMockito.mockStatic(BusProvider.class);
		//Mockito.doNothing().when(bus).register(Mockito.any());
		
//		when(BusProvider.getInstance()).thenReturn(mockedBus);
	//	PowerMockito.doReturn(mockedBus).when(BusProvider.class, "getInstance");
		
		lines = XSLReader.readFromFile(ProjectInfo.getTemplateFileName());

		columnNames = lines.get(0);
		columnsNamesSplitted = columnNames.split("\\;", -1);
		
		parsedColumns = XSLReader.parseColumns(lines);
		
		units = XSLReader.groupByUnit(lines);
		
	}
	
	public void testFindMatchingEndIndex() {
		int index = XSLReader.findMatchingEndIndex(2, lines);
		assertEquals(4, index);
	}
	
	/*
	@Test
	public void testGenerateDatabase() {
		
		Database database = XSLReader.generateDatabase(units, columnsNamesSplitted, Creator.TABLE_NAME, Creator.TABLE_KEY); // TODO tablename, key
		assertNotNull(database);
	}
	*/
	
	public void testReadLine() {
		List<String> readFromFile = XSLReader.readFromFile(ProjectInfo.getTemplateFileName());
		assertTrue(readFromFile != null);
		assertEquals(readFromFile.size(), 71); // it removes empty lines
	}
	
	public void testParseColumns() {
		lines.add(0, columnNames);
		Map<String, List<String>> parsedColumns = XSLReader.parseColumns(lines);
		assertTrue(parsedColumns != null);
		
		List<String> labelColumn = parsedColumns.get("label");
		
		assertEquals("Etapa", labelColumn.get(5));
		assertEquals(70, labelColumn.size()); // the first line is out
	}
	
	public void testGroupByUnit() {
		List<Unit> groupByUnit = XSLReader.groupByUnit(lines);
		assertNotNull(groupByUnit);
	//	assertEquals(30, groupByUnit.size());
	}

}

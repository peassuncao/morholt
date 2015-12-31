package br.fapema.morholt.android.initial.load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.fapema.morholt.android.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.db.DBHelper;
import br.fapema.morholt.android.model.ProjectInfo;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

public class LoadActivity extends BaseListActivity {

	private LoadListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.initial_load);
		final List<LoadListValues> loadList = getLoadList();
		setHeaders(loadList);

		ListView view = (ListView) findViewById(android.R.id.list);
		adapter = new LoadListAdapter(getApplicationContext(), this, loadList);
		view.setAdapter(adapter);
		
		/*
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Toast.makeText(view.getContext(), "xxx", Toast.LENGTH_SHORT).show();
				
			}
		});*/
	}

	private void setHeaders(final List<LoadListValues> loadList) {
		TextView description1 = (TextView) findViewById(R.id.header_line1);
		TextView description2 = (TextView) findViewById(R.id.header_line2);
		description1.setText("Carregar coleta");
		description2.setText("Escolha:");

		TextView columnHeader1 = (TextView) findViewById(R.id.column_header1);
		TextView columnHeader2 = (TextView) findViewById(R.id.column_header2);
		TextView columnHeader3 = (TextView) findViewById(R.id.column_header3);
		TextView columnHeader4 = (TextView) findViewById(R.id.column_header4);
		TextView columnHeader5 = (TextView) findViewById(R.id.column_header5);
		TextView columnHeader6 = (TextView) findViewById(R.id.column_delete);

		if (loadList.size() > 0) {
			columnHeader1.setText(loadList.get(0).getColumnOneHeader());
			columnHeader2.setText(loadList.get(0).getColumnTwoHeader());
			columnHeader3.setText(loadList.get(0).getColumnThreeHeader());
			columnHeader4.setText(loadList.get(0).getColumnFourHeader());
			columnHeader5.setText(loadList.get(0).getColumnPhotoSentToServerHeader());
			columnHeader6.setText(loadList.get(0).getColumnDelete());
		}
	}

	private List<LoadListValues> getLoadList() {
		List< LoadListValues> threeColumnListViewInterface = new ArrayList<LoadListValues>();
		List<Values> collects = DBHelper.listAll(ProjectInfo.getTableName(), "_id desc"); 
		for (Values values : collects) {
			threeColumnListViewInterface.add(new LoadListValues(values));
		}
		return threeColumnListViewInterface;
	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		Values selectedItem = (Values) adapter.getItem(position);
		openLoadDialog(position, selectedItem);
	}

	private void openLoadDialog(final int position, Values selectedItem) {
		new AlertDialog.Builder(this).setTitle("Carregar?")
				.setMessage("Quer carregar esse item,\n id =  " + selectedItem.getAsString("_id") + ", np = " + selectedItem.getAsText("np") + " ?") 
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						loadDataIntoApplication(position);
						open(R.id.action_edit);
					}

					private void loadDataIntoApplication(final int position) {
						Values selectedItem = (Values) adapter.getItem(position);
						MyApplication application = (MyApplication) getApplication();
						// application.setPreData(selectedItem.preData);
						application.setValues(selectedItem);
						application.setCurrentCollectPage(0);
						application.setLoaded(true); 
						application.pagelist.initializeRepeats();
					}
				}).setNegativeButton(android.R.string.no, null).show();
	}
}
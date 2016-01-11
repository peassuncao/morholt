package br.fapema.morholt.android.initial.load;

import java.util.Iterator;
import java.util.List;

import com.activeandroid.util.Log;
import br.fapema.morholt.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.db.DAO;
import br.fapema.morholt.android.task.EndpointCallback;
import br.fapema.morholt.android.task.PhotoSendEndpointTask;
import br.fapema.morholt.android.task.SendEndpointTask;
import br.fapema.morholt.android.task.TaskResult;
import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

public class LoadListAdapter extends BaseAdapter implements EndpointCallback, ListAdapter {
	private Context context;
    private Activity activity;
    private List<LoadListValues> loadListValuesCollection;
    

    public LoadListAdapter(Context context, Activity activity, List<LoadListValues> loadListCollection) {
        super();
        this.context = context;
        this.activity = activity;
        this.loadListValuesCollection = loadListCollection;
    }

    @Override
    public int getCount() {
        return loadListValuesCollection.size();
    }

    @Override
    public Object getItem(int position) {
        return loadListValuesCollection.get(position);
    }

    @Override
    public long getItemId(int position) {
        return loadListValuesCollection.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getViewFromLoadListCollectionPosition(position, convertView);
        return convertView;
    }

	private View getViewFromLoadListCollectionPosition(int position, View convertView) {
		if (convertView == null) {
        	LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.initial_load_listrow, null);
        }
        TextView col1 = (TextView) convertView.findViewById(R.id.column1);
        TextView col2 = (TextView) convertView.findViewById(R.id.column2);
        TextView col3 = (TextView) convertView.findViewById(R.id.column3);
        ToggleButton col4 = (ToggleButton) convertView.findViewById(R.id.sendData);
        col4.setOnClickListener(new PositionSendButtonClickListener(position));
        ToggleButton col5 = (ToggleButton) convertView.findViewById(R.id.sendPhoto);
        col5.setOnClickListener(new PositionSendPhotoButtonClickListener(position));
        Button colDelete = (Button) convertView.findViewById(R.id.deleteButton);
        colDelete.setOnClickListener(new DeleteButtonClickListener(position));
        
        
        col1.setText(loadListValuesCollection.get(position).getColumnOne());
        col2.setText(loadListValuesCollection.get(position).getColumnTwo());
        col3.setText(loadListValuesCollection.get(position).getColumnThree());
        col4.setChecked(loadListValuesCollection.get(position).getColumnSentToServer()); 
        col4.setEnabled(!loadListValuesCollection.get(position).getColumnSentToServer());
        col5.setEnabled(loadListValuesCollection.get(position).getColumnSentToServer() && !loadListValuesCollection.get(position).getColumnPhotoSentToServer());
        col5.setChecked(loadListValuesCollection.get(position).getColumnPhotoSentToServer()); 
		return convertView;
	}
    

    private class DeleteButtonClickListener implements OnClickListener {

    	private int position;
    	public DeleteButtonClickListener(int position) {
    		this.position = position;
    	}
    	
		@Override
		public void onClick(View v) {
			final Long id = loadListValuesCollection.get(position).getId();
			String np =  (String) loadListValuesCollection.get(position).getMValues().get("np");
			if(np == null) np = "(vazio)";
			new AlertDialog.Builder(activity).setTitle("Apagar")
					.setMessage("Quer apagar esse item,\n id =  " + id + ", np = " + np + " ?") 
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							DAO.delete( MyApplication.getRootPath(), id);
							Toast.makeText(activity, "Apagado!", Toast.LENGTH_SHORT).show();
							loadListValuesCollection.remove(position);
							notifyDataSetChanged();
						}
					}).setNegativeButton(android.R.string.no, null).show();
		}
    }
    
    private class PositionSendButtonClickListener implements OnClickListener {
    	private int position;
    	public PositionSendButtonClickListener(int position) {
    		this.position = position;
    	}
		@Override
		public void onClick(View v) {
			if(sendEnabled(v)) {
				if(invalidSendingData()) {
					Toast.makeText(activity, "Dados incompletos, não foi possível enviar.", Toast.LENGTH_SHORT).show();
					((ToggleButton) v).setChecked(false);
					return;
				}
				((ToggleButton) v).setEnabled(false);
				new SendEndpointTask(activity, LoadListAdapter.this, (MyApplication)activity.getApplication(), loadListValuesCollection.get(position), position).execute(activity);
			}
		}
		private boolean sendEnabled(View v) {
			return loadListValuesCollection.get(position).getColumnSentToServer()== false && ((ToggleButton) v).isEnabled();
		}
		
		private boolean invalidSendingData() {
			Values rootContentValues = MyApplication.getRootContentValues();
			Values clone;
			boolean invalid = true;
			try {
				clone = rootContentValues.clone();
				 ((MyApplication)activity.getApplication()).setValues( loadListValuesCollection.get(position));
				 invalid = !MyApplication.pagelist.validate(); 
				 ((MyApplication)activity.getApplication()).setValues( clone);
			} catch (CloneNotSupportedException e) {
				Log.i(this.getClass().getSimpleName() + ".invalidSending data clone failed: " + e.getMessage());
				throw new RuntimeException();
			}
			return invalid;
		}
    }

	@Override
	public void endpointCallback(TaskResult result) {
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.initial_load_listrow, null);
        ToggleButton col4 = (ToggleButton) convertView.findViewById(R.id.sendData);
        
     // on fail
		if (result.result != 0) {
        	col4.setChecked(false);
        	col4.setEnabled(true);
			Log.i(this.getClass().getSimpleName() + ".endpointCallback failed");
			if (result.result == SendEndpointTask.RESULT_ERROR_AUTHORIZATION) {
				Toast.makeText(activity, "não foi possível enviar, falha de autorização do usuário", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ALLOCATED) {
				Toast.makeText(activity, "não foi possível enviar, este usuário não está alocado neste projeto", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHENTICATION) {
				Toast.makeText(activity, "não foi possível enviar, falha na autenticação do usuário", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ONLINE) {
				Toast.makeText(activity, "não foi possível enviar, sem acesso à internet", Toast.LENGTH_SHORT).show();
			}
			else if (result.result == SendEndpointTask.RESULT_ERROR_SERVER_UNAVAILABLE) {
				Toast.makeText(activity, "não foi possível enviar, servidor indisponível", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(activity, "falha no envio", Toast.LENGTH_SHORT).show();
			
			notifyDataSetChanged();
			return;
		}

		loadListValuesCollection.get(result.listPosition).setColumnSentToServer(true);
		DAO.save(MyApplication.getRootPath(), loadListValuesCollection.get(result.listPosition));
		notifyDataSetChanged();
    	Toast.makeText(activity, "sucesso", Toast.LENGTH_SHORT).show();
	}
	
    public class PositionSendPhotoButtonClickListener implements OnClickListener {
    	private int position;
    	public PositionSendPhotoButtonClickListener(int position) {
    		this.position = position;
    	}
		@Override
		public void onClick(View v) {
			if(loadListValuesCollection.get(position).getColumnPhotoSentToServer()== false && ((ToggleButton) v).isEnabled()) {
				((ToggleButton) v).setEnabled(false);
				new PhotoSendEndpointTask(context, new PhotoEndpointCallback(), position, loadListValuesCollection.get(position)).execute(activity);
			}
		}
		
		 public class PhotoEndpointCallback implements EndpointCallback {
			@Override
			public void endpointCallback(TaskResult result) {
				// on fail
				if (result.result != 0) {
					LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			        View convertView = inflater.inflate(R.layout.initial_load_listrow, null);
			        ToggleButton col5 = (ToggleButton) convertView.findViewById(R.id.sendPhoto);
		        	col5.setChecked(false);
		        	col5.setEnabled(true);
					Log.i(PhotoEndpointCallback.this.getClass().getSimpleName() + ".endpointCallback failed");
					if (result.result == SendEndpointTask.RESULT_ERROR_AUTHORIZATION) {
						Toast.makeText(activity, "não foi possível enviar a foto, falha de autorização do usuário", Toast.LENGTH_SHORT).show();
					}
					else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ALLOCATED) {
						Toast.makeText(activity, "não foi possível enviar, este usuário não está alocado neste projeto", Toast.LENGTH_SHORT).show();
					}
					else if (result.result == SendEndpointTask.RESULT_ERROR_AUTHENTICATION) {
						Toast.makeText(activity, "não foi possível enviar a foto, falha na autenticação do usuário", Toast.LENGTH_SHORT).show();
					}
					else if (result.result == SendEndpointTask.RESULT_ERROR_NOT_ONLINE) {
						Toast.makeText(activity, "não foi possível enviar a foto, sem acesso à internet", Toast.LENGTH_SHORT).show();
					}
					else if (result.result == SendEndpointTask.RESULT_ERROR_SERVER_UNAVAILABLE) {
						Toast.makeText(activity, "não foi possível enviar a foto, servidor indisponível", Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(activity, "falha no envio de foto", Toast.LENGTH_SHORT).show();
					
					notifyDataSetChanged();
					return;
				}
				LoadListValues loadListValues = loadListValuesCollection.get(result.listPosition);
				loadListValues.setPhotoSentToServer("1");
				DAO.save(MyApplication.getRootPath(), loadListValues);
				notifyDataSetChanged();
		    	Toast.makeText(activity, "sucesso no envio de foto", Toast.LENGTH_SHORT).show();
			}
		 }
    }
}
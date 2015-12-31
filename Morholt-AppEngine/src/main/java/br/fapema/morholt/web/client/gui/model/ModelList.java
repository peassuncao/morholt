package br.fapema.morholt.web.client.gui.model;

import java.util.ArrayList;

public class ModelList extends ArrayList<Model> {
	private static final long serialVersionUID = -6046472945933169684L;
	
	private ArrayList<String> columns;
	
	/**
	 * O(n)
	 * @param keyValue
	 * @return
	 */
	public Model get(String keyValue) {
		for (Model e : this) {
			if(e.getKeyValue().equals(keyValue)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * O(n)
	 * @param keyValue
	 * @return
	 */
	public boolean remove(String keyValue) {
		for (Model e : this) {
			if(e.getKeyValue().equals(keyValue)) {
				return super.remove(e);
			}
		}
		return false;
	}
	
	public String toCSV(String separator) {
		StringBuilder sb = new StringBuilder();
		
		if(size()==0) return "";
		
		
		Model aModel = get(0);
		for(String key : aModel.getContentValues().keySet()) {
			sb.append(key+separator);
		}

		sb.append("\n");
		
		for(Model model:this) {
			sb.append(model.toCSV(separator));
			sb.append("\n");
		}
		return sb.toString();
	}

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}
}

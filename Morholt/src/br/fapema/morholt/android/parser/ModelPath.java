package br.fapema.morholt.android.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.fapema.morholt.android.wizardpager.wizard.basic.Values;

public class ModelPath implements Serializable{

	private static final long serialVersionUID = -1774540913741536731L;
	List<PathPair> fullPath;
	       
	/**
	 * 
	 * @param ancestors
	 */
	public ModelPath(PathPair ...ancestors) {
		fullPath = new ArrayList<PathPair>(Arrays.asList (ancestors));
	}
	
	public ModelPath getRootModelPath () {
		return new ModelPath(fullPath.get(0));
	}
	
	public ModelPath add(PathPair pathPair) {
		
		ArrayList<PathPair> pairs = new ArrayList<PathPair>();
		pairs.addAll(fullPath);
		pairs.add(pathPair);
		return new ModelPath(pairs.toArray(new PathPair[pairs.size()] ));
	}
	
	public String obtainKind() {
		return fullPath.get(fullPath.size() -1).kind;
	}
	public Integer obtainIndex() {
		return fullPath.get(fullPath.size() -1).index;
	}
	
	public void setIndex(int value) {
		fullPath.get(fullPath.size() -1).index = 0;
	}
	
	public ModelPath getParentModelPath() {
		if(fullPath.size() == 1) return null; 
		PathPair[] pairArray = new PathPair[fullPath.size()-1];
		for (int i = 0; i < fullPath.size() -1; i++) {
			pairArray[i] = PathPair.c(fullPath.get(i).kind, fullPath.get(i).index);
		}
		return new ModelPath(pairArray);
	}
	
	/**
	 * obtain the values of the end of fullpath
	 * @param root
	 * @return
	 */
	public Values obtainContentValues(Values root) {
		
		if (fullPath.size() == 1) { 
			return root;
		}
		
		for (int i = 1; i < fullPath.size(); i++) {
			System.out.println("fullPath i:" + i + "v:" + fullPath.get(i));
			System.out.println("kind:" + root.getAsValuesList(fullPath.get(i).kind));
			List<Values> list = root.getAsValuesList(fullPath.get(i).kind);
			if(list == null) {
				ArrayList<Values> arrayList = new ArrayList<Values>();
				arrayList.add(new Values());
				root.put(fullPath.get(i).kind, arrayList);
				list = root.getAsValuesList(fullPath.get(i).kind);
			}
			root = list.get(fullPath.get(i).index);
		}
		return root;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (PathPair pathPair : fullPath) {
			stringBuilder.append(pathPair + ", ");
		}
		stringBuilder.append(" END");
		return stringBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ModelPath) return false;
		return o.toString().equals(toString());
	}
	
	public boolean isRootTable() {
		return fullPath.size() == 1;
	}
}
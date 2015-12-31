package br.fapema.morholt.android.collectpages.interfaces;

public interface SetadorColeta {
	public void radioMudou(int checkedId);
	
	/**
	 * 
	 * @param viewId may not have been set
	 */
    public void valueChanged(int viewId);
}

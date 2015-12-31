package br.fapema.morholt.android.wizardpager;

import android.content.Context;
import br.fapema.morholt.android.parser.Database;
import br.fapema.morholt.android.parser.ModelPath;

public interface ApplicationInterface {
	public void addToViewsAlreadyValidating(String _class);
	public boolean isLoaded();
	public boolean isViewAlreadyValidating(String _class);
	public Context getApplicationContext();
	public void setDatabase(Database database);
	public void setRootModelPath(ModelPath rootModelPath);
}

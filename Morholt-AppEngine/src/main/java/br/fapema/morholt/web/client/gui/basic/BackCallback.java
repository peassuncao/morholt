package br.fapema.morholt.web.client.gui.basic;

import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;

/**
 * Called when a back button is clicked
 * @author pedro
 *
 */
public interface BackCallback {
	public void back(CRUDEL crudel, Model model);
}

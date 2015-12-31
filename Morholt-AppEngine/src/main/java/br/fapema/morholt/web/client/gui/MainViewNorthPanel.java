package br.fapema.morholt.web.client.gui;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MainViewNorthPanel extends HorizontalPanel{

	public MainViewNorthPanel() {
		SafeHtml fapema = new SafeHtml() {
			private static final long serialVersionUID = -7044853830910607237L;
			@Override
			public String asString() {
				return "<a href=\"http://www.fapema.br/\" target=\"_blank\"><img href=\"www.fapema.br\" id=\"revealIcon\" src=\"img/fapema.png\" width=\"300\"/></a>";
			}
		};
		Anchor anchorFapema = new Anchor(fapema);
		
		SafeHtml ma = new SafeHtml() {
			private static final long serialVersionUID = -7067561415973403793L;
			@Override
			public String asString() {
				return "<a href=\"http://www.ma.gov.br/\" target=\"_blank\"><img  src=\"img/maranhao.png\" width=\"300\"/></a>";
			}
		};
		Anchor anchorMa = new Anchor(ma);
		anchorMa.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		add(anchorFapema);
		add(anchorMa);
		setWidth("100%");
	}

}

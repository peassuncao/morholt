package br.fapema.morholt.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ListView extends Composite implements HasText {

	private static ListViewUiBinder uiBinder = GWT
			.create(ListViewUiBinder.class);

	interface ListViewUiBinder extends UiBinder<Widget, ListView> {
	}

	public ListView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Button button;

	public ListView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		button.setText(firstName);
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
	}

	public void setText(String text) {
		button.setText(text);
	}

	public String getText() {
		return button.getText();
	}

}

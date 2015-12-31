package br.fapema.morholt.android.helper;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RadioHelper {
	public static String obterRadioText(View rootView, int radioCheckedId) {
		RadioButton selectedRadio = (RadioButton) rootView
				.findViewById(radioCheckedId);
		String radioMaterialText = selectedRadio.getText().toString();
		return radioMaterialText;
	}
	
	public static void checkRadioGroup(RadioGroup radioGroup, String selectedRadioText) {
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			RadioButton radio = (RadioButton) radioGroup.getChildAt(i);
			if(radio.getText().equals(selectedRadioText)) {
				radioGroup.check(radio.getId());
				break;
			}
		}
	}
}

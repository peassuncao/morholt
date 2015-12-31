package br.fapema.morholt.android.helper;

import br.fapema.morholt.android.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ContainerUtil {
	public static void replaceContainerFragmentWith(Fragment newFragment, int containerLayoutId, FragmentManager fragmentManager) {
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	  /*  if (direction == DirectionEnum.NEXT) {
	    	fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
	    }
	    else if (direction == DirectionEnum.PREVIOUS) {
	    	fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
	    }*/
		fragmentTransaction.replace(containerLayoutId, newFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commitAllowingStateLoss();
	}
}

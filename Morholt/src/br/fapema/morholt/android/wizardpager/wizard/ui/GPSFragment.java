package br.fapema.morholt.android.wizardpager.wizard.ui;

import br.fapema.morholt.android.MyApplication;
import br.fapema.morholt.android.R;
import br.fapema.morholt.android.WizardActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.ToggleButton;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.GPSPage;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

public class GPSFragment extends BugFragment implements PageInterface{
private View rootView;
    public boolean isCreated = false;

    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewLat;
    private TextView textViewLong;

    private TextView textLoadingPosition;


	private GPSPage mPage;

	public static GPSFragment create(String pageId) {
        Bundle args = new Bundle();
        args.putSerializable(PAGE_ID, pageId);

		GPSFragment f = new GPSFragment();
		f.setArguments(args);
		
		return f;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String pageId = (String) args.getSerializable(PAGE_ID);
        mPage = (GPSPage) getPage(pageId);
    }

    private class HandleClick implements OnClickListener{
        public void onClick(View v) {
            ToggleButton button = (ToggleButton) v;
            String nextText = button.getText().toString();

            if(getResources().getString(R.string.capital_on).equals(nextText)) {
                setLocationManager();
            }
            else {
                stopLocating();
            }

            Log.d("button", button.getText().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gps_fragment,
                container, false);
        textLoadingPosition = (TextView) rootView.findViewById(R.id.textLoadingPosition);


        HandleClick hc = new HandleClick();

        toggleButton = (ToggleButton) rootView.findViewById(R.id.toggleButtonStop);

        toggleButton.setOnClickListener(hc);

        textViewX = (TextView) rootView
                .findViewById(R.id.textX);
        textViewY = (TextView) rootView
                .findViewById(R.id.textY);
        
        textViewLat = (TextView) rootView
                .findViewById(R.id.textLat);
        textViewLong = (TextView) rootView
                .findViewById(R.id.textLong);
        
        textViewX.setSaveEnabled(false);
        textViewY.setSaveEnabled(false);
        textViewLat.setSaveEnabled(false);
        textViewLong.setSaveEnabled(false);
        
        
        
        
       update();

        setLocationManager();

        return rootView;
    }
    
    @Override
	public void disable() {
		toggleButton.setEnabled(false);
		textViewX.setEnabled(false);
		textViewY.setEnabled(false);
		textViewLat.setEnabled(false);
		textViewLong.setEnabled(false);
	}

	@Override
	public void update() {
        textViewX.setText(mPage.getXValue());
		textViewY.setText(mPage.getYValue());
        textViewLat.setText(mPage.getLatValue());
		textViewLong.setText(mPage.getLongitudeValue());
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
 //      if (((WizardActivity)getActivity()).isCurrentPage(mPage.getKey()))
  //     		setInputType();
        textViewX.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(getClass().toString() + ".afterTextChanged:  ", editable.toString());

                mPage.saveXValue((editable != null) ? editable.toString() : null);
                BusProvider.getInstance().post(new PageChangeEvent(mPage));
            }
        });

        textViewY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(getClass().toString() + ".afterTextChanged:  ", editable.toString());
                mPage.saveYValue((editable != null) ? editable.toString() : null);
                BusProvider.getInstance().post(new PageChangeEvent(mPage));
            }
        });
        
        
        
        
        
        textViewLat.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(getClass().toString() + ".afterTextLatChanged:  ", editable.toString());

                mPage.saveLatValue((editable != null) ? editable.toString() : null);
                BusProvider.getInstance().post(new PageChangeEvent(mPage));
            }
        });

        textViewLong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(getClass().toString() + ".afterTextLongChanged:  ", editable.toString());
                mPage.saveLongitudeValue((editable != null) ? editable.toString() : null);
                BusProvider.getInstance().post(new PageChangeEvent(mPage));
            }
        });
    }

    protected void setInputType() {
        textViewX.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        textViewY.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        textViewLat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        textViewLong.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocating();
    }

    private void stopLocating() {
    	Log.d(this + "stopLocating", "stopLocating");
        if(locationManager != null) {
            locationManager.removeUpdates(locationListener);
            textLoadingPosition.setVisibility(View.INVISIBLE);
        }
    }

    private void setLocationManager() {
    	Log.d(this + "setLocationManager", "setLocationManager");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );

        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
            	Log.d(this + "onLocationChanged", "onLocation accuracy:" + location.getAccuracy() + " lat:" +location.getLatitude() + " long:" + location.getLongitude() + " alt: " + location.getAltitude());
                System.out.println("onLocation accuracy:" + location.getAccuracy() + " lat:" +location.getLatitude() + " long:" + location.getLongitude() + " alt: " + location.getAltitude());
                if(location.getAccuracy() < 1000) { //FIXME was 40
                    textViewX.setText((String.valueOf(location.getLatitude())));
                    textViewY.setText(String.valueOf(location.getLongitude()));
                    stopLocating();
                    toggleButton.setChecked(false);
                }
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        textLoadingPosition.setVisibility(View.VISIBLE);
    	Log.d(this + "setLocationManager", "setLocationManager done");
    }

    public static boolean isValid = true;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private ToggleButton toggleButton;

    @Override
    public void onResume () {
        super.onResume();
    }




/*
    @Override
    public void valueChanged(int viewId) {
        if(viewId == textViewX.getId() ) {
            setValue1OnModel(textViewX.getText().toString());
            clearErrors1();
        }
        else {
            setValue2OnModel(textViewY.getText().toString());
            clearErrors2();
        }
    }
*/
    @Override
    public boolean validate() {
       return true; //it´s on page
        /*
        boolean validateText1 = validateText1();
        boolean validateText2 = validateText2();

        if(validateText1) {
            clearErrors1();
        }
        else {
            showErrors1();
        }

        if(validateText2) {
            clearErrors2();
        }
        else {
            showErrors2();
        }
        return validateText1 && validateText2;
    */
    }





    public void showErrors() {
        showErrors1();
        showErrors2();
    }
    public void showErrors1() {
        textViewX.setError("errado!");
    }

    public void showErrors2() {
        textViewY.setError("errado!");
    }

    public void clearErrors1() {
        textViewX.setError(null);
    }

    public void clearErrors2() {
        textViewY.setError(null);
    }

    public void clearErrors() {
        clearErrors1();
        clearErrors2();
    }


/*
    @Override
    public void onClick(View v) {
        ToggleButton button = (ToggleButton) v;
        String nextText = button.getText().toString();

        if(getResources().getString(R.string.capital_on).equals(nextText)) {
            setLocationManager();
        }
        else {
            stopLocating();
        }

        Log.d("button", button.getText().toString());
    }
*/
    @Override
    public void configureKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        textViewX.requestFocus();
        textViewX.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewY.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewLat.setInputType(InputType.TYPE_CLASS_NUMBER);
        textViewLong.setInputType(InputType.TYPE_CLASS_NUMBER);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        imm.showSoftInput(textViewX, 0);
    }

	@Override
	public String getUniqueIdentifier() {
		return PAGE_ID;
	}
}	
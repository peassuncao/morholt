package br.fapema.morholt.android.wizardpager.wizard.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.fapema.morholt.android.wizardpager.wizard.basic.BusProvider;
import br.fapema.morholt.android.wizardpager.wizard.model.Page;
import br.fapema.morholt.android.wizardpager.wizard.model.PageChangeEvent;
import br.fapema.morholt.android.wizardpager.wizard.ui.interfaces.PageInterface;

import br.fapema.morholt.android.R;


public class CameraFragment extends BugFragment implements OnClickListener, PageInterface {
	public boolean isCreated = false;
	private Button button;
	private ImageView foto;
	public  final int CAMERA_FRAGMENT = 666;
	
	private Page mPage; 
	

	protected static final String ARG_KEY = "key_camera";
	
	
	private View rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(this.toString(), "onCreateXX");
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		String pageId = (String) args.getSerializable(PAGE_ID);
		mPage = getPage(pageId);
		//setRetainInstance(false);
	}
	
	public static CameraFragment create(String pageId) {
		CameraFragment cameraFragment = new CameraFragment();

		Bundle args = new Bundle();
		args.putSerializable(PAGE_ID, pageId);
		
		cameraFragment.setArguments(args);
		return cameraFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d(this.toString(), "onCreateView");
		rootView = inflater.inflate(R.layout.camera_fragment, container,
				false);
		
		button = (Button) rootView.findViewById(R.id.botaoFoto);
		button.setText("fotografar");
		foto = (ImageView) rootView.findViewById(R.id.foto);

		button.setOnClickListener(this);
		foto.setSaveEnabled(false);
		update();
		return rootView;
	}

	@Override
	public void disable() {
		button.setEnabled(false);
	}
	
	@Override
	public void update() {
		if(mPage.getValue() != null) {
			putImage(mPage.getValue());
		}
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		button = null;
		foto.setImageBitmap(null);
	}

	public void onClick(View v)

	{
		Log.d(this.toString(), "onClick");

		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE,
				"New Picture" + new Date().getTime());
		values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
		Uri imageUri = getActivity().getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new ContentValues());

		if (imageUri == null) {
			Toast.makeText(getActivity(), "Erro tirando foto.",
					Toast.LENGTH_SHORT);
			return;
		}
		try {
			File imageFile = createImageFile();
			imageUri = Uri.fromFile(imageFile);
			mPage.saveValue(imageFile.getAbsolutePath());

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			Log.d(this.toString(), "startActivityForResult");
			startActivityForResult(intent, CAMERA_FRAGMENT);
		} catch (IOException e) {
			Log.e("CAMERA", "Error taking the photo: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(this.toString(), "onActivityResult");

		if (requestCode == CAMERA_FRAGMENT && resultCode == Activity.RESULT_OK) {
			try {
				File imageFile = new File(mPage.getValue());
				Uri imageUri = Uri.fromFile(imageFile);
				
				Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
						getActivity().getContentResolver(), imageUri);

				foto.setImageBitmap(thumbnail);
				System.out.println("tamanho foto em mb: "
						+ thumbnail.getByteCount() / 1000000);
				BusProvider.getInstance().post(new PageChangeEvent(mPage));

			} catch (Exception e) {
				Log.e("CAMERA", "Error taking the photo(onResult): " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
    /**
     * Creates the image file to which the image must be saved.
     * @return
     * @throws IOException
     */
    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
     //   CameraActivity activity = (CameraActivity)getActivity();
      //  activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }
	
	private void putImage(String imageAbsolutePath) {
		File file = new File(imageAbsolutePath);
		Uri imageUri = Uri.fromFile(file);
		Bitmap thumbnail;
		try {
			thumbnail = MediaStore.Images.Media.getBitmap(
			        getActivity().getContentResolver(), imageUri);
			foto.setImageBitmap(thumbnail);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	static final int REQUEST_TAKE_PHOTO = 1;

	
	@Override
	public boolean validate() {
		if (!mPage.isRequired() || mPage.isCompleted()) {
			clearErrors();
			return true;
		} else {
			showErrors();
			return false;
		}
	}

	public void showErrors() {
		Toast t = Toast.makeText(getActivity(), "Tire uma foto para avançar.",
				Toast.LENGTH_SHORT);
		t.show();
	}

	@Override
	public void clearErrors() {
		// do nothing
	}

	@Override
	public void onResume() {
		Log.d(this.toString(), "onResume");
		super.onResume();
	}

	@Override
	public void configureKeyboard() {
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}

	@Override
	public String getUniqueIdentifier() {
		return ARG_KEY;
	}
}	
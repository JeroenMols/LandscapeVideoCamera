package com.jmolsmobile.landscapevideocapture_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureDemoFragment extends Fragment {

	private final String	KEY_STATUSMESSAGE	= "com.jmolsmobile.statusmessage";

	private TextView		statusTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		final Button captureBtn = (Button) rootView.findViewById(R.id.btn_capturevideo);
		captureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(getActivity(), VideoCaptureActivity.class);
				startActivityForResult(intent, 101);
			}
		});

		statusTv = (TextView) rootView.findViewById(R.id.tv_status);
		if (savedInstanceState != null) {
			statusTv.setText(savedInstanceState.getString(KEY_STATUSMESSAGE));
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_STATUSMESSAGE, statusTv.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			statusTv.setText(R.string.status_capturesuccess);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			statusTv.setText(R.string.status_capturecancelled);
		} else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
			statusTv.setText(R.string.status_capturefailed);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
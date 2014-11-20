package com.jmolsmobile.landscapevideocapture_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureDemoFragment extends Fragment {

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

		return rootView;
	}
}
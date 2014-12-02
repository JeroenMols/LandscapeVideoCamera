package com.jmolsmobile.landscapevideocapture_sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jmolsmobile.landscapevideocapture.VideoCaptureActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureDemoFragment extends Fragment {

	private final String	KEY_STATUSMESSAGE	= "com.jmolsmobile.statusmessage";
	private final String	KEY_FILENAME		= "com.jmolsmobile.outputfilename";

	private String			statusMessage		= null;
	private String			filename			= null;

	private ImageView		thumbnailIv;
	private TextView		statusTv;
	private Spinner			resolutionSp;
	private Spinner			qualitySp;

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

		thumbnailIv = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
		statusTv = (TextView) rootView.findViewById(R.id.tv_status);
		if (savedInstanceState != null) {
			statusMessage = savedInstanceState.getString(KEY_STATUSMESSAGE);
			filename = savedInstanceState.getString(KEY_FILENAME);
		}

		updateStatusAndThumbnail();

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item,
				new String[] { "360p", "540p", "720p", "1080p", "1440p", "2160p" });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		resolutionSp = (Spinner) rootView.findViewById(R.id.sp_resolution);
		resolutionSp.setAdapter(adapter);

		final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, new String[] { "low", "medium", "high" });
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		qualitySp = (Spinner) rootView.findViewById(R.id.sp_quality);
		qualitySp.setAdapter(adapter2);

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(KEY_STATUSMESSAGE, statusMessage);
		outState.putString(KEY_FILENAME, filename);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			filename = data.getStringExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME);
			statusMessage = String.format(getString(R.string.status_capturesuccess), filename);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			filename = null;
			statusMessage = getString(R.string.status_capturecancelled);
		} else if (resultCode == VideoCaptureActivity.RESULT_ERROR) {
			filename = null;
			statusMessage = getString(R.string.status_capturefailed);
		}
		updateStatusAndThumbnail();

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateStatusAndThumbnail() {
		if (statusMessage == null) {
			statusMessage = getString(R.string.status_nocapture);
		}
		statusTv.setText(statusMessage);

		final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filename, Thumbnails.FULL_SCREEN_KIND);
		if (thumbnail != null) {
			thumbnailIv.setImageBitmap(thumbnail);
		} else {
			thumbnailIv.setImageResource(R.drawable.thumbnail_placeholder);
		}
	}
}
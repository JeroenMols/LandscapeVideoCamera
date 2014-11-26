package com.jmolsmobile.landscapevideocapture;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jmolsmobile.landscapevideocapture.R.id;

/**
 * @author Jeroen Mols
 */
public class VideoCaptureView {

	private ImageView	mDeclineBtnIv;
	private ImageView	mAcceptBtnIv;
	private ImageView	mRecordBtnIv;
	SurfaceView			mSurfaceView;
	ImageView			mThumbnailIv;

	void initializeAllViews(final VideoCaptureActivity videoCaptureActivity, View container2) {
		mRecordBtnIv = (ImageView) container2.findViewById(id.videocapture_recordbtn_iv);
		mAcceptBtnIv = (ImageView) container2.findViewById(id.videocapture_acceptbtn_iv);
		mDeclineBtnIv = (ImageView) container2.findViewById(id.videocapture_declinebtn_iv);

		mRecordBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (videoCaptureActivity.isRecording()) {
					videoCaptureActivity.stopRecording();
				} else {
					videoCaptureActivity.setRecording(videoCaptureActivity.startRecording());
				}
			}
		});

		mAcceptBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				videoCaptureActivity.finishCompleted();
			}
		});
		mDeclineBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				videoCaptureActivity.finishCancelled();
			}
		});

		mThumbnailIv = (ImageView) container2.findViewById(R.id.videocapture_preview_iv);
		mSurfaceView = (SurfaceView) container2.findViewById(R.id.videocapture_preview_sv);
	}

	public SurfaceView getSurfaceView() {
		return mSurfaceView;
	}

	public void updateUINotRecording() {
		mRecordBtnIv.setSelected(false);
		mRecordBtnIv.setVisibility(View.VISIBLE);
		mAcceptBtnIv.setVisibility(View.GONE);
		mDeclineBtnIv.setVisibility(View.GONE);
		mThumbnailIv.setVisibility(View.GONE);
		mSurfaceView.setVisibility(View.VISIBLE);
	}

	void updateUIRecordingOngoing() {
		mRecordBtnIv.setSelected(true);
		mRecordBtnIv.setVisibility(View.VISIBLE);
		mAcceptBtnIv.setVisibility(View.GONE);
		mDeclineBtnIv.setVisibility(View.GONE);
		mThumbnailIv.setVisibility(View.GONE);
		mSurfaceView.setVisibility(View.VISIBLE);
	}

	void updateUIRecordingFinished(Bitmap videoThumbnail) {
		mRecordBtnIv.setVisibility(View.INVISIBLE);
		mAcceptBtnIv.setVisibility(View.VISIBLE);
		mDeclineBtnIv.setVisibility(View.VISIBLE);
		mThumbnailIv.setVisibility(View.VISIBLE);
		mSurfaceView.setVisibility(View.GONE);
		final Bitmap thumbnail = videoThumbnail;
		if (thumbnail != null) {
			mThumbnailIv.setBackgroundDrawable(new BitmapDrawable(thumbnail));
		}
	}

}

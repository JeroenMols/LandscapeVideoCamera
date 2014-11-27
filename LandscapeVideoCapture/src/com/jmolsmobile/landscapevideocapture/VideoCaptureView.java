package com.jmolsmobile.landscapevideocapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jmolsmobile.landscapevideocapture.R.id;

/**
 * @author Jeroen Mols
 */
public class VideoCaptureView extends FrameLayout {

	private ImageView			mDeclineBtnIv;
	private ImageView			mAcceptBtnIv;
	private ImageView			mRecordBtnIv;
	SurfaceView					mSurfaceView;
	ImageView					mThumbnailIv;

	private RecordingInterface	mRecordingInterface;

	public VideoCaptureView(Context context) {
		super(context);
		initialize(context);
	}

	public VideoCaptureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public VideoCaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}

	public void setRecordingInterface(RecordingInterface mRecordingInterface) {
		this.mRecordingInterface = mRecordingInterface;
	}

	public SurfaceView getSurfaceView() {
		return mSurfaceView;
	}

	private void initialize(Context context) {
		final View videoCapture = View.inflate(context, R.layout.view_videocapture, this);

		mRecordBtnIv = (ImageView) videoCapture.findViewById(id.videocapture_recordbtn_iv);
		mAcceptBtnIv = (ImageView) videoCapture.findViewById(id.videocapture_acceptbtn_iv);
		mDeclineBtnIv = (ImageView) videoCapture.findViewById(id.videocapture_declinebtn_iv);

		mRecordBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (mRecordingInterface.isRecording()) {
					mRecordingInterface.stopRecording();
				} else {
					mRecordingInterface.setRecording(mRecordingInterface.startRecording());
				}
			}
		});

		mAcceptBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				mRecordingInterface.finishCompleted();
			}
		});
		mDeclineBtnIv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				mRecordingInterface.finishCancelled();
			}
		});

		mThumbnailIv = (ImageView) videoCapture.findViewById(R.id.videocapture_preview_iv);
		mSurfaceView = (SurfaceView) videoCapture.findViewById(R.id.videocapture_preview_sv);
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

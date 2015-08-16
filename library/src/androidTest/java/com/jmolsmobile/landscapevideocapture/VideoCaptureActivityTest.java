/**
 * Copyright 2014 Jeroen Mols
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmolsmobile.landscapevideocapture;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

public class VideoCaptureActivityTest extends ActivityInstrumentationTestCase2<VideoCaptureActivity> {

	public VideoCaptureActivityTest() {
		super(VideoCaptureActivity.class);
	}

	public void test_shouldReturnDefaultFilename() {
		final VideoFile videoFile = getActivity().generateOutputFile(null);
		assertTrue(getFilename(videoFile).matches("video_[0-9]{8}_[0-9]{6}\\.mp4"));
	}

	public void test_shouldReturnSpecifiedFilename() {
		final String expectedFilename = "video.mp4";
		setIntentWithFilename(expectedFilename);
		final VideoFile videoFile = getActivity().generateOutputFile(null);

		assertEquals(getFilename(videoFile), expectedFilename);
	}

	public void test_shouldReturnSavedFilename() {
		final String expectedFilename = "saved.mp4";
		final Bundle savedBundle = new Bundle();
		savedBundle.putString(VideoCaptureActivity.SAVED_OUTPUT_FILENAME, expectedFilename);
		setIntentWithFilename("video.mp4");
		final VideoFile videoFile = getActivity().generateOutputFile(savedBundle);

		assertEquals(getFilename(videoFile), expectedFilename);
	}

	private String getFilename(final VideoFile videoFile) {
		return videoFile.getFile().getName();
	}

	private void setIntentWithFilename(final String expectedFilename) {
		final Intent fileNameIntent = new Intent();
		fileNameIntent.putExtra(VideoCaptureActivity.EXTRA_OUTPUT_FILENAME, expectedFilename);
		getActivity().setIntent(fileNameIntent);
	}
}

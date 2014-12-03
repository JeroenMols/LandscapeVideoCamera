package com.jmolsmobile.landscapevideocapture.configuration;

import junit.framework.TestCase;

import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureQuality;
import com.jmolsmobile.landscapevideocapture.configuration.PredefinedCaptureConfigurations.CaptureResolution;

/**
 * @author Jeroen Mols
 */
public class CaptureConfigurationTest extends TestCase {

	public void test_defaultConfiguration() throws Exception {
		final CaptureConfiguration config = new CaptureConfiguration();

		checkConfiguration(config, CaptureResolution.RES_720P.width, CaptureResolution.RES_720P.height,
				CaptureResolution.RES_720P.getBitrate(CaptureQuality.HIGH), CaptureConfiguration.NO_DURATION_LIMIT,
				CaptureConfiguration.NO_FILESIZE_LIMIT);
	}

	public void test_predefinedConfiguration() throws Exception {
		final CaptureConfiguration config = new CaptureConfiguration(CaptureResolution.RES_2160P, CaptureQuality.LOW);

		checkConfiguration(config, CaptureResolution.RES_2160P.width, CaptureResolution.RES_2160P.height,
				CaptureResolution.RES_2160P.getBitrate(CaptureQuality.LOW), CaptureConfiguration.NO_DURATION_LIMIT,
				CaptureConfiguration.NO_FILESIZE_LIMIT);
	}

	public void test_predefinedConfigurationWithSizeDuration() throws Exception {
		final CaptureConfiguration config = new CaptureConfiguration(CaptureResolution.RES_1080P,
				CaptureQuality.MEDIUM, 50, 10);

		checkConfiguration(config, CaptureResolution.RES_1080P.width, CaptureResolution.RES_1080P.height,
				CaptureResolution.RES_1080P.getBitrate(CaptureQuality.MEDIUM), 50 * 1000, 10 * 1024 * 1024);
	}

	public void test_configurationWithHeightWidthBitrate() throws Exception {
		final CaptureConfiguration config = new CaptureConfiguration(200, 300, 5000000);

		checkConfiguration(config, 200, 300, 5000000, CaptureConfiguration.NO_DURATION_LIMIT,
				CaptureConfiguration.NO_FILESIZE_LIMIT);
	}

	public void test_configurationWithHeightWidthBitrateSizeDuration() throws Exception {
		final CaptureConfiguration config = new CaptureConfiguration(200, 300, 5000000, 1, 100);

		checkConfiguration(config, 200, 300, 5000000, 1 * 1000, 100 * 1024 * 1024);
	}

	private void checkConfiguration(final CaptureConfiguration config, int width, int height, int bitrate,
			int duration, int filesize) {
		assertEquals(config.getVideoWidth(), width);
		assertEquals(config.getVideoHeight(), height);
		assertEquals(config.getVideoBitrate(), bitrate);
		assertEquals(config.getMaxCaptureDuration(), duration);
		assertEquals(config.getMaxCaptureFileSize(), filesize);
	}
}

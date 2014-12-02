package com.jmolsmobile.landscapevideocapture;

public class PredefinedCaptureConfigurations {

	public static final int	BITRATE_LQ_360P		= 300000;
	public static final int	BITRATE_MQ_360P		= 700000;
	public static final int	BITRATE_HQ_360P		= 1000000;
	public static final int	HEIGHT_360P			= 360;
	public static final int	WIDTH_360P			= 640;

	public static final int	BITRATE_LQ_480P		= 750000;
	public static final int	BITRATE_MQ_480P		= 1750000;
	public static final int	BITRATE_HQ_480P		= 2500000;
	public static final int	HEIGHT_480P			= 480;
	public static final int	WIDTH_480P			= 854;

	public static final int	BITRATE_LQ_720P		= 1500000;
	public static final int	BITRATE_MQ_720P		= 3500000;
	public static final int	BITRATE_HQ_720P		= 5000000;
	public static final int	HEIGHT_720P			= 720;
	public static final int	WIDTH_720P			= 1280;

	public static final int	BITRATE_LQ_1080P	= 2400000;
	public static final int	BITRATE_MQ_1080P	= 5600000;
	public static final int	BITRATE_HQ_1080P	= 8000000;
	public static final int	HEIGHT_1080P		= 1080;
	public static final int	WIDTH_1080P			= 1920;

	public static final int	BITRATE_LQ_1440P	= 3000000;
	public static final int	BITRATE_MQ_1440P	= 7000000;
	public static final int	BITRATE_HQ_1440P	= 10000000;
	public static final int	HEIGHT_1440P		= 1440;
	public static final int	WIDTH_1440P			= 2560;

	public static final int	BITRATE_LQ_2160P	= 12000000;
	public static final int	BITRATE_MQ_2160P	= 28000000;
	public static final int	BITRATE_HQ_2160P	= 40000000;
	public static final int	HEIGHT_2160P		= 2160;
	public static final int	WIDTH_2160P			= 3840;

	public enum CaptureQuality {
		LOW, MEDIUM, HIGH;
	}

	public enum CaptureResolution {

		RES_360P(WIDTH_360P, HEIGHT_360P, BITRATE_HQ_360P, BITRATE_MQ_360P, BITRATE_LQ_360P),    		// LD 
		RES_480P(WIDTH_480P, HEIGHT_480P, BITRATE_HQ_480P, BITRATE_MQ_480P, BITRATE_LQ_480P),    		// SD
		RES_720P(WIDTH_720P, HEIGHT_720P, BITRATE_HQ_720P, BITRATE_MQ_720P, BITRATE_LQ_720P),   		// HD ready
		RES_1080P(WIDTH_1080P, HEIGHT_1080P, BITRATE_HQ_1080P, BITRATE_MQ_1080P, BITRATE_LQ_1080P), 	// HD 
		RES_1440P(WIDTH_1440P, HEIGHT_1440P, BITRATE_HQ_1440P, BITRATE_MQ_1440P, BITRATE_LQ_1440P), 	// 2K
		RES_2160P(WIDTH_2160P, HEIGHT_2160P, BITRATE_HQ_2160P, BITRATE_MQ_2160P, BITRATE_LQ_2160P); 	// 4K

		public int			width;
		public int			height;
		private final int	lowBitrate;
		private final int	medBitrate;
		private final int	highBitrate;

		private CaptureResolution(int width, int height, int lowBitrate, int medBitrate, int highBitrate) {
			this.width = width;
			this.height = height;
			this.lowBitrate = lowBitrate;
			this.medBitrate = medBitrate;
			this.highBitrate = highBitrate;
		}

		public int getBitrate(CaptureQuality quality) {
			switch (quality) {
			case HIGH:
				return highBitrate;
			case MEDIUM:
				return medBitrate;
			case LOW:
				return lowBitrate;
			default:
				return highBitrate;
			}
		}

	}

}

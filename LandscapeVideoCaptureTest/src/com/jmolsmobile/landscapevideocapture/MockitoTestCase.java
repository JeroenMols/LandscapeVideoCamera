package com.jmolsmobile.landscapevideocapture;

import android.test.InstrumentationTestCase;

public class MockitoTestCase extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
		super.setUp();
	}

}

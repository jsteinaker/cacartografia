package com.jsteinaker.cacartografia;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.jsteinaker.cacartografia.DUALCTest \
 * com.jsteinaker.cacartografia.tests/android.test.InstrumentationTestRunner
 */
public class DUALCTest extends ActivityInstrumentationTestCase2<DUALC> {

    public DUALCTest() {
        super("com.jsteinaker.cacartografia", DUALC.class);
    }

}

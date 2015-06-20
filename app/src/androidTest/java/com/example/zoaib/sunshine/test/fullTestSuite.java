package com.example.zoaib.sunshine.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by Zoaib on 6/20/2015.
 */
public class fullTestSuite {
    public static Test suite()
    {
       return new TestSuiteBuilder(fullTestSuite.class)
               .includeAllPackagesUnderHere().build();

    }

    public fullTestSuite()
    {
        super();
    }

}


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.integrationtests;

import java.io.File;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.IntegrationTestRunner;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-2068">MNG-2068</a>.
 *
 * Verify that a multimodule build, built from the middle node in an inheritance hierarchy,
 * can find all parent POMs necessary to build each project in the reactor using ONLY the
 * relativePath from the parent specification (in this case, the implied one of '../pom.xml').
 * 
 * @author jdcasey
 * 
 */
public class MavenITmng2068ReactorRelativeParentsTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng2068ReactorRelativeParentsTest()
        throws InvalidVersionSpecificationException
    {
        super( "(2.0.6,)" ); // only test in 2.0.7+
    }

    public void testitMNG2068 ()
        throws Exception
    {
        // The testdir is computed from the location of this
        // file.
        File testDir = extractTestResources( getClass(), "/mng-2068-reactorRelativeParents" );
        File projectDir = new File( testDir, "frameworks" );

        IntegrationTestRunner itr;

        /*
         * We must first make sure that any artifact created
         * by this test has been removed from the local
         * repository. Failing to do this could cause
         * unstable test results. Fortunately, the itr
         * makes it easy to do this.
         */
        itr = new IntegrationTestRunner( projectDir.getAbsolutePath() );

        itr.deleteArtifact( "samplegroup", "master", "0.0.1", "pom" );
        itr.deleteArtifact( "samplegroup", "frameworks", "0.0.1", "pom" );
        itr.deleteArtifact( "samplegroup", "core", "1.0.0", "pom" );

        itr.invoke( "validate" );

        /*
         * This is the simplest way to check a build
         * succeeded. It is also the simplest way to create
         * an IT test: make the build pass when the test
         * should pass, and make the build fail when the
         * test should fail. There are other methods
         * supported by the itr. They can be seen here:
         * http://maven.apache.org/shared/maven-itr/apidocs/index.html
         */
        itr.verifyErrorFreeLog();

        /*
         * Reset the streams before executing the itr
         * again.
         */
        itr.resetStreams();
    }
}

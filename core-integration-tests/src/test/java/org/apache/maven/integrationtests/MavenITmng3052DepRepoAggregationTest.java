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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.IntegrationTestRunner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * This is a test set for <a href="http://jira.codehaus.org/browse/MNG-3052">MNG-3052</a>.
 *
 * When a project dependency declares its own repositories, they should be used to
 * resolve that dependency's dependencies. This includes both steps: determining
 * the dependency artifact information (version, etc.) AND resolving the actual
 * artifact itself.
 *
 * NOTE: The SNAPSHOT versions are CRITICAL in this test, since they force the
 * correct resolution of artifact metadata, which depends on having the correct
 * set of repositories present.
 *
 * @author jdcasey
 */
public class MavenITmng3052DepRepoAggregationTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng3052DepRepoAggregationTest()
        throws InvalidVersionSpecificationException
    {
        super( "(2.0.9,)" ); // only test in 2.0.10+
    }

    public void testitMNG3052 ()
        throws Exception
    {
        File testDir = extractTestResources( getClass(), "/mng-3052depRepoAggregation" )
                                        .getCanonicalFile();

        File foo = new File( testDir, "foo" );
        File bar = new File( testDir, "bar" );
        File wombat = new File( testDir, "wombat" );

        // Since this test relies on artifact deployment, we need to provide two
        // "remote" repository locations into which the foo and bar project builds
        // can be deployed, for eventual resolution from the wombat project build.
        //
        // To do this, we're using "remote" locations in the testDir/target directory,
        // do they will be cleaned up when the test directory is cleaned. The
        // commands below substitute the current testDir location into the
        // repository declarations, to make them absolute file references on the
        // local filesystem.
        rewritePom( new File( foo, "pom.xml" ), testDir );
        rewritePom( new File( bar, "pom.xml" ), testDir );
        rewritePom( new File( wombat, "pom.xml" ), testDir );

        List cliOptions = new ArrayList();
        cliOptions.add( "-X" );

        IntegrationTestRunner itr;

        // First, build the two levels of dependencies that will be resolved.

        // This one is a transitive dependency, and will be deployed to a
        // repository that is NOT listed in the main project's POM (wombat).
        itr = new IntegrationTestRunner( foo.getAbsolutePath() );
        itr.executeGoal( "deploy", cliOptions );
        itr.verifyErrorFreeLog();
        itr.resetStreams();

        // This one is a direct dependency that will be deployed to a repository
        // that IS listed in the main project's POM (wombat). It lists its own
        // repository entry that should enable resolution of the transitive
        // dependency it lists (foo, above).
        itr = new IntegrationTestRunner( bar.getAbsolutePath() );
        itr.executeGoal( "deploy", cliOptions );
        itr.verifyErrorFreeLog();
        itr.resetStreams();

        String artifactPath = itr.getArtifactPath( "org.mule", "mule-foo", "1.0-SNAPSHOT", "jar" );
        File artifact = new File( artifactPath );

        File dir = artifact.getParentFile().getParentFile().getParentFile();
        FileUtils.deleteDirectory( dir );

        // This is the main project, which lists a repository where the bar
        // project (above) was deployed. It should be able to use the
        // repositories declared in the bar POM to find the transitive dependency
        // (foo, top).
        itr = new IntegrationTestRunner( wombat.getAbsolutePath() );
        itr.executeGoal( "package", cliOptions );
        itr.verifyErrorFreeLog();
        itr.resetStreams();
    }

    private void rewritePom( File pomFile,
                             File testDir )
        throws IOException
    {
        FileReader reader = null;
        String pomContent = null;
        try
        {
            reader = new FileReader( pomFile );
            pomContent = IOUtil.toString( reader );
        }
        finally
        {
            IOUtil.close( reader );
        }

        pomContent = StringUtils.replace( pomContent, "@testDir@", testDir.getAbsolutePath() );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( pomFile );
            writer.write( pomContent );
        }
        finally
        {
            IOUtil.close( writer );
        }
    }
}

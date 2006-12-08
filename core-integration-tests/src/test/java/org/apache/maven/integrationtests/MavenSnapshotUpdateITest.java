package org.apache.maven.integrationtests;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.FileUtils;
import org.apache.maven.it.util.ResourceExtractor;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Test to verify that a snapshot can be updated, even if the
 */
public class MavenSnapshotUpdateITest
    extends AbstractMavenIntegrationTestCase
{

    /**
     * Test plugin configuration and goal configuration that overrides what the
     * mojo has specified.
     */
    public void testSnapshotUpdated()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/snapshotUpdate" );
        Verifier verifier = new Verifier( testDir.getAbsolutePath() );
        verifier.deleteArtifact( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar" );


        // create a repository (TODO: into verifier)
        File repository = new File( testDir, "repository" );

        repository.mkdirs();

        // create artifact in repository (TODO: into verifier)
        File artifact = new File( repository,
                                  "org/apache/maven/maven-core-it-support/1.0-SNAPSHOT/maven-core-it-support-1.0-SNAPSHOT.jar" );
        artifact.getParentFile().mkdirs();
        FileUtils.fileWrite( artifact.getAbsolutePath(), "originalArtifact" );
        verifier.assertArtifactNotPresent( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar" );

        verifier.executeGoal( "package" );

        verifier.assertArtifactPresent( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar" );
        verifier.assertArtifactContents( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar",
                                         "originalArtifact" );
        File localRepoFile =
            new File( verifier.getArtifactPath( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar" ) );
        // set in the past to ensure it is downloaded
        localRepoFile.setLastModified( System.currentTimeMillis() - 5000 );

        FileUtils.fileWrite( artifact.getAbsolutePath(), "updatedArtifact" );

        verifier.executeGoal( "package" );

        verifier.assertArtifactPresent( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar" );
        verifier.assertArtifactContents( "org.apache.maven", "maven-core-it-support", "1.0-SNAPSHOT", "jar", "updatedArtifact" );

        verifier.verifyErrorFreeLog();
        verifier.resetStreams();
    }
}

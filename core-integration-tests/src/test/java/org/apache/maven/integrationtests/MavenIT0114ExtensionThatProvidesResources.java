package org.apache.maven.integrationtests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.IntegrationTestRunner;

public class MavenIT0114ExtensionThatProvidesResources
    extends AbstractMavenIntegrationTestCase
{
    public MavenIT0114ExtensionThatProvidesResources()
        throws InvalidVersionSpecificationException
    {
        super( "(,2.1-SNAPSHOT)" );
    }

    public void testit0114()
        throws Exception
    {
        File testDir =
            extractTestResources( getClass(), "/it0114-extensionThatProvidesResources" );

        IntegrationTestRunner itr;

        // Install the parent POM, extension and the plugin 
        itr = new IntegrationTestRunner( testDir.getAbsolutePath() );
        itr.deleteArtifact( "org.apache.maven.its.it0114", "it0114-plugin-runner", "1.0", "pom" );                
        itr.deleteArtifact( "org.apache.maven.its.it0114", "it0114-extension", "1.0", "jar" );
        itr.deleteArtifact( "org.apache.maven.its.it0114", "it0114-plugin", "1.0", "jar" );
        itr.deleteArtifact( "org.apache.maven.its.it0114", "it0114-parent", "1.0", "pom" );
        
        List cliOptions = new ArrayList();        
        itr.invoke( "install" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();
        
        //now run the test
        testDir =
            extractTestResources( getClass(), "/it0114-extensionThatProvidesResources/test-project" );
        itr = new IntegrationTestRunner( testDir.getAbsolutePath() );
        cliOptions = new ArrayList();
        itr.invoke( "verify" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();
        
    }
}

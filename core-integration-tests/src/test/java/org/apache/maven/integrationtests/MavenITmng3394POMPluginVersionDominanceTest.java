package org.apache.maven.integrationtests;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.DefaultInvocationRequest;
import org.apache.maven.it.IntegrationTestRunner;
import org.apache.maven.it.InvocationRequest;

/**
 * Check that plugin versions in the POM obey the correct order of precedence. Specifically, that
 * mojos in the default lifecycle bindings can find plugin versions in the pluginManagement section
 * when the build/plugins section is missing that plugin, and that plugin versions in build/plugins
 * override those in build/pluginManagement.
 */
public class MavenITmng3394POMPluginVersionDominanceTest
    extends AbstractMavenIntegrationTestCase
{

    private static final String BASEDIR_PREFIX = "/mng-3394-pomPluginVersionDominance/";

    public MavenITmng3394POMPluginVersionDominanceTest()
        throws InvalidVersionSpecificationException
    {
        super( "(2.0.8,)" ); // only test in 2.0.9+
    }

    public void testitMNG3394a()
        throws Exception
    {
        //testShouldUsePluginVersionFromPluginMgmtForLifecycleMojoWhenNotInBuildPlugins 
        File testDir = extractTestResources( getClass(), BASEDIR_PREFIX + "lifecycleMojoVersionInPluginMgmt" );

        IntegrationTestRunner itr;

        itr = new IntegrationTestRunner( testDir.getAbsolutePath() );

        itr.executeGoal( "install", Collections.singletonList( "-X" ) );

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

        List logFile = itr.loadFile( new File( testDir, "log.txt" ), false );

        boolean foundSiteBeta5 = false;
        for ( Iterator it = logFile.iterator(); it.hasNext(); )
        {
            String line = (String) it.next();
            if ( line.indexOf( "maven-site-plugin:2.0-beta-5" ) > -1 )
            {
                foundSiteBeta5 = true;
                break;
            }
        }

        /*
         * Reset the streams before executing the itr
         * again.
         */
        itr.resetStreams();

        assertTrue( "No reference to maven-site-plugin, version 2.0-beta-5 found in build log.", foundSiteBeta5 );
    }

    public void testitMNG3394b()
        throws Exception
    {
        //testShouldPreferPluginVersionFromBuildPluginsOverThatInPluginMgmt
        File testDir = extractTestResources( getClass(), BASEDIR_PREFIX + "preferBuildPluginOverPluginMgmt" );

        IntegrationTestRunner itr;

        itr = new IntegrationTestRunner( testDir.getAbsolutePath() );

        InvocationRequest r = new DefaultInvocationRequest()
            .setGoals( "clean" )
            .setAutoclean( false );

        itr.invoke( r );

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

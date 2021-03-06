package org.apache.maven.integrationtests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.IntegrationTestRunner;


public class MavenITmng2123VersionRangeDependencyTest
    extends AbstractMavenIntegrationTestCase
{
    
    public MavenITmng2123VersionRangeDependencyTest()
        throws InvalidVersionSpecificationException
    {
        super( "(2.0.8,)" );
    }
    
    public void testitMNG2123 ()
        throws Exception
    {
       

        // The testdir is computed from the location of this
        // file.
        File testDir = extractTestResources( getClass(), "/mng-2123-npe-with-conflicting-ranges" );

        IntegrationTestRunner itr;

        /*
         * We must first make sure that any artifact created
         * by this test has been removed from the local
         * repository. Failing to do this could cause
         * unstable test results. Fortunately, the itr
         * makes it easy to do this.
         */
        itr = new IntegrationTestRunner( testDir.getAbsolutePath() );
        itr.deleteArtifact( "org.apache.maven.its.mng2123", "parent", "1.0", "pom" );
        itr.deleteArtifact( "org.apache.maven.its.mng2123", "artifact-combined", "1.0", "jar" );
        itr.deleteArtifact( "org.apache.maven.its.mng2123", "artifact-fix", "1.0", "jar" );
        itr.deleteArtifact( "org.apache.maven.its.mng2123", "artifact-range", "1.0", "jar" );

        /*
         * The Command Line Options (CLI) are passed to the
         * itr as a list. This is handy for things like
         * redefining the local repository if needed. In
         * this case, we use the -N flag so that Maven won't
         * recurse. We are only installing the parent pom to
         * the local repo here.
         */
        List cliOptions = new ArrayList();
        cliOptions.add( "-N" );
        itr.invoke( "install" );

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

        /*
         * Build the artifact with a fix version of commons-collections
         */
        itr = new IntegrationTestRunner( new File( testDir.getAbsolutePath(), "artifact-fix" ).getAbsolutePath() );
        itr.invoke( "install" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();

        /*
         * Build the artifact with a version range of commons-collections
         */
        itr = new IntegrationTestRunner( new File( testDir.getAbsolutePath(), "artifact-range" ).getAbsolutePath() );
        itr.invoke( "install" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();
        
        /*
         * Now we are running the actual test. 
         * This particular test will attempt to build the
         * artifact that uses the artifacts above.
         * On any version >= 2.0.9 it should work
         */
        itr = new IntegrationTestRunner( new File( testDir.getAbsolutePath(), "artifact-combined" ).getAbsolutePath() );
        itr.invoke( "install" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();

    }
}

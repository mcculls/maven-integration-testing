package org.apache.maven.integrationtests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.it.IntegrationTestRunner;

/**
 * Test that pom.xml encoding is properly detected.
 * 
 * @author <a href="mailto:herve.boutemy@free.fr">Herve Boutemy</a>
 * 
 */
public class MavenITmng2254PomEncodingTest
    extends AbstractMavenIntegrationTestCase
{
    public MavenITmng2254PomEncodingTest()
        throws InvalidVersionSpecificationException
    {
        super( "(2.0.7,)" ); // 2.0.8+
    }

    public void testitMNG2254 ()
        throws Exception
    {
        // The testdir is computed from the location of this
        // file.
        File testDir = extractTestResources( getClass(), "/mng-2254-PomEncoding" );

        IntegrationTestRunner itr = new IntegrationTestRunner( testDir.getAbsolutePath() );

        List cliOptions = new ArrayList();
        cliOptions.add( "-N" );
        itr.invoke( "compile" );

        itr.verifyErrorFreeLog();

        itr.resetStreams();
    }
}

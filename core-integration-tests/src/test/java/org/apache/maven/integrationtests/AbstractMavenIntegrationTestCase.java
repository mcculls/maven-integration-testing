package org.apache.maven.integrationtests;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.it.DefaultInvocationRequest;
import org.apache.maven.it.IntegrationTestException;
import org.apache.maven.it.IntegrationTestRunner;
import org.apache.maven.it.InvocationRequest;
import org.apache.maven.it.ResourceExtractor;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public abstract class AbstractMavenIntegrationTestCase
    extends TestCase
{
    /**
     * Save System.out for progress reports etc.
     */
    private static PrintStream out = System.out;

    private boolean skip;

    private DefaultArtifactVersion version;

    private VersionRange versionRange;

    protected AbstractMavenIntegrationTestCase()
    {
    }

    protected AbstractMavenIntegrationTestCase( String versionRangeStr )
        throws InvalidVersionSpecificationException
    {
        versionRange = VersionRange.createFromVersionSpec( versionRangeStr );

        String v = System.getProperty( "maven.version" );
        if ( v != null )
        {
            version = new DefaultArtifactVersion( v );
            if ( !versionRange.containsVersion( version ) )
            {
                skip = true;
            }
        }
        else
        {
            out.println( "WARNING: " + getITName() + ": version range '" + versionRange + "' supplied but no maven version - not skipping test." );
        }
    }

    /**
     * This allows fine-grained control over execution of individual test methods by allowing tests
     * to adjust to the current maven version, or else simply avoid executing altogether if the
     * wrong version is present.
     */
    protected boolean matchesVersionRange( String versionRangeStr )
        throws InvalidVersionSpecificationException
    {
        versionRange = VersionRange.createFromVersionSpec( versionRangeStr );

        String v = System.getProperty( "maven.version" );
        if ( v != null )
        {
            version = new DefaultArtifactVersion( v );
            return versionRange.containsVersion( version );
        }
        else
        {
            out.println( "WARNING: " + getITName() + ": version range '" + versionRange + "' supplied but no maven version found - returning true for match check." );

            return true;
        }
    }

    protected void runTest()
        throws Throwable
    {
        out.print( getITName() + "(" + getName() + "[" + getLocation() + "]).." );

        if ( skip )
        {
            out.println( " Skipping (version " + version + " not in range " + versionRange + ")" );
            return;
        }

        if ( "true".equals( System.getProperty( "useEmptyLocalRepository", "false" ) ) )
        {
            setupLocalRepo();
        }

        try
        {
            super.runTest();
            out.println( " Ok" );
        }
        catch ( Throwable t )
        {
            out.println( " Failure" );
            throw t;
        }
    }

    private String getITName()
    {
        String simpleName = getClass().getName();
        int idx = simpleName.lastIndexOf( '.' );
        simpleName = idx >= 0 ? simpleName.substring( idx + 1 ) : simpleName;
        simpleName = simpleName.startsWith( "MavenIT" ) ? simpleName.substring( "MavenIT".length() ) : simpleName;
        simpleName = simpleName.endsWith( "Test" ) ? simpleName.substring( 0, simpleName.length() - 4 ) : simpleName;
        return simpleName;
    }

    protected File setupLocalRepo()
        throws IOException
    {
        String tempDirPath = System.getProperty( "maven.test.tmpdir", System.getProperty( "java.io.tmpdir" ) );
        File localRepo = new File( tempDirPath, "local-repository/" + getITName() );
        if ( localRepo.isDirectory() )
        {
            FileUtils.deleteDirectory( localRepo );
        }

        System.setProperty( "maven.repo.local", localRepo.getAbsolutePath() );

        return localRepo;
    }

    public File extractTestResources( Class clazz, String location )
        throws IOException
    {
        return ResourceExtractor.simpleExtractResources( clazz, location );
    }

    public InvocationRequest createInvocationRequest( String goals )
    {
        return new DefaultInvocationRequest().setGoals( goals );
    }

    private String getLocation()
    {
        String location = getClass().getName().substring( getClass().getPackage().getName().length() + 6 ).toLowerCase();
        location = location.substring( 0, location.length() - 4 );
        return location;
    }
    
    // The end goal will be to pull the resources required for a test based on naming. Right now we can't
    // because no one stuck to the original naming.
    public IntegrationTestRunner createTestRunner()
        throws IOException, IntegrationTestException
    {
        String location = getLocation();
        System.out.println( location);
        File basedir = extractTestResources( getClass(), location );
        IntegrationTestRunner runner = new IntegrationTestRunner( basedir );
        return runner;
    }

    public IntegrationTestRunner createTestRunner( String location )
        throws IOException, IntegrationTestException
    {
        if ( !location.startsWith( "/" ) )
        {
            location = "/" + location;
        }

        File basedir = extractTestResources( getClass(), location );
        IntegrationTestRunner runner = new IntegrationTestRunner( basedir );
        return runner;
    }
}

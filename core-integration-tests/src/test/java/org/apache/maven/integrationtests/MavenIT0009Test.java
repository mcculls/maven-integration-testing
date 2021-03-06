package org.apache.maven.integrationtests;

import org.apache.maven.it.IntegrationTestRunner;

public class MavenIT0009Test
    extends AbstractMavenIntegrationTestCase
{
    /**
     * Test plugin configuration and goal configuration that overrides what the
     * mojo has specified.
     */
    public void testit0009()
        throws Exception
    {
        IntegrationTestRunner itr = createTestRunner();
        itr.deleteArtifact( "org.apache.maven.its.plugins", "maven-it-plugin-touch", "1.0", "maven-plugin" );
        itr.invoke( "generate-resources" );
        itr.assertFilePresent( "target/pluginItem" );
        itr.assertFilePresent( "target/goalItem" );
        itr.assertFileNotPresent( "target/bad-item" );
        itr.verifyErrorFreeLog();
        itr.resetStreams();
    }
}


package org.apache.maven.it0056;

import junit.framework.TestCase;

public class PersonTest
    extends TestCase
{
    public void testPerson()
    {
        Person person = new Person();
        
        person.setName( "foo" );
        
        assertEquals( "foo", person.getName() );
    }
}

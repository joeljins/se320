/*
In class we discussed passing a collaborator, factory methods, and external factories.
I chose to refactor the code by passing a collaborator to the Client constructor.
I felt this would be the most straightforward approach. With this approach, the only additional effort I had to put in was
modifying the constructor to accept a ServerConnection object. After passing in the mock ServerConnection object,
I did not to call any methods on the Client object but just the mock object. 
If I had chosen to use a factory method, I would have needed to create a method in the Client class that returns a ServerConnection object. 
Although this would not have been necessary for this assignment, 
using a different implementation instead of the default ServerConnection would require overriding the factory method in a subclass of Client.
Jf I chose to use an external factory, I would have to create a new class that implements the ServerConnectionFactory interface.

*/
package edu.drexel.se320;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InOrder;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MockTests {

    public MockTests() {}

    /**
     * Demonstrate a working mock from the Mockito documentation.
     * https://static.javadoc.io/org.mockito/mockito-core/latest/org/mockito/Mockito.html#1
     */
    @Test
    public void testMockDemo() {
         List<String> mockedList = (List<String>)mock(List.class);

         mockedList.add("one");
         mockedList.clear();

         verify(mockedList).add("one");
         verify(mockedList).clear();
    }

    @Test
    public void testServerConnectionFailureGivesNull() throws IOException {
        // Client c = new Client();
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);
        when(sc.connectTo(anyString())).thenReturn(false);

        // If you change the code to pass the mock above to the client (based on your choice of
        // refactoring), this test should pass.  Until then, it will fail.
        assertNull(c.requestFile("DUMMY", "DUMMY"));
    }

    // Test that if the attempt to connectTo(...) the server fails, the client code calls no further methods on the connection.
    @Test 
    public void test1() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(false);

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, never()).requestFileContents(anyString());
        verify(sc, never()).read();
        verify(sc, never()).moreBytes();
        verify(sc, never()).closeConnection();
    }

    // Test that if the connection succeeds but there is no valid file of that name, 
    // the client code calls no further methods on the connection except closeConnection. 
    // That is, the client code is expected to call closeConnection exactly once, 
    // but should not call other methods after it is known the file name is invalid.
    // **Expected to fail**
    @Test 
    public void test2() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(false);

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, never()).read();
        verify(sc, never()).moreBytes();
        verify(sc, times(1)).closeConnection();
    }

    // Test that if the connection succeeds and the file is valid and non-empty, 
    // that the connection asks for at least some part of the file. 
    // (We want you to check that the client makes the request; examining the return value is insufficient for this.)
    @Test 
    public void test3() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        // when(sc.moreBytes()).thenReturn(true);
        // when(sc.read()).thenReturn("DUMMY");

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, atLeastOnce()).moreBytes();
    }

    // Test that if the connection succeeds and the file is valid but empty, the client returns an empty string.
    @Test 
    public void test4() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(false);

        assertEquals("", c.requestFile("DUMMY", "DUMMY"));
    }

    // Test that if the client successfully reads part of a file, 
    // and then an IOException occurs before the file is fully read (i.e., moreBytes() has not returned false), 
    // the client still returns null to indicate an error, rather than returning a partial result.
    @Test 
    public void test5() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true);
        when(sc.read()).thenReturn("DUMMY").thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));
    }

    // Test that if the initial server connection succeeds, 
    // then if an IOException occurs while retrieving the file (requesting, or reading bytes, either one)
    // the client still explicitly closes the server connection.
    // **Expected to fail**?
    @Test
    public void test6() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).closeConnection();
    }

    // Test that if the initial connection succeeds and the file is valid and non-empty, the client calls moreBytes() before it calls read().
    @Test
    public void test7() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true);
        when(sc.read()).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));
 
        InOrder order = inOrder(sc);
        order.verify(sc).moreBytes();
        order.verify(sc).read();
    }

    // If the server returns the file in four pieces (i.e., four calls to read() must be executed), 
    // the client concatenates them in the correct order).
    @Test
    public void test8() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(sc.read()).thenReturn("1").thenReturn("2").thenReturn("3").thenReturn("4");

        assertEquals("1234", c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(4)).read();
    }

    // If read() ever returns null, the client treats this as the empty string.
    //This stands in contrast to appending "null" to the file contents read thus far, 
    // which is the default if you simply append null. In Java, "asdf"+null evaluates to "asdfnull".
    @Test
    public void test9() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true).thenReturn(false);
        when(sc.read()).thenReturn(null);

        assertEquals("", c.requestFile("DUMMY", "DUMMY"));

        verify(sc, atLeastOnce()).read();
    } 

    // Test that if any of the connection operations fails the first time it is executed with an IOException, 
    // the client returns null.
    // For connectTo()
    @Test
    public void test10a() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, never()).requestFileContents(anyString());
        verify(sc, never()).moreBytes();
        verify(sc, never()).read();
        verify(sc, never()).closeConnection();
    } 

    // For requestFileContents()
    @Test
    public void test10b() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, times(1)).requestFileContents(anyString());
        verify(sc, never()).moreBytes();
        verify(sc, never()).read();
        verify(sc, never()).closeConnection();
    } 

    // For moreBytes()
    @Test
    public void test10c() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, times(1)).requestFileContents(anyString());
        verify(sc, times(1)).moreBytes();
        verify(sc, never()).read();
        verify(sc, never()).closeConnection();
    }

    // For read()
    @Test
    public void test10d() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true);
        when(sc.read()).thenThrow(new IOException());

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, times(1)).requestFileContents(anyString());
        verify(sc, times(1)).moreBytes();
        verify(sc, times(1)).read();
        verify(sc, never()).closeConnection();
    } 

// For closeConnection()
    @Test
    public void test10e() throws IOException {
        ServerConnection sc = mock(ServerConnection.class);
        Client c = new Client(sc);

        when(sc.connectTo(anyString())).thenReturn(true);
        when(sc.requestFileContents(anyString())).thenReturn(true);
        when(sc.moreBytes()).thenReturn(true).thenReturn(false);
        when(sc.read()).thenReturn("DUMMY");
        // when(sc.closeConnection()).thenThrow(new IOException());
        doThrow(new IOException()).when(sc).closeConnection();

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, times(1)).requestFileContents(anyString());
        verify(sc, times(2)).moreBytes();
        verify(sc, times(1)).read();
        verify(sc, times(1)).closeConnection();
    }

}

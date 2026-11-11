package edu.drexel.se320;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InOrder;
import static org.mockito.Mockito.atLeastOnce;
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

        // verify(sc, never()).requestFileContents(anyString());
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




}

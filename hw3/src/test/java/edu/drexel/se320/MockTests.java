/*
Methods discussed in class:
- passing a collaborator
- factory method
- external factory

I chose: passing a collaborator

The original Client built its own ServerConnection inside requestFile(), so a test
had no way to substitute a mock. I changed the Client constructor to accept a
ServerConnection object and store it in a field. requestFile() now calls the methods
on that object rather than creating a new one. Each test creates a mock with Mockito,
passes it to the constructor, and then verifies how the client interacts with it.

Why I chose this over the alternatives discussed in class:

- Factory method: I would need to write a method like createConnection() that returns
  a ServerConnection object, and each test would need a Client subclass overriding it to
  return a reference to a mock that I created outside of the class. That is extra
  boilerplate in every test, and it only works if Client is subclassable and the
  method is overridable.

- External factory: I would need to create a ServerConnectionFactory interface and
  an implementation, and then pass a factory into the Client. In the tests I would
  have to supply a factory whose create() method returns a mock that I created
  outside of the class. That adds extra production classes.

- Passing a collaborator only needs a small change to the constructor. The test has
  a reference to the mock and passes it in directly, without overriding a method or
  writing a factory to return it.

- Tradeoff: I cannot refresh the connection by creating a new connection object inside Client, 
    which would be an option for me with the other two approaches.
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
        when(sc.moreBytes()).thenReturn(true).thenReturn(false);
        when(sc.read()).thenReturn("DUMMY");

        c.requestFile("DUMMY", "DUMMY");

        verify(sc, atLeastOnce()).requestFileContents(anyString());
        verify(sc, atLeastOnce()).read();
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
    // **Expected to fail**
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
        doThrow(new IOException()).when(sc).closeConnection();

        assertNull(c.requestFile("DUMMY", "DUMMY"));

        verify(sc, times(1)).connectTo(anyString());
        verify(sc, times(1)).requestFileContents(anyString());
        verify(sc, times(2)).moreBytes();
        verify(sc, times(1)).read();
    }

}

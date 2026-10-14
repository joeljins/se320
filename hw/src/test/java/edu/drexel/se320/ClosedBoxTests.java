package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ClosedBoxTests extends BinarySearchBase {

    /**
     * This is just an example of how to call the generic find method which dispatches to your binary search. 
     * Recall that List<int> is not a permitted Java type, but List<Integer> is, and the
     * Java compiler automatically converts values between int and Integer when needed,
     * but generic types must use Integer (or Char, Double, etc. rather than other primitives).
     * For regular object types (e.g., String) there's nothing special to do, just use the class.
    */
    public void exampleCallToDelete() {
        // Java generics do not treat primitives the same as object types.
        // To pass a primitive type (int, double, etc.) to the find method, you need to actually use the corresponding "boxed" version (Integer, Double, etc.) which is a class-based version of each primitive.
        // For types which are already/always objects, like String, everything will just work.
        // This method is not part of a test, but just a refresher/crash-course for those who may not have tried passing primitives to generic Java methods previously.
        // You should not modify this method; no part of the assignment requires you to do so, and this
        // can simply be deleted.

        Integer[] arr = { 0, 1, 2 };
        find(arr, 1);

        String[] arr2 = { "a", "b", "c", "foo" };
        find(arr2, "c");

    }

    @Test
    public void test1() {
        // Delete this test once you're sure you can run the code.
        assertEquals(0, 0);
    }

    @Test
    public void testExceptions() {
        // Also delete this test after you understand how to check for exceptions.
        Exception exception = assertThrows(UnsupportedOperationException.class,
        /* The bit between this and the next comment is a 
        * *lambda expression*: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#syntax
        * If you've taken PL, this is Java's lambda.
        * If not, this is like special syntax for a function object with one method.
        * Notice the form is:
        *   _args_ -> { body }
        * The body here simply throws an exception so you can see how the exception assertions work, but in general this is where you'd write code you expect to throw an exception when run.
        */
            () -> { throw new UnsupportedOperationException("Bad jasdf;lkasdjlfk"); }
        /* end of lambda expression. The () at the beginning is the (empty)
        * argument list of the lambda */
        ); // <-- This is closing off the call to assertThrows
        // Here you can use any methods on the Exception type to inspect whether the right exception was thrown.
        assertEquals("Bad Operation", exception.getMessage());   
    }

}


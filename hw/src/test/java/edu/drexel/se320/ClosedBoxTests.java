package edu.drexel.se320;

// Hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.lessThan;

// Core JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

import jdk.jfr.Timestamp;

import java.util.NoSuchElementException;

public class ClosedBoxTests extends BinarySearchBase {

    /**
     * This is just an example of how to call the generic find method which dispatches to your binary search. 
     * Recall that List<int> is not a permitted Java type, but List<Integer> is, and the
     * Java compiler automatically converts values between int and Integer when needed,
     * but generic types must use Integer (or Char, Double, etc. rather than other primitives).
     * For regular object types (e.g., String) there's nothing special to do, just use the class.
    */

    // Partition: Invalid inputs

    // 1. Null array
    @Test
    public void nullArray() {
        Integer[] arr = null;
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, 1); }
        );
        assertEquals("Array is null", exception.getMessage());
    }

    // 2. Empty array
    @Test
    public void emptyArray() {
        Integer[] arr = {};
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, 1); }
        );
        assertEquals("Array is empty", exception.getMessage());
    }

    // 3. Null element
    @Test
    public void nullElement() {
        Integer[] arr = { 1 };
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, null); }
        );
        assertEquals("Element is null", exception.getMessage());
    }

    // Partition: Edge cases and boundaries depending on array size

    // 1. Search for an element in array size one
    @Test
    public void searchElementArraySizeOne() {
        Integer[] arr = { 1 };
        assertEquals(find(arr, 1), 0);
    }

    // 2. Search for an element in an odd size array
    @Test
    public void searchElementArrayOdd() {
        Integer[] arr = { 1, 2, 3, 4, 5 };
        assertEquals(find(arr, 3), 2);
    }

    // 3. Search for an element in an even size array (upper mid index)
    @Test
    public void searchElementArrayEven() {
        Integer[] arr = { 1, 2, 3, 4 };
        assertEquals(find(arr, 3), 2);
    }

    // 4. Search for an element in an even size array (lower mid index)
    @Test
    public void searchElementArrayEvenLowerMid() {
        Integer[] arr = { 1, 2, 3, 4 };
        assertEquals(find(arr, 2), 1);
    }

    // Partition: Edge cases and boundaries depending on array position

    // 1. Search for the first element in an array
    @Test
    public void searchFirstElementInArray() {
        Integer[] arr = { 1, 2, 3, 4, 5};
        assertEquals(find(arr, 1), 0);
    }

    // 2. Search for the last element in an array
    @Test
    public void searchLastElement() {
        Integer[] arr = { 1, 2, 3, 4, 5 };
        assertEquals(find(arr, 5), 4);
    }

    // 3. Search for an element in the middle of the array
    @Test
    public void searchMiddleElement() {
        Integer[] arr = { 1, 2, 3, 4, 5 };
        assertEquals(find(arr, 3), 2);
    }

    // Partition: Edge cases and boundaries for elements not in the array

    // 1. Search for an element not in the array but within the range of the array
    @Test
    public void searchElementNotInArrayButBetweenRange() {
        Integer[] arr = {1, 2, 4, 5};
        Exception exception = assertThrows(NoSuchElementException.class,
            () -> { find(arr, 3); }
        );
        assertEquals("No such element exists in the array", exception.getMessage());

    }
        
    // 2. Search for an element not in the array and smaller than min
    @Test
    public void searchElementNotInArrayAndSmallerThanMin() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Exception exception = assertThrows(NoSuchElementException.class,
            () -> { find(arr, 0); }
        );
        assertEquals("No such element exists in the array", exception.getMessage());
    }

    // 3. Search for an element not in the array and greater than max
    @Test
    public void searchElementNotInArrayAndGreaterThanMax() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Exception exception = assertThrows(NoSuchElementException.class,
            () -> { find(arr, 6); }
        );
        assertEquals("No such element exists in the array", exception.getMessage());
    }

    // Partition: Edge cases and boundaries depending on duplicate elements in the array

    // 1. Search for an element in an array with multiple of that element
    @Test
    public void searchElementInArrayWithElementDuplicates() {
        Integer[] arr = {1, 2, 3, 3, 4, 5};
        boolean result = find(arr, 3) == 2 || find(arr, 3) == 3;
        assertEquals(result, true);
    }

    // 2. Search for an element in an array with multiple of other elements
    @Test
    public void searchElementInArrayWithOtherDuplicates() {
        Integer[] arr = {1, 2, 3, 4, 4, 5, 6, 6};
        assertEquals(find(arr, 2), 1);
    }

    // Partition: Ensure that the array is not modified by the search

    // 1. Searches for an element in an array without changing the array 
    @Test
    public void searchElementWithoutChangingArray() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Integer[] arrCopy = arr.clone();
        find(arr, 3);
        assertArrayEquals(arr, arrCopy);
    }
    
    // 2. Searches for an element not in an array without changing the array 
    @Test
    public void searchElementNotInWithoutChangingArray() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Integer[] arrCopy = arr.clone();
        Exception exception = assertThrows(NoSuchElementException.class,
            () -> { find(arr, 6); }
        );
        assertEquals("No such element exists in the array", exception.getMessage());        
        assertArrayEquals(arr, arrCopy);
    }
}


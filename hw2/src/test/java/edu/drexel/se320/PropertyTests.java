package edu.drexel.se320;

// Hamcrest
import java.util.Arrays;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;


public class PropertyTests extends BinarySearchBase {

    // Partitions

    // Partition: Invalid inputs
    
    // Null array
    @Property
    public void checkNullArray(@ForAll("generateElem") int x) {
        Integer[] arr = null;
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> find(arr, x)
        );
        assertEquals("Array is null", exception.getMessage());
    }

    // Empty array
    @Property
    public void checkEmptyArray(@ForAll("generateElem") int x) {
        Integer[] arr = new Integer[0];
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> find(arr, x)
        );
        assertEquals("Array is empty", exception.getMessage());
    }
    
    // Null element
    @Property
    public void checkForNullElt(@ForAll("generateArray") Integer[] arr) {
        Integer elem = null;
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> find(arr, elem)
        );
        assertEquals("Element is null", exception.getMessage());
    }

    // Partion: Edge cases and boundaries depending on array size
    // Search for an element in array size one
    @Property
    public boolean checkSingleArrayElt(@ForAll int x) {
	    Integer[] arr = { x };
	    return find(arr, x) == 0;
    }

    // Search for element in odd sized arrays
    @Property
    public boolean checkOddSizedArrayElt(@ForAll("generateArray") Integer[] arr) {
        if (arr.length % 2 == 1) {
            Integer [] oddArr = Arrays.copyOf(arr, arr.length | 1);
            int elem = oddArr[oddArr.length / 2];
            return find(arr, elem) == arr.length / 2;
        }
        return true;
    }

    // Search for an element in an even size array (upper mid index)
    @Property
    public boolean checkEvenSizedArrayRightElt(@ForAll("generateArray") Integer[] arr) {
        if (arr.length % 2 == 0) {
            int elem = arr[arr.length / 2] + 1;
            return find(arr, elem) == arr.length / 2 + 1;
        }
        return true;
    }

    // Search for an element in an even size array (lower mid index)
    @Property
    public boolean checkEvenSizedArrayLeftElt(@ForAll("generateArray") Integer[] arr) {
        if (arr.length % 2 == 0) {
            int elem = arr[arr.length / 2] - 1;
            return find(arr, elem) == arr.length / 2 - 1;
        }
        return true;
    }

    // Partition: Edge cases and boundaries depending on element position
    // Search for first element
    @Property
    public boolean checkFirstElt(@ForAll("generateArray") Integer[] arr) {
        int elem = arr[0];
        return find(arr, elem) == 0;
    }

    // Search for last element
    @Property
    public boolean checkLastElt(@ForAll("generateArray") Integer[] arr) {
        int elem = arr[arr.length - 1];
        return find(arr, elem) == arr.length - 1;
    }
    
    // Search for middle element
    @Property
    public boolean checkMiddleElt(@ForAll("generateArray") Integer[] arr) {
        int elem = arr[arr.length / 2];
        return find(arr, elem) == arr.length / 2;
    }

    // Partition: Edge cases and boundaries for elements not in the array
    @Property
    public boolean checkEltNotInArray(@ForAll("generateArray") Integer[] arr) {
        int elem = 101;
        Exception exception = assertThrows(
            IllegalArgumentException.class,
            () -> find(arr, elem)
        );
        assertEquals("Array is null", exception.getMessage());
    }


    @Provide
    Arbitrary<Integer> generateElem() {
        return Arbitraries.integers().between(0, 100);
    }

    @Provide
    Arbitrary<Integer[]> generateArray() {
        return Arbitraries.integers()
            .between(0, 100)
            .array(Integer[].class)
            .ofSize(30)
            .map( arr -> {
                Arrays.sort(arr);
                return arr;
            });
    }

    record ArrayAndIndex(Integer[] arr, int index) {}

    @Provide
    Arbitrary<ArrayAndIndex> generateArrayAndIndex() {
        return generateArray().flatMap(arr ->
            Arbitraries.integers()
                .between(0, arr.length - 1)
                .map(index -> new ArrayAndIndex(arr, index))
        );
    }

}



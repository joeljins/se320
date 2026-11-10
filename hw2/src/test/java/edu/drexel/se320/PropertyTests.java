package edu.drexel.se320;

// Hamcrest
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

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
        if (arr.length % 2 == 0) {
            arr = Arrays.copyOf(arr, arr.length - 1);
        }
        int elem = arr[arr.length / 2];
        return find(arr, elem) == arr.length / 2;
    }

    // Partition: Edge cases and boundaries depending on element position
    // Search for first element
    @Property
    public boolean checkFirstElt(@ForAll("generateArray") Integer[] arr) {
        int elem = arr[0];
        return arr[find(arr, arr[0])].equals(arr[0]);
    }

    // Search for last element
    @Property
    public boolean checkLastElt(@ForAll("generateArray") Integer[] arr) {
        int elem = arr[arr.length - 1];
        return arr[find(arr, elem)].equals(elem);
    }
    
    // Search for middle element
    @Property
    public boolean checkMiddleElt(@ForAll("generateArray") Integer[] arr) {
        int mid = (arr.length - 1) / 2;
        return find(arr, arr[mid]) == mid;
    }

    // Partition: Edge cases and boundaries for elements not in the array

    // Search for an element greater than the maximum in the array
    @Property
    public void checkGreaterEltNotInArray(@ForAll("generateArray") Integer[] arr) {
        int elem = 101;
        Exception exception = assertThrows(
            NoSuchElementException.class,
            () -> find(arr, elem)
        );
        assertEquals("No such element exists in the array", exception.getMessage());
    }

    // Search for an element less than the minimum in the array
    @Property
    public void checkLesserEltNotInArray(@ForAll("generateArray") Integer[] arr) {
        int elem = -1;
        Exception exception = assertThrows(
            NoSuchElementException.class,
            () -> find(arr, elem)
        );
        assertEquals("No such element exists in the array", exception.getMessage());
    }

    // Partition: Ensure that the array is not modified by the search

    // Searches for an element in an array without changing the array 
    @Property
    public void checkWithoutChangingArray(@ForAll("generateArrayAndIndex") ArrayAndIndex arrayAndIndex) {
        Integer[] arr = arrayAndIndex.arr();
        int index = arrayAndIndex.index();
        int len1 = arr.length;
        find(arr, arr[index]);
        int len2 = arr.length;
        assertEquals(len1, len2);
    }
    
    // Searches for an element not in an array without changing the array
    @Property
    public void checkEltNotInWithoutChangingArray(@ForAll("generateArray") Integer[] arr) {
        int elem = 101;
        int len1 = arr.length;
        Exception exception = assertThrows(
            NoSuchElementException.class,
            () -> find(arr, elem)
        );
        int len2 = arr.length;
        assertEquals(len1, len2);
    }

    // Partition: Edge cases and boundaries depending on duplicate elements in the array

    // Search for an element in an array with multiple of that element
    @Property
    public boolean searchEltInArrayWithEltDuplicates(@ForAll("generateArrayWithDuplicatesAndIndex") ArrayAndIndex arrayAndIndex) {
        Integer[] arr = arrayAndIndex.arr();
        int elem = arr[arrayAndIndex.index()];
        return arr[find(arr, elem)].equals(elem);
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
    Arbitrary<ArrayAndIndex> generateArrayWithDuplicatesAndIndex() {
        return Arbitraries.integers()
            .between(0, 5)
            .array(Integer[].class)
            .ofSize(30)
            .map(arr -> { Arrays.sort(arr); return arr; })
            .flatMap(arr -> Arbitraries.integers()
                .between(0, arr.length - 1)
                .map(index -> new ArrayAndIndex(arr, index)));
    }

    @Provide
    Arbitrary<ArrayAndIndex> generateArrayAndIndex() {
        return generateArray().flatMap(arr ->
            Arbitraries.integers()
                .between(0, arr.length - 1)
                .map(index -> new ArrayAndIndex(arr, index))
        );
    }

}



package edu.drexel.se320;

import java.util.NoSuchElementException;

public class BinarySearch {

    // DO NOT MODIFY THIS SIGNATURE
    // This includes the protected modifier; the autograder currently relies
    // on a combination of overloading and visibility hacks to swap out your
    // code at runtime to test your test suite.
    protected static <T extends Comparable<T>> int binarySearchImplementation(T[] array, T elem) {
        // If the array is unsorted, the function will return NoSuchElementException or the index by chance, if the midpoint happens to be the element being searched for
        if (elem == null) 
            throw new IllegalArgumentException("Element is null");
        if (array == null) 
            throw new IllegalArgumentException("Array is null");
        if (array.length == 0)
            throw new IllegalArgumentException("Array is empty");
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid].equals(elem)) {
                return mid;
            } else if (array[mid].compareTo(elem) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        throw new NoSuchElementException("No such element exists in the array");
    }
}

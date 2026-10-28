package edu.drexel.se320;

public class BinarySearchBase extends BinarySearch {

    // DO NOT MODIFY THIS FILE
    // The autograder uses this as an intercept point, grading will fail if you change this.
    public <T extends Comparable<T>> int find(T[] array, T elem) {
        return BinarySearch.binarySearchImplementation(array, elem);
    }

}

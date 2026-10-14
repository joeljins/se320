package edu.drexel.se320;

public class BinarySearch {

    // DO NOT MODIFY THIS SIGNATURE
    // This includes the protected modifier; the autograder currently relies
    // on a combination of overloading and visibility hacks to swap out your
    // code at runtime to test your test suite.
    protected static <T extends Comparable<T>> int binarySearchImplementation(T[] array, T elem) {
        // TODO: Implement binary search here. The signature above sets you up to ensure the array elements have a compareTo method
        // The code below here is just so the project will compile out of the box; go ahead and delete these.
        if (elem == null) 
            return -1;
        return elem.compareTo(elem);
    }
}

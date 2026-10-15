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

import java.util.NoSuchElementException;

public class OpenBoxTests extends BinarySearchBase {
    // Triggers null array exception
    @Test
    public void TriggerNullArrayException() {
        Integer[] arr = null;
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, 1); }
        );
        assertEquals("Array is null", exception.getMessage());
    }

    // Triggerrs empty array exception
    @Test
    public void TriggerEmptyArrayException() {
        Integer[] arr = {};
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, 1); }
        );
        assertEquals("Array is empty", exception.getMessage());
    }

    // Triggers null element exception
    @Test
    public void TriggerNullElementException() {
        Integer[] arr = { 1 };
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> { find(arr, null); }
        );
        assertEquals("Element is null", exception.getMessage());
    }

    // Searches left half
    @Test
    public void TriggerSearchLeftHalf() {
        Integer[] arr = { 1, 2, 3, 4, 5};
        assertEquals(find(arr, 1), 0);
    }

    // Searches right half and no such element exception
    @Test
    public void TriggerSearchRightHalfAndNoSuchElementException() {
        Integer[] arr = {1, 2, 3, 4, 5};
        Exception exception = assertThrows(NoSuchElementException.class,
            () -> { find(arr, 6); }
        );
        assertEquals("No such element exists in the array", exception.getMessage());
    }


}


package edu.drexel.se320;

// Hamcrest
import java.util.Arrays;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class PropertyTests extends BinarySearchBase {

    @Property
    public boolean checkSingleArrayElt(@ForAll int x) {
	    Integer[] arr = { x };
	    return find(arr, x) == 0;
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

}


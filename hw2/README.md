
# Homework 2: Property-Based Testing

This assignment explores property-based testing, applied to the binary
search routine from homework 1.  You'll be using [jqwik](https://jqwik.net/docs/current/user-guide.html), which is the most up-to-date library for doing this in Java.

This homework requires you to *add a new test suite to your Homework 1*.  
The template has a file ```PropertyTests.java``` in the same directory as the test files from Homework 1 (under ```src/test/java/edu/drexel/se320/```). All code for this assignment (generator(s) and properties) should go in this additional file. You'll need to copy over your binary search implementation from Homework 1 into the corresponding location here (```src/main/java/edu/drexel/se320/```).
You may find it useful to start with the imports from the [course example of PBT](https://github.com/Drexel-se320/examples/blob/main/src/test/java/edu/drexel/se320/PropertyTesting.java).

Note that as before, CI templates are included for both Github Actions and Gitlab (public or gitlab.cci.drexel.edu),
and you're encouraged to take advantage of that.


## Part 1: Generator for Sorted Arrays

Your binary search only works properly on sorted arrays.  So most (not all) of the properties you'll specify
for your search should be stated for sorted arrays.  As discussed in lecture, the odds of generating
an array of some random length that happens to be sorted, without some additional work, are
astronomically low.  So you need to write a generator that produces *only* sorted arrays.

Write a generator which produces only sorted arrays.  You may choose a
modest upper bound on length (e.g., arrays of some random length up to size 30).  You may also
generate arrays of integers to keep things a bit simpler, and you may restrict the array elements to
a certain reasonable range (e.g., 0-100).  (In practice you wouldn't make these assumptions, but
for a first time doing property based testing this is fine.)

For easy reference, here's a link to the relevant part of the [jqwik documentation](https://jqwik.net/docs/current/user-guide.html#customized-parameter-generation). Note that you can either generate the array structure yourself, or generate a random list (e.g., Arbitrary.list) and convert it to an array.  Recall that given an Arbitrary producing instances of some type T, you can use the [map](https://jqwik.net/docs/1.10.2/javadoc/net/jqwik/api/Arbitrary.html#map-java.util.function.Function-) method with an argument transforming instances of T into instances of another type U to get an Arbitrary that produces instances of U.

You might find [Arrays.sort](https://docs.oracle.com/en/java/javase/27/docs/api/java.base/java/util/Arrays.html#sort(T%5B%5D,java.util.Comparator)) handy for your generator.

Do *not* simply sort an array in individual test cases, but write a generator that specifically produces sorted arrays.  You're not testing the sorting code, so it shouldn't be in your property-based test --- you should instead *generate* the sorted arrays as sorted, because you're writing properties true *for sorted arrays*.  If you do this in each test case you will lose points; the point is for the generator to do the sorting.


## Part 2: Properties for Binary Search

Write property-based tests for your binary
search.  Some of your tests might use a default generator for arrays (to check
behavior on unsorted arrays), while most should probably use the custom generator above.

Write *at least 10* meaningfully different properties, covering sensible general properties of the input. A good starting point is to write a property-based test for each equivalence partition of the valid input, which you likely already worked out in the first homework. If one of your equivalence partitions ends up having only one possible input, then it's fine to use [@Example](https://jqwik.net/docs/current/user-guide.html#creating-an-example-based-test), but almost all of your tests should be general properties, not examples.

Advice: To test a variety of successful cases, you'll need to mostly search for elements that are in
different locations in the array. There are a variety of ways to approach this, and which is more
effective will depend on your array generator and the particular framework you're using.  You should
consider either writing another generator that produces pairs of a sorted array and some element in
that array, or using [Assume.that](https://jqwik.net/docs/current/javadoc/net/jqwik/api/Assume.html#that-boolean-) with the regular integer generator to discard cases where the random element to find is not actually present in the array.  (Grade-wise, there's no preference; choose what works for you, but be mindful of using Assume.that in a way
that is too hard to satisfy.)

If you find bugs in your implementation of binary search, fix them.



## Part 3: Reflection

Please respond to the following questions.  There are no right or wrong answers, but I want to see serious consideration, and thoughtful justification for your responses --- you should explain *why* for each of these questions, addressing *both* the positives of your preference, and the negatives of the alternative.  The goal is for you to form, articulate, and defend a technical choice, rather than conform to whatever your instructor's preferences may or may not be.

1. Comparing property-based testing to closed-box testing, which seems *easier to do* for you at this time? (Many of you have prior experience that might influence your answer; that's okay)
2. Given a set of tests for some unfamiliar software component, which style of tests (concrete closed-box tests with specific inputs, or property-based tests) do you think would be most useful for you to understand the expected behavior of the software? (i.e., which would help you learn the specification from the tests, assuming you only had the tests, and most likely no comments)
3. If the specification for search changed in the future, which style of test suite (closed-box or property-based) would most likely be easier/simpler to update for the new specification?  This is a speculative question, and not specific to a particular change; example changes to consider include changing it to assume the array is sorted in *decreasing* order rather than increasing order, or to take a *set* of elements to search for and return true if *all* of them are present, though you shouldn't assume it's necessarily one of those particular changes.

## Grading

Please submit the whole project as a zip file, including the reflection.  If you like, the reflection can be written inside a block comment in your property-based test file.  Otherwise, if you use a PDF, TXT, or MD file, please make sure you remember to submit the reflection! (It can just be in the root directory of the project.)

Rubric:

- 20% generator
  + You will receive no credit for the generator if you simply sort arbitrary arrays in individual tests.
- 50% tests
- 30% reflection (10% each)

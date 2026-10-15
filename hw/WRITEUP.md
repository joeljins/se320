1. For the closed box tests, I used equivalence partitions and boundary value analysis. I first broke the input space into equivalence partitions, then picked boundary values within each partition, and then wrote the tests.

**Equivalence partitions:**
- Invalid inputs: null array, empty array, null element
- Valid inputs: everything else, which I partitioned further by:
  - Array size: size one, odd size greater than one, even size greater than one
  - Position of the element: first, last, middle
  - Whether the element is present: present, not present and less than the min, not present and greater than the max, not present but within the range of the array
  - Duplicates: duplicates of the element being searched for, duplicates of other elements
  - Side effects: array unchanged after a successful search and after an unsuccessful search

**Boundary value analysis:**
- Searching for an element in an array of size one
- Searching for an element in an array of size greater than one
  - If the array size is odd or even (to find bugs with splitting the search range in half). For even sizes I tested both the lower middle and upper middle elements.
- Searching for the first and last elements (to find off-by-one bugs in the search bounds)
- Searching for a non-existing element lesser than the min and greater than the max
- Searching for a non-existing element between two existing elements
- Searching for an element exactly in the middle
- Making sure the search doesn't modify the array
- Searching for an element in an array that has duplicates of that element

**Resulting tests:**
I wrote a comment above each test but just including this for more info. 

| Partition / boundary | Test |
|---|---|
| Null array | nullArray |
| Empty array | emptyArray |
| Null element | nullElement |
| Size one | searchElementArraySizeOne |
| Odd size | searchElementArrayOdd |
| Even size (upper middle) | searchElementArrayEven |
| Even size (lower middle) | searchElementArrayEvenLowerMid |
| First element | searchFirstElementInArray |
| Last element | searchLastElement |
| Middle element | searchMiddleElement |
| Not present, between range | searchElementNotInArrayButBetweenRange |
| Not present, less than min | searchElementNotInArrayAndSmallerThanMin |
| Not present, greater than max | searchElementNotInArrayAndGreaterThanMax |
| Duplicates of the element | searchElementInArrayWithElementDuplicates |
| Duplicates of other elements | searchElementInArrayWithOtherDuplicates |
| Array unchanged, successful search | searchElementWithoutChangingArray |
| Array unchanged, unsuccessful search | searchElementNotInWithoutChangingArray |


**Exception messages:** I check the exception type, and I also check the message text, following the example in the template tests.

For open box tests, I used statement and branch coverage. I read my implementation, listed each branch, and wrote tests so that every branch is executed. For example, I knew that searching for an element greater than the middle element would trigger the code that moves the search range to the right of the middle. The same logic applies to the left. I knew that using a null array, empty array, null element would trigger the informative errors. I wrote the minimum amount of tests to reach 100% code coverage. The test names should be self-explanatory.


2. The underspecified part of the requirements is searching an unsorted array. This could occur when I'm given requirements but run into implementation details (such as considering unsorted arrays) as I code. I don't know whether an unsorted array will be allowed to be given as input. On an unsorted array, my search may throw NoSuchElementException even though the element is present, or it may find the element by chance. I documented this in a comment on the function. The tests related to this choice would be tests that search unsorted arrays, and those are open box tests. Closed box tests are written from the requirements only, and the requirements don't say what should happen for unsorted input, so a closed box tester has no expected result to check. The expected result only comes from knowing how my implementation works. For example, I could pass in an unsorted array with the element to search for right at the middle to show the chance occurence. I could also pass in an unsorted array that would trigger the exception. These would be open box tests since I know what the output will be because I know the details of the implementation. 


3. Neither approach found a bug in my implementation, but I can imagine scenarios where each could have found bugs the other would miss Closed box tests are better at finding bugs from missing or wrong behavior, since they come from the requirements and not from the code. For example, if I had computed the midpoint incorrectly with floor division, my open box tests could still have hit 100% branch coverage while missing it, because coverage only checks that each line runs, not that it runs at every array size and position. My closed box tests for odd and even sizes, and for the first, last, and middle elements, are what would catch that. Closed box tests would also catch a missing requirement, like if I forgot to throw IllegalArgumentException for an empty array. Open box tests built from the code could never reveal a feature that isn't in the code. Open box tests are better at finding bugs tied to how the code is structured, which the requirements don't point to. For example, if the code had a special case for a certain array size, or an extra branch that only runs in a rare situation, a closed box tester reading only the requirements wouldn't know to test for it. They are also the only ones that can check behavior the requirements leave undefined, like my choice for unsorted arrays.
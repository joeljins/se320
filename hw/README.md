
# SE320 Homework 1: Closed-box / Open-box Testing
## Fall 2026

- Late Policy: standard for the course, see the syllabus

### Overview
In this assignment you'll be writing a bit of code, and testing it using whichever techniques we've discussed that seem appropriate.

### Code To Test
For this assignment, please implement a single method that performs a binary search on a
sorted array.  We will work under the assumption that the array represents some kind of
multiset/bag structure, and that searches are only expected for elements that should be in the
array.

- Valid inputs to the search operation are non-null, non-empty arrays (i.e., length > 0), and a non-null element to search for.
- Your operation should never modify the array
- If your search operation returns a number that is a valid index into the array, the array element at that index should be ```.equals()``` to the sought-after element.
- Your search operation should always either return a valid index into the array, or throw an exception.
- If the input array is sorted and the element is present, the search operation must find the element and return its index in the array.
    + Arrays with duplicate entries are considered valid. If the element being searched for has duplicates, you may return the index of any copy of that element.
- If the search does not find the element in the array, it should raise an informative
  exception with an informative error message, specifically a [```NoSuchElementException```](https://docs.oracle.com/en/java/javase/27/docs/api/java.base/java/util/NoSuchElementException.html).
- Your search operation should tolerate invalid input --- i.e., it should produce an 
  [```IllegalArgumentException```](https://docs.oracle.com/en/java/javase/27/docs/api/java.base/java/lang/IllegalArgumentException.html)
  with an informative error message rather than crashing.
- Notice search functionality is under-specified for unsorted arrays: the requirements above do not obligate any particular finding or non-finding of elements in unsorted arrays --- you choose how your code should behave in this case, and document it.
    + Your search operation should *not* check that the array is sorted. Why? This is a trade-off between correctness and performance; checking sortedness is linear in the size of the array, while binary search is logarithmic on sorted arrays; checking sortedness would defeat the point of doing binary search --- sub-linear performance.

For your convenience, you may consider the ```Find``` specification from the
lecture slides, but note that your code is expected to work for more than just integers, and your tests should be specific to the knowledge that this is a *binary* search, not a linear search.

The ```edu.drexel.se320.BinarySearch``` class in the template code (see below)
has the signature we expect you to implement; do not modify the signature.  If you are
inclined to change the signature of that method, you are probably on the wrong
track.  Note that this requires you to use Java generics with bounds to ensure
the implementation (should) work with any array that can have elements
compared.  For testing, you don't need to test for every conceivable type that
could be put in the array (this is impossible to do anyways).  You can stick to
one choice of array element type for all of your tests (e.g., Integer or
String).  Make use of the [```Comparable``` interface](https://docs.oracle.com/en/java/javase/27/docs/api/java.base/java/lang/Comparable.html). 


### Closed-Box Testing
Test your code!  Please write appropriate automated tests for your search functionality, by
applying closed-box techniques discussed in class.  (Note you'll be
expected to explain the approach you took; see below).

Please put them in the ```src/test/java/edu/drexel/se320/ClosedBoxTests.java``` which already exists in the skeleton code.

Note that the test cases discussed in class are *far* from adequate.

Tip: While discouraged by some groups' coding standards (for good reason), Java's ```import
static``` support can often make test suites a bit less verbose.  ```import static``` imports a
static method of some class such that it can be used directly as a local method, without having to
repeat the class name.  For example, you'll see that many test suites contain

```import static org.junit.jupiter.api.Assertions.assertEquals```

and similar for other static methods of ```org.junit.jupiter.api.Assertions```.  You're welcome to do this with
your search method as well.

The template code contains several trivial tests already as a reminder for how to write certain tests. 
Remember that the other class examples are on Github: https://github.com/Drexel-se320/examples

Before submitting, please ensure you can run this command to execute (only) your closed-box tests.
```
./gradlew test --tests ClosedBoxTests
```

This will generate a report in ```build/reports/tests/test/index.html``` describing passing tests
and, for any failing tests, reporting what exactly failed. If you run your project via an IDE that
understands Gradle projects, it should give you the same information, but both the autograder
and human graders will run your tests via Gradle.

### Open-Box Testing

Now let's switch gears and write some tests using code coverage.

Please put them in the ```src/test/java/edu/drexel/se320/OpenBoxTests.java``` which already exists in the skeleton code.

Start with a single test of your choosing, write just the tests required to achieve 100% branch coverage (so you should also have 100% statement coverage as a result).

Make sure you can gather code coverage with the command:

```
./gradlew test --tests OpenBoxTests jacocoTestReport
```

In addition to generating a test pass/fail report as above, this will generate an HTML
coverage report in ```build/reports/coverage/index.html```.

If a test run fails, you'll want to run ```./gradlew clean``` / clean the project before re-attempting.

Note that while you are welcome to use your IDE to check code coverage as you write tests, double-check before submission that your IDE agrees with the Gradle tooling! If your IDE uses JaCoCo internally, then it almost certainly
will. But some IDEs use a different Java code coverage utility internally, and might report slightly different
results.


### Grading

- 10% Search Routine
    + If your code compiles, genuinely attempts to implement binary search, and passes all of your
      tests, you get full here.
    + If your code fails to compile, fails some of your tests, or if you implement something other than binary search (i.e.,
      something less error-prone), you'll get 0/20.
- 40% Closed-box Test suite
- 20% Open-box Coverage-based Test Suite
- 30% Testing Approach Writeup
    + See below for description of what is expected
    + Please write this up in a file saved in the root of your project directory (the same directory as the ```build.gradle``` file). Please submit a PDF, txt, or Word document.  Make sure you ```git add``` this file every time you modify it!

Note that the 10% credit for your code is all-or-nothing, and doesn't depend on the code being bug-free!
Though if you get full credit for the testing portion and your tests all pass, then you've probably
caught most of your bugs.

Note: Yes, Java's standard library includes binary search implementations on arrays.
No, you may *not* simply call that --- you should implement the algorithm yourself; if you just call Java's, you'll get a 0 for the assignment.

### Submitting

Please put your write-up (details below) in an appropriate file *inside your project directory*.

Submissions proceed via Gradescope, which permits two paths to submission:

- You may submit a zip file directly. For your convenience, you can run ```./gradlew zipSource``` from the project directory, and it will zip everything except build outputs
into ```build/se320-homework1.zip```. Then you can upload that file. If you have saved your written responses in the project directory, this should automatically catch that.
- There is an option to link a Github or Bitbucket git repository to the assignment, and have Gradescope pull from there. If you do this, make sure you have committed and pushed your write-up to your remote repository!

Please do not modify the ```build.gradle``` file. If you need to modify the file to run the tests, then your machine is set up incorrectly; we can help with that.
If you submit the assignment with a modified ```build.gradle``` the autograder will reject it.

#### Write-up
For your write-up, you must:

1. Explain the approach you took to testing your search routine.
   Tell us if you used equivalence partitions, boundary value analysis, etc.,
   including what order you did these things and what process (if any) you used for each.  We are not
   looking for a particular combination of techniques; we're interested in your record of how you selected and
   applied the chosen techniques.  Please include in your write-up any intermediate results (e.g, your textual breakdown of equivalence partitions, etc.); any closed-box
   technique you apply generates intermediate results that are not literally represented as test
   methods written in Java.  If you're unsure what information to include here, you could of course err
   on the side of more information, but really you should just ask.
2. One part of the requirements above was (identified as) underspecified, yet you still had to make a decision regarding the behavior for those under-specified inputs. Which tests are related to that choice of behavior? Are those closed-box, or open-box tests? Why?
3. Briefly compare your closed-box and open-box tests. Did either approach find bugs in your implementation that the other did not (or would not have)? If not, can you imagine scenarios where it could have? (Hint: closed-box and code-coverage-driven open-box tests are complementary, each is better at finding certain kinds of bugs the other is not as good at finding; see the lecture slides.)

Please submit your write-up as a .txt, .md, or .pdf file.

### Scaffolding
To (possibly) simplify things for you, we're providing scaffolding for you to
use the gradle Java build tool for your project.  Gradle is similar to ```ant``` (which is like ```make``` for Java), but
requires a bit less work to get going.  We're providing a template project that
automatically downloads JUnit for you, and manages classpaths correctly when
running the test target.  For many projects, simply adding proper dependency
descriptions to the file ```build.gradle``` and placing a copy of your original
project's source code in ```src/main/java/```` will be enough for gradle to compile
the project (we'll discuss this in detail later this term).
  Extending or replacing the sample test files under src/test/java/
should make it easy to get started writing unit tests.

Gradle has the additional advantage of auto-generating scripts that relieve you of globally installing the correct gradle version on your computer.  The skeleton code contains scripts in the same directory as the main build file (```build.gradle```) that will download and run the appropriate version of gradle for you, from within your project directory.  These are also the scripts that many IDEs assume are present when importing Gradle projects, so don't delete them.

To run your tests from the command line / shell / command prompt / Powershell, change directory to the folder containing ```build.gradle``` and the ```gradlew``` scripts, and type

    ./gradlew test

on Mac OS or Linux systems (including [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install)), or

    .\gradlew.bat test

on Windows.

To make grading easier *we will be running your code via gradle*.  This means your code and test
suite need to work when we run your tests as just described.
This means your program source code must be in files under ```src/main/java/```, and your test code
must be somewhere under ```src/test/java/```. 
For this assignment we want you to only put code/tests in the specific files mentioned above, so we can auto-grade much (not all) of the coding portions of your assignment.

That said, you don't need to use ```gradle``` yourself as you work through the project.  Eclipse,
NetBeans, IntelliJ, and other Java IDEs should be able to work with the directory structure just fine, and
are capable of locating and running JUnit tests, though the details vary across IDEs.  You're
welcome to do development that way, and then make sure it still works via ```gradle``` shortly
before submitting.  Most Java IDEs have support for importing a gradle project and doing all building and testing via gradle.  In the case of Eclipse, you'll need to install the [Eclipse Buildship](https://projects.eclipse.org/projects/tools.buildship) plugin for this. If you do use an IDE, please import the project as a *gradle project* rather than a regular *Java project*, because only the former will ensure your IDE builds the code the same way we do.


### Source Control
At this point, you should all know what version control / source control is, and be comfortable
with some form of it.  "My machine crashed and my code is gone" is not an acceptable reason for an extension in this course; you should be keeping all of your code under version control with a version stored somewhere other than your personal computer, from the moment you start work on an assignment.

The template code includes ready-to-go CI configurations for both Github and Gitlab, which should work with both the main Gitlab service as well as CCI's own: https://support.cci.drexel.edu/getting-connected/gitlabgithub/gitlab-basics/

If you need a git crash course or refresher, we recommend the early chapters of [Pro Git](https://git-scm.com/book/en/v2).


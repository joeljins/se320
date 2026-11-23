1. An initial warm-up run is used to factor out the startup costs of the first run,
   such as warming up the disk, CPU caches, and OS. For Java specifically, we can factor
   out class loading and the JVM interpreting code before the JIT compiler has optimized
   it. With an initial run that isn't counted, we get rid of a factor that would otherwise
   inflate Benchmark run 0 and skew the average. We measure multiple runs (10) to give us
   room to average and compute statistics. Timing each run separately lets us report the
   minimum, maximum, and average, which shows how consistent the results are and gives a
   more reliable estimate than one measurement. It is also important that each run be long
   enough to expose real costs, which is done with the 1,000,000 operations per run.
2. Operational Profile (insert/delete/lookup)
- (a) A logging data structure, which mostly records new data, with occasional queries and edits (deletions)  
    - 70/15/15
- (b) A read-heavy database, with occasional updates and deletions
    - 15/15/70
- (c) A read-only database, with no modifications performed
    - 0/0/100
3. 
| Profile (insert/delete/lookup) | Warmup time (ns) | Min iteration (ns) | Max iteration (ns) | Avg iteration (ns) |
|:---|---:|---:|---:|---:|
| (a) 70/15/15 | 96,956,800 | 69,900,000 | 74,973,900 | 71,845,090 |
| (b) 15/15/70 | 64,952,600 | 46,644,900 | 49,170,200 | 47,174,780 |
| (c) 0/0/100 | 47,174,780 | 3,752,700 | 4,240,500 | 3,862,550 |

4. I think the read-only profile will have the highest throughput. This is because searching
   a red-black tree is O(log n), while an insertion costs about twice that: O(log n) to find
   the position, plus O(log n) to rebalance on the way back up (the actual node insertion is
   O(1)). Deletion has the same kind of extra rebalancing cost. So profiles with more
   modifications should do more work per operation and have lower throughput.
   Pictures are attached -> hw4/attachments
   After running the profiler, it seems my hypothesis is correct. The read-only profile had
   the best throughput, and in the profiles that modify the tree, put and delete took a
   larger share of CPU time than get did.
5. Yes, the warm-up times are noticeably different. Doing some research into the JVM,
   we can expect a significant difference. The JVM initially interprets bytecode and only
   compiles a function to optimized machine code after it has been called a certain number
   of times. Once compiled, the code is cached, and subsequent calls can save time by using
   the cached compiled code. The first run also pays one-time costs, such as loading and
   initializing classes.
6. The numbers for profile (c) tell us nothing useful about a real read-only workload
      because of how the benchmark is written. Each call to runOps creates a brand new, empty
      tree. Every lookup  hits an empty
      tree and returns immediately without traversing anything. The time we measured is really
      the cost of the loop, generating random numbers, etc, not the cost of
      searching a BST. A real read-only database would likely be populated with data beforehand, so
      lookups would have to traverse the tree, and the benchmark can't properly measure that.
7. Instrumentation-based profiling slows down measurements because instrumentation is
   added throughout the program, recording the start/end time and caller of every function
   call. If there are x function calls and each one adds t seconds of overhead, the total
   added time is x * t. In our benchmark, put, get, and delete are called millions of times
   and each does very little work, so t is large compared to the real work per call and the
   overhead dominates the run time, giving a slowdown of 10x or more. 
8.
    - logging (70/15/15): $\frac{1}{0.07184509\ \text{s}} \approx 13.9\ \text{requests/second}$
    - read-heavy (15/15/70): $\frac{1}{0.04717478\ \text{s}} \approx 21.2\ \text{requests/second}$
    - read-only (0/0/100): $\frac{1}{0.00386255\ \text{s}} \approx 259\ \text{requests/second}$
9.
    - $5\ \text{requests/minute} = \frac{1}{12}\ \text{requests/second}$
    - $\text{users} = \dfrac{\text{throughput (requests/second)}}{\frac{1}{12}\ \text{requests/second}} = \text{throughput} \times 12$
    - logging (70/15/15): $13.9\ \text{req/s} \times 12 \approx 167\ \text{users}$
    - read-heavy (15/15/70): $21.2\ \text{req/s} \times 12 \approx 254\ \text{users}$
    - read-only (0/0/100): $258.9\ \text{req/s} \times 12 \approx 3107\ \text{users}$
### Thicket Library:
Library for force-directed layout of 2D or 3D graphs. The solver API provides ability to layout graphs step-by-step, 
with direct access to intermediate results, so the library is suitable interaction and dynamic layout 
processes. Thicket is aggressively optimized by Java standards. On my machine, it takes about 10 seconds to layout the "4lt.graph"
graph of 15,606 vertices and 45,878 edges, with good results.

* Features *

- Barnes-Hut optimization. Uses quad/oct trees to compute repulsive forces. Tree construction is dynamically tuned.

- Graph coarsening. Solver iteratively simplifies graph, performs layout, expands, refines, and repeats. Helps avoid local minima.

- Iterative solver that provides direct access to intermediate results. Suitable for animation or interaction layout.

- Modular design, with options to customize _attraction_, _repulsion_, and _update_ phases.

- Low memory footprint. Due to aggressive use of object pooling, most solver steps after initialization will result in zero memory allocations.

Relevant documentation on this implementation and various algorithms used have been included in the "docs/publications" directory. 

Although there is a bunch of code and libraries in src/test, this project is primarily focused
on the underlying layout engine and not on visualization. 

* Non-Features *

- Documentation is not great.

- Library is written in the style of a C library largely for the sake of optimizations. This includes heavy use of all public struct-like objects, and overlapping intrinsic container fields. May cause conniptions for EE Java folk.


### Build:
$ ant


### Runtime:
After build, add all jars in **target** directories to your project.


### Dependencies:
None. Well, there are a bunch in the src/test area. I'll need to update build.xml eventually to run the
test/example code.

---
Author: Philip DeCamp

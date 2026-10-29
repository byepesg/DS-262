# Java Data Structures Visualizer

This submission contains the Java Swing data-structure modules used by the team's larger application.

## Included modules

- Binary search tree and AVL tree
- Dynamic array and hash table
- Graph traversal/path algorithms (Dijkstra and Bellman-Ford)
- Binary heap and heapsort
- Shared animation and documentation/export helpers

Relevant background documents are in `docs/`.

## Contributors

See the [team README](../../README.md). Before requesting review, record each member's GitHub username and contribution there or in this file; the current roster only says "Alejandro Acosta and partner."

## Requirements and compile check

Use JDK 8 or newer. The controls depend on MigLayout 4.0 and Swing Timing Framework 0.55. The course repository ignores JAR files, so obtain these dependencies separately and place them temporarily at `lib/miglayout-4.0.jar` and `lib/TimingFramework-0.55.jar`.

From this directory, compile all submitted sources in PowerShell:

```powershell
$sources = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp ".\lib\miglayout-4.0.jar;.\lib\TimingFramework-0.55.jar" -d .\out $sources
```

The submission contains reusable UI modules, not an application entry point, so it does not have a standalone run command. Integrate the modules into the host application's UI to launch them. No automated test suite is included in this source snapshot.

## Complexity notes

Let `h` be the tree height, `n` the number of stored elements, and `V`/`E` the graph's vertices/edges.

| Structure or algorithm | Time complexity |
|---|---|
| BST search, insert, delete | `O(h)`; worst case `O(n)` |
| AVL search, insert, delete | `O(log n)` |
| Dynamic array access / append | `O(1)` / amortized `O(1)` |
| Dynamic array insert or remove | `O(n)` |
| Hash-table lookup, insert, delete | Expected `O(1)`, worst case `O(n)` |
| Binary-heap peek / insert / remove | `O(1)` / `O(log n)` / `O(log n)` |
| Heapsort | `O(n log n)` |
| Dijkstra with a priority queue | `O((V + E) log V)` |
| Bellman-Ford | `O(VE)` |

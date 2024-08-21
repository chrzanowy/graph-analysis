## The graph analysis service

### Assumptions

Graph edge has only two vertices. Graphs traversal is one directional.
Datasets are stored in the database. I created a local file loader which runs with local profile selected.
Test coverage is set on 80% of the codebase.

Edit 13.04.2024
- fixed loader, map was inappropriate for the task as it doesn't allow duplicates, replaced with list of objects
- algorithm A1 fixed, rewritten using list instead of map
- added more test cases for algorithms
- fixed dataset analysis when one of the algorithms is not enabled
- added cache for execution of the same algorithm on the same dataset, versioned by dataset modification date
- new metrics service and controller added
- execution time moved out of the algorithm scope to the service
- added datasets visualization to the resources that helps with organoleptic analysis of the datasets
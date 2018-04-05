# SPARQL-LD-Query-Optimizer

This extension propose a set of query 
reordering methods for [SPARQL-LD](https://github.com/fafalios/sparql-ld "SPARQL-LD") that make use of a greedy
algorithm for choosing a near to optimal query execution plan. 

This work focus on heuristics-based methods that reorder the service graph patterns based on selectivity 
estimation on new unbound variables, without requiring the gathering
and use of statistics from the remote sources. Thus decreasing the
number of intermediate results and the number of calls to remote resources.

In a nutshell, this extension provides:
* A set of query reordering methods for SPARQL-LD, which considers the number and type of unbound vari-
ables can highly improve the query execution time
* A greedy algorithm for choosing a near to optimal query execution plan for cases of queries with many service patterns


### Reordering methods provided
This extentiom offer 4 reordering methods: 
1. VC
2. UVC
3. WUVC
4. JWUVC

### Related publications

```
P. Fafalios, T. Yannakis and Y. Tzitzikas,
"Querying the Web of Data with SPARQL-LD", 
20th International Conference on Theory and Practice of Digital Libraries (TPDL'16), 
Hannover, Germany, September 5-9, 2016
``` 
[PDF](http://l3s.de/~fafalios/files/pubs/fafalios_2016_tpdl.pdf) | [BIB](http://l3s.de/~fafalios/files/bibs/fafalios2016sparql-ld.bib)
 
```
P. Fafalios and Y. Tzitzikas,
"SPARQL-LD: A SPARQL Extension for Fetching and Querying Linked Data", 
14th International Semantic Web Conference (Demo Paper) - ISWC'15, 
Bethlehem, Pennsylvania, USA, October 11-15, 2015 
``` 
[PDF](http://users.ics.forth.gr/~fafalios/files/pubs/fafalios_2015_sparql-ld.pdf) | [BIB](http://users.ics.forth.gr/~fafalios/files/bibs/fafalios2015sparql.bib)


## Installation

- Download and install [SPARQL-LD](https://github.com/fafalios/sparql-ld "SPARQL-LD") source code
- Add the new classes
- Build the sources
- Try to run the main class "ics.forth.query_analyzer.main"

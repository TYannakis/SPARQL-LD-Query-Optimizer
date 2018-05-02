# Heuristics-based Query Reordering for SPARQL 1.1 and SPARQL-LD

This library implements a set of heuristics-based query reordering methods for SPARQL 1.1 and [SPARQL-LD](https://github.com/fafalios/sparql-ld "SPARQL-LD") that make use of a greedy algorithm for choosing a near to optimal query execution plan. The methods reorder the SERVICE graph patterns based on selectivity estimation on new unbound variables, without requiring the gathering and use of statistics from the remote sources. This decreases the number of intermediate results and thus the number of calls to remote resources and the overal query execution time. 

In a nutshell, this library provides:
* A set of query reordering methods for SPARQL 1.1 and SPARQL-LD, which considers the number and type of unbound variables and joins 
* A greedy algorithm for choosing a near to optimal query execution plan for cases of queries with many service patterns


### Reordering methods
1. VC (Variable Count)
2. UVC (Unbound Variable Count)
3. WUVC (Weighted Unbound Variable Count)
4. JWUVC (Joins-aware Weighted Unbound Variable Count)

Details and evaluation results are provided in the following paper: 
```
T. Yannakis, P. Fafalios, and Y. Tzitzikas,
"Heuristics-based Query Reordering for Federated Queries in SPARQL 1.1 and SPARQL-LD", 
2nd Workshop on Querying the Web of Data (QuWeDa), in conjunction with the 15th Extended Semantic Web Conference (ESWC'18), 
Heraklion, Greece, June 3-7, 2018
``` 
[PDF](http://l3s.de/~fafalios/files/pubs/fafalios2018_QuWeDa.pdf) | [BIB](http://l3s.de/~fafalios/files/bibs/fafalios2018_QuWeDa.bib) 

### Other related publications

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

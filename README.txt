-------
READ ME
-------
The Additional directory contains slightly different implementations 
of the same RouteRecommender. 

RouteRecommender.java
	The version is based Priority Queue based on Min heap property.

RouteRecommender_extra.java
	The version is based Priority Queue based on list data type.

Note: Both implementations are successful against all junit test case.

FUNCTIONAL FEATURES
-------------------
Bus networks consists of a number of bus routes.
The program takes input of the form that includes 2 portions:
   a bus network portion and a query portion 
The bus network consists of one or more bus routes 
Every bus route is marked with the freqency of the buses and the stops they stop at.
These details are accompanied with the number of miutes the bus takes to reach from the
previous stop.
These inputs are accepted and recommends routes for the user to reach any particular destination queried.

INPUT FORMAT
------------
route 2 15
A3 4
B3 3
C3 5
D3 2
end
route 4 10
B1 3
B2 1
B3 6
B4 3
end
route 3 20
D3 3
D4 1
C4 6
B4 4
A4 3
end
end
A3 A4 1
end

OUTPUT FORMAT
-------------
At stop A3 take bus #2
At stop B3 switch to bus #4
At stop B4 switch to bus #3
Get off at stop A4

NOTE:
-----
1. This Code was done as part of Assignment 6 in CS3 course(MACS, Dalhousie University)
2. RouteRecommenderTest.java, Tester.java - Files rovided by Dr. Alexander Brodskey, Dalhousie University 
   for the purpose of testing.




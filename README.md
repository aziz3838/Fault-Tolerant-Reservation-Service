# README #

This repository is for the practical aspect of the Fault Tolerance graduate course at UBC.

### General Overview ###

This assignments require writing code in Java language. The code is a web application project, based on Java Servlets, and more specifically, http Servlets. Http Servlets are classes that handle requests made to different pages of your web application. We are to improve some aspects of the initial code.

### Team Members ###

* Aziz Alghamdi

### Task #1: Implement Transaction Retries ###

* Retries mechanism is implemented inside the ReserveSeat function in Seat.java. It is implemented at the transaction level. The number of retries is set as 3 (variable retries). A ReserveSeat is considered successful if the transaction commit doesn't raise any exceptions. 
* A back-off timeout is also implemented. The first retry happens after 30ms, and every retry after that happens after previous timeout*2 ms.

### Task #2: Resolve Datastore Contention ###

* Datastore contention was easily resolved by getting rid of the entity group of Flights and Seats. This relationship is an overkill, and it is not necessary. From the Google App Engine (GAE) documentation for how to avoid data contention: "Keep entity groups small. [...] Note that entity groups are not required if you simply plan to reference one entity from another."
* Implementing the documentation suggestion was done by making the following changes to the Seat entity:
    * A seat entity is identified by the key "FlightNumber.SeatID", instead of just SeatID. This provides unique seat keys. This also provides us with the ability to access seat entities directly, if we know the seat and flight numbers (no need to query flight entities). This is described better in the GAE documentation: "If the application knows the complete key for an entity (or can derive it from its parent key, kind, and identifier), it can use the key to operate directly on the entity."
    * A new property is added in Seats: FlightName. Note that the purpose of this property is not to access the Seat entity (we can do this efficiently as described in the previous point). The purpose of this property is to be used when querying for all seat that belong to a certain flight, like in the GetFreeSeats function.
    * GetFreeSeats function queries all Seats using two filters. The first is that it belongs to the flight we're interested in. The second is that the PersonSitting is empty. In Java, this is accomplished using two new Fliters, and then using the CompositeFilterOperator in the Query.

### Load Testing ###

* Jmeter was used for load testing. The initial .jmx configuration file runs 20 threads, eaching with a loop count of 10, that are sending requests to /SeatReservation. I added the extra functionalities for convenience:
    * setUp Thread Group: it sends an HTTP request to /FreeSeats before every test.
    * Response Assertion (probably not needed): to assert the response from the HTTP requests is 200.
* Now the load test just involves Jmeter, and it can be invoked from command line (without GUI) as: 
```
#!shell

./bin/jmeter -n -t A2LoadTest.jmx
```
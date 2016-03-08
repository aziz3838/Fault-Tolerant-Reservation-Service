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

* TBA

### Load Testing ###

* Jmeter...
package com.UBC513.A2.Data;

import java.util.ConcurrentModificationException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;

//Helper class for flight seats.
public class Seat {

	// Create a seat on a specific flight,
	// @store = true, when you want to commit entity to the datastore
	// = false, when you want to commit entity later, like in a batch operation
	public static Entity CreateSeat(String SeatID, String FlightName, boolean store) {
		Entity e = new Entity("Seat", FlightName + "." + SeatID);
		e.setProperty("PersonSitting", null);
		e.setProperty("FlightName", FlightName);

		if (store) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.put(e);
		}

		return e;
	}

	// Frees specific seat(SeatID) on flight(FlightKey)
	public static void FreeSeat(String SeatID, Key FlightKey) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		try {
			Entity e = ds.get(KeyFactory.createKey(FlightKey, "Seat", SeatID));

			e.setProperty("PersonSitting", null);
			ds.put(e);
		} catch (EntityNotFoundException e) {
		}
	}

	//Returns all free seats on a specific flight(FlightKey)
	public static Iterable<Entity> GetFreeSeats(String FlightName) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//		Query q = new Query("Seat").setAncestor(FlightKey).addFilter(
//				"PersonSitting", FilterOperator.EQUAL, null);
		
		// Filters
		Filter flightNameFilter =
		  new Query.FilterPredicate("FlightName",
				                	FilterOperator.EQUAL,
				                	FlightName);
		Filter emptySeatFilter =
		  new Query.FilterPredicate("PersonSitting",
				                	FilterOperator.EQUAL,
				                	null);
		
		//Use CompositeFilter to combine multiple filters
		Filter freeSeatFilter =
		  Query.CompositeFilterOperator.and(flightNameFilter, emptySeatFilter);
		
		Query q = new Query("Seat")
				.setFilter(freeSeatFilter);
		
		return ds.prepare(q).asIterable();
	}

	//Reserves a specific seat(SeatID) on a specific flight(FlightKey)
	public static boolean ReserveSeat(String SeatID,
			String FirstName, String LastName) throws EntityNotFoundException {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Transaction tx = ds.beginTransaction();
		int retries = 3;
		try {
			Entity e = ds.get(tx, KeyFactory.createKey("Seat", SeatID));
			
			if (e.getProperty("PersonSitting") != null)
				return false;

			e.setProperty("PersonSitting", FirstName + " " + LastName);
			
			ds.put(tx, e);
			tx.commit();
		} catch (ConcurrentModificationException e) {
			if (retries == 0) {
	            return false;
	        }
	        // Decrement retry counter
	        --retries;
		}
		return true;
	}
}

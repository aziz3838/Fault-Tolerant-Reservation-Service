package com.UBC513.A3.Data;

import java.util.Date;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


//Flight Helper class, used to create and get flights from Google Datastore
public class SeatReservation {
	// Create a flight
	// @store = true when you want to commit entity to datastore
	// = false when you want to commit entity later, like in a batch operation.
	public static Entity CreateReservation(String FlightName, String SeatID, String FirstName, String LastName, boolean store) {
		Entity e = new Entity("SeatReservation");
		
		e.setProperty("date", new Date() );
		e.setUnindexedProperty("FlightName", FlightName);
		e.setUnindexedProperty("SeatID", SeatID);
		e.setUnindexedProperty("FirstName", FirstName);
		e.setUnindexedProperty("LastName",LastName);
		
		if (store) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
			ds.put(e);
		}

		return e;
	}
	
	public static Entity CreateReservation(String Flight1, String Flight1Seat, String Flight2, String Flight2Seat, String Flight3,
			String Flight3Seat, String Flight4, String Flight4Seat, String FirstName, String LastName, boolean store ) throws Exception
	{
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		String keyString = Flight1+Flight2+Flight3+Flight4+Flight1Seat+Flight2Seat+Flight3Seat+Flight4Seat+FirstName+LastName;
		try {
	        ds.get(KeyFactory.createKey("SeatReservation",keyString));
	        return null;	//TODO
	    } catch (EntityNotFoundException e){
	        // Continue
	    }
		
		Entity e = new Entity("SeatReservation", keyString);
		
		e.setProperty("date", new Date() );
		e.setProperty("Flight1", Flight1);
		e.setProperty("Flight2", Flight2);
		e.setProperty("Flight3", Flight3);
		e.setProperty("Flight4", Flight4);
		
		e.setProperty("Flight1Seat", Flight1Seat);
		e.setProperty("Flight2Seat", Flight2Seat);
		e.setProperty("Flight3Seat", Flight3Seat);
		e.setProperty("Flight4Seat", Flight4Seat);
		
		e.setProperty("FirstName", FirstName);
		e.setProperty("LastName", LastName);
		
		if(store) {
			ds.put(e);
		}
		
		return e;
	}

	// Get all flights in the datastore
	public static Iterable<Entity> GetReservations() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("SeatReservation").addSort( "date", SortDirection.ASCENDING);
		return ds.prepare(q).asIterable();
	}
}
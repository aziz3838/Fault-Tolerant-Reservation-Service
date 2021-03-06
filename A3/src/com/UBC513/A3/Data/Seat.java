package com.UBC513.A3.Data;

import java.util.ConcurrentModificationException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;


//Helper class for flight seats.
public class Seat {
	
	static final int NUM_RETRIES = 10;

	// Create a seat on a specific flight,
	// @store = true, when you want to commit entity to the datastore
	// = false, when you want to commit entity later, like in a batch operation
	public static Entity CreateSeat(String SeatID, String FlightName,
			boolean store) {
		Entity e = new Entity(FlightName, SeatID);
		e.setProperty("PersonSitting", null);

		if (store) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.put(e);
		}

		return e;
	}

	// Frees specific seat(SeatID) on flight(FlightKey)
	public static void FreeSeat(String SeatID, String FlightName) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		try {
			Entity e = ds.get(KeyFactory.createKey(FlightName, SeatID));

			e.setProperty("PersonSitting", null);
			ds.put(e);
		} catch (EntityNotFoundException e) {
		}
	}

	// Returns all free seats on a specific flight(FlightKey)
	public static Iterable<Entity> GetFreeSeats(String FlightName) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(FlightName).addFilter("PersonSitting",
				FilterOperator.EQUAL, null);
		return ds.prepare(q).asIterable();
	}

	// Reserves a specific seat(SeatID) on a specific flight(FlightKey)
	public static boolean ReserveSeat(String FlightName, String SeatID,
			String FirstName, String LastName) throws EntityNotFoundException {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		for (int i = 0; i < NUM_RETRIES; i++) {
			Transaction tx = ds.beginTransaction();
			
			try {
				Entity e = ds.get(tx, KeyFactory.createKey(FlightName, SeatID));

				if (e.getProperty("PersonSitting") != null)
					return false;

				e.setProperty("PersonSitting", FirstName + " " + LastName);
				ds.put(tx, e);
				tx.commit();
				return true;
			} catch (ConcurrentModificationException e) {
				// continue
			} finally {
				if (tx.isActive())
					tx.rollback();
			}
		}
		throw new ConcurrentModificationException();
	}

	public static boolean ReserveSeats(String Flight1, String Flight1Seat,
			String Flight2, String Flight2Seat, String Flight3,
			String Flight3Seat, String Flight4, String Flight4Seat,
			String FirstName, String LastName) throws EntityNotFoundException {
		System.out.println("Received Reserve Seat Request, Flight1: " + Flight1 +
						   ", SeatID1: " + Flight1Seat + ", FirstName: " + FirstName);
		// Use arrays
		String flights[] = new String[] {Flight1, Flight2, Flight3, Flight4};
		String seatIDs[] = new String[] {Flight1Seat, Flight2Seat, Flight3Seat, Flight4Seat};
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		// Use cross-group transaction
		TransactionOptions options = TransactionOptions.Builder.withXG(true);

		final int NUM_FLIGHTS = 4;
		
		for (int i = 0; i < NUM_RETRIES; i++) {
			Transaction tx = ds.beginTransaction(options);
			try {
				
				for(int flightIdx=0; flightIdx<NUM_FLIGHTS; flightIdx++)
				{
					Entity e = ds.get(tx, KeyFactory.createKey(flights[flightIdx], seatIDs[flightIdx]));
									
					if (e.getProperty("PersonSitting") != null)
					{
						if(e.getProperty("PersonSitting").equals(FirstName + " " + LastName))
						{
							//System.out.println("idempotent");
							continue;	// idempotent
						} else {
							return false;
						}
					}
					e.setProperty("PersonSitting", FirstName + " " + LastName);
					ds.put(tx, e);
				}
				
				tx.commit();
				return true;
			} catch (ConcurrentModificationException e) {
				// continue
			} catch (Exception e1) {
				// continue
			} finally {
				if (tx.isActive())
				{
					tx.rollback();
				}
			}
		}
		throw new ConcurrentModificationException();
	}
}

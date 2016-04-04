package com.UBC513.A3.Helpers;

import com.UBC513.A3.Data.Seat;
import com.UBC513.A3.Data.SeatReservation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class Worker {

	public static void Process() throws EntityNotFoundException 
	{
		System.out.println("Called Waitinglist Handler");
		
		// Retry the waitlist transaction!!
		Iterable<Entity> reservations = SeatReservation.GetReservations();
		for(Entity e : reservations )
		{
			// Get the data again so we can make a new trasact.
			String Flight1 = (String) e.getProperty("Flight1");
			String Flight2 = (String) e.getProperty("Flight2");
			String Flight3 = (String) e.getProperty("Flight3");
			String Flight4 = (String) e.getProperty("Flight4");
			String Flight1Seat = (String) e.getProperty("Flight1Seat");
			String Flight2Seat = (String) e.getProperty("Flight2Seat");
			String Flight3Seat = (String) e.getProperty("Flight3Seat");
			String Flight4Seat = (String) e.getProperty("Flight4Seat");
			String FirstName = (String) e.getProperty("FirstName");
			String LastName = (String) e.getProperty("LastName");
			
			// Retry tx
			if (Seat.ReserveSeats(Flight1, Flight1Seat, Flight2, Flight2Seat, Flight3, Flight3Seat, Flight4, Flight4Seat, FirstName, LastName)) 
			{
				// Success!! remove this.
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Key k = e.getKey();
				ds.delete(k);
			}
		}
		
		// Re-schedule this task
		try {
			Thread.sleep(3000);
		} catch (InterruptedException except) {
			// do nothing
		}
		Queue q = QueueFactory.getDefaultQueue();
		q.add(TaskOptions.Builder.withUrl("/worker"));	
	}		
}

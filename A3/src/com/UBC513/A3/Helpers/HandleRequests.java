package com.UBC513.A3.Helpers;

import java.util.ConcurrentModificationException;

import javax.servlet.http.HttpServletRequest;

import com.UBC513.A3.Data.Request;
import com.UBC513.A3.Data.Seat;
import com.UBC513.A3.Data.SeatReservation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class HandleRequests {
	
	public static void Process() throws EntityNotFoundException 
	{
		System.out.println("Called Request Handler");
				
		// Retry the waitlist transaction!!
		Iterable<Entity> requests = Request.GetRequests();
		for(Entity e : requests )
		{
			System.out.println("Processing a Request");
			
			// Get data.
			String flights[] = new String[4];
			String seatIDs[] = new String[4];
			for(int flightIdx=0; flightIdx<4; flightIdx++)
			{
				flights[flightIdx] = (String) e.getProperty("Flight" + (flightIdx + 1));
				seatIDs[flightIdx] = (String) e.getProperty("Flight" + (flightIdx + 1) + "Seat");
			}
			String FirstName = (String) e.getProperty("FirstName");
			String LastName = (String) e.getProperty("LastName");
			Boolean waitingListOk = (Boolean) e.getProperty("waitingListOk");

			// Try to reserve/put in waiting list
			try {
				if (!Seat.ReserveSeats(flights[0], seatIDs[0],
									   flights[1], seatIDs[1],
									   flights[2], seatIDs[2],
								   	   flights[3], seatIDs[3],
								   	   FirstName, LastName)) 
				{
					System.out.println("Could not reserve seat");
					if(waitingListOk)
					{
						SeatReservation.CreateReservation(flights[0], seatIDs[0], flights[1], seatIDs[1],
														  flights[2], seatIDs[2], flights[3], seatIDs[3],
														  FirstName, LastName, true);
						System.out.println("Added to waiting list");				
					}
					else
					{
						System.out.println("Seat Not Reserved, no waiting list request");
					}
				} else {
					System.out.println("Seat Reserve Succeeded");
				}
				
				// Success! Remove this request.
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Key k = e.getKey();
				ds.delete(k);
				
			} catch (EntityNotFoundException exep) {
				System.out.println("Error 1");
			} catch (Exception exep) {
				System.out.println("Error 2");
			}
			
		}
		
		// Re-schedule this task
		try {
			Thread.sleep(1000);
		} catch (InterruptedException except) {
			// do nothing
		}
		Queue q = QueueFactory.getDefaultQueue();
		q.add(TaskOptions.Builder.withUrl("/request"));	
	}	
	
	
}

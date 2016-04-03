package com.UBC513.A3.Helpers;

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
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class HandleRequests {

	public static void Process() throws EntityNotFoundException 
	{
		System.out.println("Called Request Handler");
		//let datastore catch up
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {}
				
		// Retry the waitlist transaction!!
		Iterable<Entity> requests = Request.GetRequests();
		for(Entity e : requests )
		{
			System.out.println("Processing a Request");
			// Get the data again so we can make a new trasact.
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
						Queue q = QueueFactory.getDefaultQueue();
						q.add(TaskOptions.Builder.withUrl("/worker"));
						System.out.println("Added to waiting list");
						//forwardTo = "/reserveSeatWaiting.jsp";					
					}
					else
					{
						System.out.println("Seat Not Reserved, no waiting list request");
						// seat not reserved, show error page
						//forwardTo = "/reserveSeatError.jsp";
					}
				} else {
					System.out.println("Seat Reserve Succeeded");
				}
				
				// Succes!! remove this request.
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Key k = e.getKey();
				ds.delete(k);
				
			} catch (EntityNotFoundException exep) {
				// seat not found, show error page
				//forwardTo = "/reserveSeatError.jsp";
				System.out.println("Error 1");
			} catch (Exception exep) {
				// Do Nothing.
				System.out.println("Error 2");
			}
			
			
			
		}
		
		
		// Temporary
		try {
			Thread.sleep(10000);
		} catch (InterruptedException except) {
			// do nothing
		}
		Queue q = QueueFactory.getDefaultQueue();
		q.add(TaskOptions.Builder.withUrl("/request"));	
	}	
	
}

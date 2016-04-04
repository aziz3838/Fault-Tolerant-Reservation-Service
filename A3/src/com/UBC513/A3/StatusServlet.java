package com.UBC513.A3;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.UBC513.A3.Data.Request;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.TransactionOptions;

@SuppressWarnings("serial")
public class StatusServlet extends HttpServlet {
	

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		/*
		// Response
		String flight = req.getParameter("Flight0");
		System.out.println("AJAAAAAX: " + flight);
		//resp.setContentType("text/plain");
		//resp.setCharacterEncoding("UTF-8"); 
		resp.getWriter().write(flight + "hi");
		*/

						
				
		// Get parameters
		String flights[] = new String[4];
		String seatIDs[] = new String[4];
		for(int flightIdx=0; flightIdx<4; flightIdx++)
		{
			flights[flightIdx] = req.getParameter("Flight" + flightIdx);
			seatIDs[flightIdx] = req.getParameter("SeatID" + flightIdx);
		}
		String waitlist = req.getParameter("waitlist");
		boolean waitingListOk = false;
		if(waitlist != null)
			waitingListOk = true;
		String FirstName = req.getParameter("FirstName");
		String LastName = req.getParameter("LastName");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		// Use cross-group transaction
		TransactionOptions options = TransactionOptions.Builder.withXG(true);

		// Response Value
		String respValue = "Order is not processed yet";
		
		// Check Status: Reserved
		int reservedCount = 0;
		for(int flightIdx=0; flightIdx<4; flightIdx++)
		{
			try {
				Entity e = ds.get(KeyFactory.createKey(flights[flightIdx], seatIDs[flightIdx]));
				if(e.getProperty("PersonSitting") != null &&
				   e.getProperty("PersonSitting").equals(FirstName + " " + LastName))
					reservedCount++;
			} catch (EntityNotFoundException e1) {
	
			}	
		}
		if(reservedCount==4)
			respValue = "Reserved";
		System.out.println("DEBUG: ReservedCount is: " + reservedCount);

		// Check Status: in waiting list
		String keyString = flights[0]+flights[1]+flights[2]+flights[3]+
			seatIDs[0]+seatIDs[1]+seatIDs[2]+seatIDs[3]+FirstName+LastName;
		try {
	        ds.get(KeyFactory.createKey("SeatReservation",keyString));
	        respValue = "WaitingList";
	    } catch (EntityNotFoundException e){
	        // Continue
	    }
		
		// Response
		resp.getWriter().write(respValue); 
	}
}

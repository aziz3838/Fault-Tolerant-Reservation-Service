package com.UBC513.A3;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.UBC513.A3.Data.Request;
import com.UBC513.A3.Data.Seat;
import com.UBC513.A3.Data.SeatReservation;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class ReserveSeatServlet extends HttpServlet {
	

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

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
		
		// This needs to changes to a generic "we received your request"
		String forwardTo = "/seatConfirmation.jsp";
		
		// Fault Tolerance: Store Request
		try {
			Request.CreateRequest(flights[0], seatIDs[0], flights[1], seatIDs[1],
								  flights[2], seatIDs[2], flights[3], seatIDs[3],
								  FirstName, LastName, waitingListOk, true);
			System.out.println("Created Request");
			//Queue q = QueueFactory.getDefaultQueue();
			//q.add(TaskOptions.Builder.withUrl("/request"));
			
		} catch (Exception e1) {
			// ?
		}
		
		

		// redirect to final page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(forwardTo);
		rd.forward(req, resp);
	}
}

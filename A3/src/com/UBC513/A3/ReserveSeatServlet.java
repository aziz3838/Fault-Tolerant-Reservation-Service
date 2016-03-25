package com.UBC513.A3;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.UBC513.A3.Data.Seat;
import com.UBC513.A3.Data.SeatReservation;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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
		
		String FirstName = req.getParameter("FirstName");
		String LastName = req.getParameter("LastName");
		
		String forwardTo = "/seatConfirmation.jsp";
		try {
			if (!Seat.ReserveSeats(flights[0], seatIDs[0],
								   flights[1], seatIDs[1],
								   flights[2], seatIDs[2],
							   	   flights[3], seatIDs[3],
							   	   FirstName, LastName)) {
				// seat not reserved, show error page
				forwardTo = "/reserveSeatError.jsp";
			}
		} catch (EntityNotFoundException e) {
			// seat not found, show error page
			forwardTo = "/reserveSeatError.jsp";
		}

		// redirect to final page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(forwardTo);
		rd.forward(req, resp);
	}
}

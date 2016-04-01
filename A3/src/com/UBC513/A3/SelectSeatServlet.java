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
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class SelectSeatServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		final int NUM_FLIGHTS = 4;
		String flights[] = new String[NUM_FLIGHTS];
		ArrayList<Iterable<Entity>> flightsSeats = new ArrayList<Iterable<Entity>>();
		
		for(int i=0; i<NUM_FLIGHTS; i++)
		{
			flights[i] = req.getParameter("Flight" + i);
			flightsSeats.add(Seat.GetFreeSeats(flights[i])); 
		}
		req.setAttribute("flightsSeats", flightsSeats);
		
		//redirect to index.jsp
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher("/selectSeats.jsp");
		rd.forward(req, resp);
	}

}

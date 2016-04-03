package com.UBC513.A3.Data;

import java.util.Date;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


//Request Helper class, used to create and get flights from Google Datastore
public class Request {

	
	public static Entity CreateRequest(String Flight1, String Flight1Seat, String Flight2, String Flight2Seat, String Flight3,
			String Flight3Seat, String Flight4, String Flight4Seat, String FirstName, String LastName, boolean waitingListOk, boolean store ) throws Exception
	{
		Entity e = new Entity("Request");
		
		e.setProperty("date", new Date() );
		e.setProperty("Flight1", Flight1);
		e.setProperty("Flight2", Flight2);
		e.setProperty("Flight3", Flight3);
		e.setProperty("Flight4", Flight4);
		
		e.setProperty("Flight1Seat", Flight1Seat);
		e.setProperty("Flight2Seat", Flight2Seat);
		e.setProperty("Flight3Seat", Flight3Seat);
		e.setProperty("Flight4Seat", Flight4Seat);
		
		e.setProperty("waitingListOk", waitingListOk);
		
		e.setProperty("FirstName", FirstName);
		e.setProperty("LastName", LastName);
		
		if( store) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.put(e);
		}
		
		return e;
	}

	// Get all requests in the datastore
	public static Iterable<Entity> GetRequests() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Request").addSort( "date", SortDirection.ASCENDING);
		return ds.prepare(q).asIterable();
	}
}
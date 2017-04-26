package it.polito.ai.es03.application;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import it.polito.ai.es03.model.hibernate.*;
import it.polito.ai.es03.model.neighborhoodgraph.*;
import it.polito.ai.es03.model.neighborhoodgraph.Transport.TransportType;

public class App 
{
	private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	private static NeighborhoodGraph neighborhoodGraph = new NeighborhoodGraph();
	private static final int RADIUS = 250;
	
	public static void main( String[] args )
    {
        
    	Session session = sessionFactory.getCurrentSession();
    	Transaction tx=null;
	    try {
	    	System.out.println( "Started graph filling!" );
	    	tx=session.beginTransaction();
	    	//find neighborhood for each stop on the DB
	    	//1 - each stop on a bus line has as neighbor the next stop
    		System.out.println( "Downloading BusLines from DB..." );
    		Query query = session.createQuery("from BusLine");
	    	List<BusLine> busLines = query.list();
		    for(BusLine busLine : busLines){
		    	findNeighborhoodOnBusLineStops(busLine.getLineStops());
		    }
		    //2 - each stop has as neighbor the stops in 250m radius
		    System.out.println( "Downloading BusStops from DB..." );
    		query = session.createQuery("from BusStop");
	    	List<BusStop> busStops = query.list();
		    for(BusStop busStop : busStops){
		    	String stopId = busStop.getId();
		    	findNeighborhoodOnRadius(stopId, RADIUS);
		    }
		    tx.commit();
		    System.out.println( "Completed graph filling!" );
		    
	    } finally {
	    	if (session!=null && session.isOpen()) session.close(); 
	    	session=null;
	    }
    }
    
	private static void findNeighborhoodOnBusLineStops(List<BusLineStop> busLineStops){
		
		//sort stops by sequence number
		Collections.sort(busLineStops);
    	int totStops = busLineStops.size();
    	for(int i=0; i < totStops - 1; i++ ){
    		//each stop has as neighbor the next stop
    		BusStop thisStop = busLineStops.get(i).getBusStop();
    		BusStop nextStop = busLineStops.get(i+1).getBusStop();
    		String thisStopId = thisStop.getId();
    		String nextStopId = nextStop.getId();;
    		//already into the graph
    		if(neighborhoodGraph.hasThisNeighbor(thisStopId, nextStopId)){
    			continue;
    		}
    		//compute distance between this stop and next stop
    		String stringQuery = "select ST_Distance(a.position, b.position) as distance "
    				+ "from busstopgeo a, busstopgeo b "
    				+ "where a.id = '"+thisStopId+"' "
    				+ "and b.id = '"+nextStopId+"';";
    		Session session = sessionFactory.getCurrentSession();
    		List<Object> result = session.createSQLQuery(stringQuery).list();
    		double distance = -1;
    		for (Object object : result) {
				distance = (Double) object;
    		}
    		//insert into the graph
    		Neighbor neighbor = new Neighbor(nextStopId, distance, TransportType.BUS);
    		neighborhoodGraph.addNeighbor(thisStopId, neighbor);
    	}
	}
	
	private static void findNeighborhoodOnRadius(String stopId, int radius){
		
		//compute distance from stops in radius
		String stringQuery = "select b.id as id, ST_Distance(a.position, b.position) as distance "
				+ "from busstopgeo a, busstopgeo b "
				+ "where a.id = '"+stopId+"' "
				+ "and a.id < b.id "
				+ "and ST_DWithin(a.position, b.position, "+radius+");";
		Session session = sessionFactory.getCurrentSession();
		List<Object[]> result = session.createSQLQuery(stringQuery).list();
		for (Object[] object : result) {
			String neighborId = (String) object[0]; 
			double distance = (Double) object[1];
			//ATTENTION -> THIS NEIGHBORHOOD IS BIDIRECTIONAL
			//1 - from stopId to neighborId
			//check if the neighbor is already into the graph
			if(neighborhoodGraph.hasThisNeighbor(stopId, neighborId)){
				continue;
			}
			//insert into the graph
			Neighbor neighbor = new Neighbor(neighborId, distance, TransportType.FOOT);
			neighborhoodGraph.addNeighbor(stopId, neighbor);
			//2 - from neighborId to stopId
			//check if the neighbor is already into the graph
			if(neighborhoodGraph.hasThisNeighbor(neighborId, stopId)){
				continue;
			}
			//insert into the graph
			Neighbor neighbor2 = new Neighbor(stopId, distance, TransportType.FOOT);
			neighborhoodGraph.addNeighbor(neighborId, neighbor2);
    	}
	}
	
}

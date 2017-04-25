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
	
	public static void main( String[] args )
    {
        
    	Session session = sessionFactory.getCurrentSession();
    	Transaction tx=null;
	    try {
			//download the list of all the stops from the DB
    		System.out.println( "Downloading BusLines from DB..." );
    		tx=session.beginTransaction();
    		Query query = session.createQuery("from BusLine");
		    List<BusLine> busLines = query.list();
		    //Fill in the neighborhood graph
		    NeighborhoodGraph neighborhoodGraph = new NeighborhoodGraph();
		    for(BusLine busLine : busLines){
		    	System.out.println("=====================");
		    	System.out.println("BusLine: "+busLine.getLine());
		    	List<BusLineStop> busLineStops = busLine.getLineStops();
		    	//sort by sequence number -> see BusLineStopPK compareTo()
		    	Collections.sort(busLineStops);
		    	int totStops = busLineStops.size();
		    	for(int i=0; i < totStops - 1; i++ ){
		    		//each stop has as neighbor the next stop
		    		BusStop thisStop = busLineStops.get(i).getBusStop();
		    		BusStop nextStop = busLineStops.get(i+1).getBusStop();
		    		String thisStopId = thisStop.getId();
		    		String nextStopId = nextStop.getId();
		    		System.out.println("BusStop: "+thisStopId);
		    		System.out.println("Neighbor: "+nextStopId);
		    		//already into the graph
		    		if(neighborhoodGraph.hasThisNeighbor(thisStopId, nextStopId)){
		    			System.out.println("Already into graph!");
		    			continue;
		    		}
		    		//compute distance between this stop and next stop
		    		String stringQuery = "select ST_Distance(a.position, b.position) as distance "
		    				+ "from busstopgeo a, busstopgeo b "
		    				+ "where a.id = '"+thisStopId+"' "
		    				+ "and b.id = '"+nextStopId+"';";
		    		List<Object> result = session.createSQLQuery(stringQuery).list();
		    		double distance = -1;
		    		for (Object object : result) {
						distance = (Double) object;
		    		}
		    		System.out.println("distance: "+distance+"m");
		    		//insert into the graph
		    		Neighbor neighbor = new Neighbor(nextStopId, distance, TransportType.BUS);
		    		neighborhoodGraph.addNeighbor(thisStopId, neighbor);
		    	}
		    	System.out.println("=====================");
		    }
		    tx.commit();
		    
	    } finally {
	    	if (session!=null && session.isOpen()) session.close(); 
	    	session=null;
	    }
    }
    
}

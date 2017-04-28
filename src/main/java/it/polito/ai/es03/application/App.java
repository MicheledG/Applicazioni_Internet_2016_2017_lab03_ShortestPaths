package it.polito.ai.es03.application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import it.polito.ai.es03.dijkstra.MinPathsCalculator;
import it.polito.ai.es03.dijkstra.model.*;
import it.polito.ai.es03.dijkstra.model.Transport.TransportType;
import it.polito.ai.es03.dijkstra.model.mongo.MinPath;
import it.polito.ai.es03.model.hibernate.*;

public class App 
{
	private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	private static NeighborhoodGraph neighborhoodGraph = new NeighborhoodGraph();
	private static final int RADIUS = 250;
	
	public static void main( String[] args )
    {
        
    	//Create graph to apply Dijkstra
		List<String> busStopIds = new ArrayList<String>();
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
		    	busStopIds.add(stopId);
		    	findNeighborhoodOnRadius(stopId, RADIUS);
		    }
		    tx.commit();
		    System.out.println( "Completed graph filling!" );
		    
	    }catch (Exception e) {
	    	System.out.println( "Not possible to Complete graph filling!" );
			neighborhoodGraph = null;
		} 
	    finally {
	    	if (session!=null && session.isOpen()) session.close(); 
	    	session=null;
	    }
	    
	    if(neighborhoodGraph == null) return;
	    
	    //DIJKSTRA TIME!
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream("log.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error opening log file");
			return;
		}
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
	    PrintWriter printWriter = new PrintWriter(outputStreamWriter, true);
	    
	    MinPathsCalculator minPathsCalculator;
		minPathsCalculator = new MinPathsCalculator(busStopIds, neighborhoodGraph);
		int totalNumberMinPaths = 0;
		
	    for (String stopId: neighborhoodGraph.getStopWithNeighborhood()) {
			List<MinPath> minPaths = minPathsCalculator.getMinPathsFromOneStop(stopId);
			int numberMinPaths = minPaths.size();
			totalNumberMinPaths += numberMinPaths;
			printWriter.println("====================================================");
			printWriter.println("Stop: "+stopId);
		    printWriter.println("Number of minimum paths found: "+numberMinPaths);
			printWriter.println("Total number of minimum paths found: "+totalNumberMinPaths);
			printWriter.println("====================================================");
			System.out.println("====================================================");
			System.out.println("Stop: "+stopId);
		    System.out.println("Number of minimum paths found: "+numberMinPaths);
			System.out.println("Total number of minimum paths found: "+totalNumberMinPaths);
			System.out.println("====================================================");
	    }
	    
	    printWriter.close();
	    
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
    		String nextStopId = nextStop.getId();
    		if(thisStopId.equals(nextStopId)){
    			//due to error in DB -> multiple line in single line. Not my fault!
    			continue;
    		}
    		//already into the graph
    		if(neighborhoodGraph.hasStopThisNeighbor(thisStopId, nextStopId)){
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
    		neighborhoodGraph.addStopNeighbor(thisStopId, neighbor);
    	}
	}
	
	private static void findNeighborhoodOnRadius(String stopId, int radius){
		
		//compute distance between stops within the radius
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
			if(neighborhoodGraph.hasStopThisNeighbor(stopId, neighborId)){
				continue;
			}
			//insert into the graph
			Neighbor neighbor = new Neighbor(neighborId, distance, TransportType.FOOT);
			neighborhoodGraph.addStopNeighbor(stopId, neighbor);
			//2 - from neighborId to stopId
			//check if the neighbor is already into the graph
			if(neighborhoodGraph.hasStopThisNeighbor(neighborId, stopId)){
				continue;
			}
			//insert into the graph
			Neighbor neighbor2 = new Neighbor(stopId, distance, TransportType.FOOT);
			neighborhoodGraph.addStopNeighbor(neighborId, neighbor2);
    	}
	}
	
}

package it.polito.ai.es03.application;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import it.polito.ai.es03.model.hibernate.*;
import it.polito.ai.es03.model.neighborhoodlist.*;

public class App 
{
    public static void main( String[] args )
    {
        
    	SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    	Session session = sessionFactory.getCurrentSession();
	    Transaction tx=null;
	    try {
    		tx=session.beginTransaction();
			//download the list of all the stops from the DB
    		System.out.println( "Downloading BusStops from DB..." );
		    Query query = session.createQuery("from BusStop");
		    List<BusStop> busStops = query.list();
		    //insert into the db the list of the geographic stops
		    for (BusStop busStop : busStops) {
		    	BusStopGeo busStopGeo = new BusStopGeo(busStop);
		    	session.save(busStopGeo);
		    	System.out.println( "Inserted BusStopGeo:"+busStopGeo.getId());
		    }  
		    tx.commit();
	    } finally {
	    	if (session!=null && session.isOpen()) session.close(); 
	    	session=null;
	    }
    	
    	System.out.println( "Completed dumping of BusStopGeo!" );
    }
}

package it.polito.ai.es03.model.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometries;
import org.geolatte.geom.Point;
import org.geolatte.geom.crs.CoordinateReferenceSystem;
import org.geolatte.geom.crs.CrsRegistry;

@Entity
public class BusStopGeo {
	
	@Id
	private String id;
	private String name;
	private Point<G2D> position;
	
	public BusStopGeo(BusStop busStop){
		
		id = busStop.getId();
		name = busStop.getName();
		
		G2D g2dPosition = new G2D(busStop.getLng(), busStop.getLat());
		CoordinateReferenceSystem<G2D> crs = CrsRegistry.getGeographicCoordinateReferenceSystemForEPSG(4326);
		position = Geometries.mkPoint(g2dPosition, crs);
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Point<G2D> getPosition() {
		return position;
	}

	public void setPosition(Point<G2D> position) {
		this.position = position;
	}

}

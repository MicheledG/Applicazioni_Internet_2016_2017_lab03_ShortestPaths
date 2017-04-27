package it.polito.ai.es03.dijkstra.model;

import it.polito.ai.es03.dijkstra.model.Transport.TransportType;

public class Neighbor {

	private String id;
	private int distance; //meter
	private Transport transport;
	
	public Neighbor(String id, double distance, TransportType transportType){
		this.id = id;
		this.distance = ((Double)distance).intValue();
		this.transport = new Transport(transportType);
	}

	public String getId() {
		return id;
	}

	public int getDistance() {
		return distance;
	}

	public Transport getTransport() {
		return transport;
	}

	public int getCost(){ //distance/speed speed = time -> seconds
		return (distance/transport.getCoeff());
	}
	
}

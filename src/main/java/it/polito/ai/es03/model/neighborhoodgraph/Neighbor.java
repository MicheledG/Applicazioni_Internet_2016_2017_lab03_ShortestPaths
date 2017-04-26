package it.polito.ai.es03.model.neighborhoodgraph;

import it.polito.ai.es03.model.neighborhoodgraph.Transport.TransportType;

public class Neighbor {

	private String id;
	private double distance;
	private Transport transport;
	
	public Neighbor(String id, double distance, TransportType transportType){
		this.id = id;
		this.distance = distance;
		this.transport = new Transport(transportType);
	}

	public String getId() {
		return id;
	}

	public double getDistance() {
		return distance;
	}

	public Transport getTransport() {
		return transport;
	}

	public Double getCost(){ //distance / speed = time
		return distance/transport.getCoeff();
	}
	
}

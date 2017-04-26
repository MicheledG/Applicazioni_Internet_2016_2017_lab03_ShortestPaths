package it.polito.ai.es03.model.neighborhoodgraph;

public class Transport {

	public enum TransportType{BUS, FOOT};
	
	private TransportType type;
	private int coeff;
	
	private static final int BUS_SPEED = 20; //medium speed almost 20km/h  
	private static final int FOOT_SPEED = 5; //medium speed almost 5km/h
	
	public Transport(TransportType type){
		this.type = type;
		
		switch (type) {
		case BUS:
			coeff = Transport.BUS_SPEED; 
			break;
		case  FOOT:
			coeff = Transport.FOOT_SPEED; 
			break;
		}
	}

	public TransportType getType() {
		return type;
	}

	public int getCoeff() {
		return coeff;
	}
	
}

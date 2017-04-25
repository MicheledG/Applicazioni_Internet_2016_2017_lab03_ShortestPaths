package it.polito.ai.es03.model.neighborhoodgraph;

public class Transport {

	public enum TransportType{BUS, FOOT};
	
	private TransportType type;
	private int coeff;
	
	private static final int BUS_COST = 1;  
	private static final int FOOT_COST = 2;
	
	public Transport(TransportType type){
		this.type = type;
		
		switch (type) {
		case BUS:
			coeff = Transport.BUS_COST; 
			break;
		case  FOOT:
			coeff = Transport.FOOT_COST; 
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

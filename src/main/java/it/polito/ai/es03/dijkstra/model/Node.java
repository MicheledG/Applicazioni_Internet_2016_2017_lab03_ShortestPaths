package it.polito.ai.es03.dijkstra.model;

public class Node {
	
	private String id;
	private String previousNodeId;
	private int cost;
	private Transport transport;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPreviousNodeId() {
		return previousNodeId;
	}
	public void setPreviousNodeId(String previousNodeId) {
		this.previousNodeId = previousNodeId;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public Transport getTransport() {
		return transport;
	}
	public void setTransport(Transport transport) {
		this.transport = transport;
	}
	
}

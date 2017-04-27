package it.polito.ai.es03.dijkstra.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeighborhoodGraph {

	private Map<String, Map<String, Neighbor>> neighborhoods;
	
	public NeighborhoodGraph(){
		neighborhoods = new HashMap<String, Map<String, Neighbor>>();
	}
	
	public void addStopNeighbor(String stopId, Neighbor neighbor){
				
		System.out.println("=====================");
		System.out.println("Neighborhood");
		System.out.println("from stop: "+stopId);
		System.out.println("to stop: "+neighbor.getId());
		System.out.println("transport type: "+neighbor.getTransport().getType().toString());
		System.out.println("distance: "+neighbor.getDistance()+"m");
		System.out.println("cost: "+neighbor.getCost());
		
		if(!neighborhoods.containsKey(stopId)){
			Map<String, Neighbor> neighborhood = new HashMap<String, Neighbor>();
			neighborhoods.put(stopId, neighborhood);
		}
		
		Map<String, Neighbor> neighborhood = neighborhoods.get(stopId);
		if(!neighborhood.containsKey(neighbor.getId())){
			neighborhood.put(neighbor.getId(), neighbor);
			neighborhoods.put(stopId, neighborhood);
			System.out.println("Added!");
		}else{
			System.out.println("Already into graph!");
		}
		System.out.println("=====================");
	}
	
	public Neighbor getStopNeighbor(String stopId, String neighborId){
		
		if(neighborhoods.containsKey(stopId)){
			if(neighborhoods.get(stopId).containsKey(neighborId)){
				return neighborhoods.get(stopId).get(neighborId);
			}
		}
		
		return null;
	}
	
	public Map<String, Neighbor> getStopNeighborhood(String id){
		if(!neighborhoods.containsKey(id)) {
			return null;
		}
		else{
			return neighborhoods.get(id);
		}
	}
	
	public boolean hasStopNeighborhood(String stopId){
		return neighborhoods.containsKey(stopId);
	}
	
	public boolean hasStopThisNeighbor(String stopId, String possibleNeighborId){
		
		System.out.println("=====================");
		System.out.println("Neighborhood");
		System.out.println("from stop: "+stopId);
		System.out.println("to stop: "+possibleNeighborId);
		
		if(!neighborhoods.containsKey(stopId)){
			System.out.println("Not in graph!");
			System.out.println("=====================");
			return false;
		}
		else{
			Map<String, Neighbor> neighborhood = neighborhoods.get(stopId);
			if(neighborhood.containsKey(possibleNeighborId)){
				System.out.println("Already into graph!");
				System.out.println("=====================");
				return true;
			}
			else{
				System.out.println("Not in graph!");
				System.out.println("=====================");
				return false;
			}
		}
	}
	
	public Set<String> getStopWithNeighborhood(){
		return neighborhoods.keySet();
	}
	
}

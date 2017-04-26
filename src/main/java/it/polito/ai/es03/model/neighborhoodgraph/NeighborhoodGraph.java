package it.polito.ai.es03.model.neighborhoodgraph;

import java.util.HashMap;
import java.util.Map;

public class NeighborhoodGraph {

	private Map<String, Map<String, Neighbor>> neighborhoods;
	
	public NeighborhoodGraph(){
		neighborhoods = new HashMap<String, Map<String, Neighbor>>();
	}
	
	public void addNeighbor(String id, Neighbor neighbor){
				
		System.out.println("=====================");
		System.out.println("Neighborhood");
		System.out.println("from stop: "+id);
		System.out.println("to stop: "+neighbor.getId());
		System.out.println("transport type: "+neighbor.getTransport().getType().toString());
		System.out.println("distance: "+neighbor.getDistance()+"m");
		System.out.println("cost: "+neighbor.getCost());
		
		if(!neighborhoods.containsKey(id)){
			Map<String, Neighbor> neighborhood = new HashMap<String, Neighbor>();
			neighborhoods.put(id, neighborhood);
		}
		
		Map<String, Neighbor> neighborhood = neighborhoods.get(id);
		if(!neighborhood.containsKey(neighbor.getId())){
			neighborhood.put(neighbor.getId(), neighbor);
			neighborhoods.put(id, neighborhood);
			System.out.println("Added!");
		}else{
			System.out.println("Already into graph!");
		}
		System.out.println("=====================");
	}
	
	public Map<String, Neighbor> getNeighborhood(String id){
		if(!neighborhoods.containsKey(id)) {
			return null;
		}
		else{
			return neighborhoods.get(id);
		}
	}
	
	public boolean hasThisNeighbor(String id, String possibleNeighborId){
		
		System.out.println("=====================");
		System.out.println("Neighborhood");
		System.out.println("from stop: "+id);
		System.out.println("to stop: "+possibleNeighborId);
		
		if(!neighborhoods.containsKey(id)){
			System.out.println("Not in graph!");
			System.out.println("=====================");
			return false;
		}
		else{
			Map<String, Neighbor> neighborhood = neighborhoods.get(id);
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
	
}

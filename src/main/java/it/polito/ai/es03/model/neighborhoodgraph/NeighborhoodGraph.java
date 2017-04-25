package it.polito.ai.es03.model.neighborhoodgraph;

import java.util.HashMap;
import java.util.Map;

public class NeighborhoodGraph {

	private Map<String, Map<String, Neighbor>> neighborhoods;
	
	public NeighborhoodGraph(){
		neighborhoods = new HashMap<String, Map<String, Neighbor>>();
	}
	
	public void addNeighbor(String id, Neighbor neighbor){
				
		
		if(!neighborhoods.containsKey(id)){
			Map<String, Neighbor> neighborhood = new HashMap<String, Neighbor>();
			neighborhoods.put(id, neighborhood);
		}
		
		Map<String, Neighbor> neighborhood = neighborhoods.get(id);
		if(!neighborhood.containsKey(neighbor.getId())){
			neighborhood.put(neighbor.getId(), neighbor);
			neighborhoods.put(id, neighborhood);
		}
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
		if(!neighborhoods.containsKey(id)){
			return false;
		}
		else{
			Map<String, Neighbor> neighborhood = neighborhoods.get(id);
			return neighborhood.containsKey(possibleNeighborId);
		}
	}
	
}

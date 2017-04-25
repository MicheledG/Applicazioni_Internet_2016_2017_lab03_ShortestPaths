package it.polito.ai.es03.model.neighborhoodlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeighborhoodGraph {

	Map<String, List<Neighbor>> neighborhoodMap;
	
	public NeighborhoodGraph(){
		neighborhoodMap = new HashMap<String, List<Neighbor>>();
	}
	
	public void addNeighbor(String id, Neighbor neighbor){
		
		if(!neighborhoodMap.containsKey(id)){
			List<Neighbor> neighborhood = new ArrayList<Neighbor>();
			neighborhoodMap.put(id, neighborhood);
		}
		
		List<Neighbor> neighborhood = neighborhoodMap.get(id);
		if(!neighborhood.contains(neighbor)){
			neighborhood.add(neighbor);
		}
	}
	
	public List<Neighbor> getNeighborhood(String id){
		if(!neighborhoodMap.containsKey(id)) {
			return null;
		}
		else{
			return neighborhoodMap.get(id);
		}
	}
	
}

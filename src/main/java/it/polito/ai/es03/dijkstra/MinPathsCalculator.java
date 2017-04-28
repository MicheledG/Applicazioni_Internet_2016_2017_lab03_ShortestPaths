package it.polito.ai.es03.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.ai.es03.dijkstra.model.Neighbor;
import it.polito.ai.es03.dijkstra.model.NeighborhoodGraph;
import it.polito.ai.es03.dijkstra.model.Node;
import it.polito.ai.es03.dijkstra.model.mongo.Edge;
import it.polito.ai.es03.dijkstra.model.mongo.MinPath;

public class MinPathsCalculator {
	
	private NeighborhoodGraph neighborhoodGraph;
	private List<String> stopIds;
	
	public MinPathsCalculator(List<String> stopIds, NeighborhoodGraph neighborhoodGraph) {
		this.stopIds = stopIds;
		this.neighborhoodGraph = neighborhoodGraph;
	}
	
	public List<MinPath> getMinPathsFromOneStop(String initialStopId){
		
		if(!neighborhoodGraph.hasStopNeighborhood(initialStopId)){
			//stop without neighborhood
			return null;
		}
		
		//initialize data structure to execute dijstra algorithm
		Map<String, Node> unvisitedSet = new HashMap<String, Node>();
		for (String stopId: stopIds) {
			Node node = new Node();
			node.setId(stopId);
			node.setCost(-1);
			node.setPreviousNodeId(null);
			unvisitedSet.put(node.getId(), node);
		}
				
		Map<String, Node> visitedSet = new HashMap<String, Node>();
		
		//set up the initial node
		Node initialNode = unvisitedSet.remove(initialStopId);
		if(initialNode == null){
			//System.out.println("***********************");
			//System.out.println("Error - corrupted logic");
			//System.out.println("***********************");
			return null;
		}
		initialNode.setCost(0);
		visitedSet.put(initialNode.getId(), initialNode);
		//set up the initial node neighborhood
		Map<String, Neighbor> initialNodeNeighborhood = this.neighborhoodGraph.getStopNeighborhood(initialNode.getId());
		for (Map.Entry<String, Neighbor> neighborhoodEntry: initialNodeNeighborhood.entrySet()) {
			Neighbor neighbor = neighborhoodEntry.getValue();
			Node node = unvisitedSet.get(neighbor.getId());
			if(node == null){
				//System.out.println("Hellone!!!");
			}
			node.setPreviousNodeId(initialNode.getId());
			node.setCost(neighbor.getCost());
			node.setTransport(neighbor.getTransport());
		}
		
		//start computing the paths populating visited set
		while(true){
			
			//checks end conditions
			Node nearestNode = extractNearestNode(unvisitedSet);
			if(nearestNode == null){
				//there are no reachable nodes
				break;
			}
			
			visitedSet.put(nearestNode.getId(), nearestNode);
			
			if(unvisitedSet.isEmpty()){
				//finished
				break;
			}
			
			//update unvisited node information
			if(!this.neighborhoodGraph.hasStopNeighborhood(nearestNode.getId())){
				continue;
			}
			
			Map<String, Neighbor> nearestNodeNeighborhood = neighborhoodGraph.getStopNeighborhood(nearestNode.getId());
			for(Map.Entry<String, Neighbor> neighborhoodEntry: nearestNodeNeighborhood.entrySet()){
				Neighbor neighbor = neighborhoodEntry.getValue();
				Node node = unvisitedSet.get(neighbor.getId());
				if(node == null){
					//already visited node
					continue;
				}
				
				int tmpCost = nearestNode.getCost() + neighbor.getCost();
				
				if(node.getCost() == -1 || node.getCost() > tmpCost){
					node.setPreviousNodeId(nearestNode.getId());
					node.setCost(tmpCost);
					node.setTransport(neighbor.getTransport());
				}
			}
		}
		
		//completed Dijstra algorithm -> extrac MinPaths objects from Visited Set informations
		//each node of the visited set, except the initial one,  represent a minimum path between the initial node and others
		List<MinPath> minPaths = new ArrayList<MinPath>();
		
		for(Map.Entry<String, Node> visitedSetEntry : visitedSet.entrySet()){
			Node lastPathNode = visitedSetEntry.getValue();
			
			if(lastPathNode.getId() == initialNode.getId())
				continue;
			
			MinPath minPath = getMinPath(visitedSet, lastPathNode);
			minPaths.add(minPath);
		}
		
		
		return minPaths;
		
	}

	private Node extractNearestNode(Map<String, Node> unvisitedSet) {

		String nearestNodeId = null;
		double minimumCost = -1;
		
		for (Map.Entry<String, Node> unvisitedSetEntry : unvisitedSet.entrySet()) {
			Node node = unvisitedSetEntry.getValue();
			double cost = node.getCost();
			if(cost != -1){
				if(minimumCost == -1){
					minimumCost = cost;
					nearestNodeId = node.getId();
				}
				else{
					if(cost<minimumCost){
						minimumCost = cost;
						nearestNodeId = node.getId();
					}
				}
			}
		}
		
		if(nearestNodeId != null){
			return unvisitedSet.remove(nearestNodeId);
		}
		else{
			return null;
		}
		
	}

	private MinPath getMinPath(Map<String, Node> visitedSet, Node lastPathNode) {
		
		MinPath minPath = new MinPath();
		List<Edge> edges = new ArrayList<Edge>();
		minPath.setEdges(edges);
		minPath.setIdDestination(lastPathNode.getId());
		minPath.setTotalCost(0);
		
		getPreviousEdgeRecursive(visitedSet, minPath, lastPathNode);
		
		//System.out.println("=======================");
		//System.out.println("Minimum path info:");
		//System.out.println("from: "+minPath.getIdSource());
		//System.out.println("to: "+minPath.getIdDestination());
		//System.out.println("total cost: "+minPath.getTotalCost());
		//System.out.println("");
		//System.out.println("Edges:");
		//int i = minPath.getEdges().size() - 1;
		//int j = 1;
		//for(; i > -1; i--, j++){
			//Edge edge = minPath.getEdges().get(i);
			//System.out.println("*****************");
			//System.out.println("Edge nr."+j);
			//System.out.println("from: "+edge.getIdSource());
			//System.out.println("to: "+edge.getIdDestination());
			//System.out.println("cost: "+edge.getCost());
			//System.out.println("mode: "+edge.isMode());
			//System.out.println("*****************");
		//}
		
		//System.out.println("=======================");
		
		return minPath;
	}

	private void getPreviousEdgeRecursive(Map<String, Node> visitedSet, MinPath minPath, Node lastNode) {

		if(lastNode.getPreviousNodeId() == null){
			//initial node
			minPath.setIdSource(lastNode.getId());
			return;
		}
		else{
			//not initial node
			Edge edge = new Edge();
			edge.setIdDestination(lastNode.getId());
			edge.setIdSource(lastNode.getPreviousNodeId());
			int edgeCost = neighborhoodGraph.getStopNeighbor(edge.getIdSource(), edge.getIdDestination()).getCost();
			edge.setCost(edgeCost);
			switch(lastNode.getTransport().getType()){
			case BUS:
				edge.setMode(false);
				break;
			case FOOT:
				edge.setMode(true);
				break;
			}
			minPath.setTotalCost(minPath.getTotalCost() + edge.getCost());
			minPath.getEdges().add(edge);
			
			Node previousNode = visitedSet.get(lastNode.getPreviousNodeId());
			getPreviousEdgeRecursive(visitedSet, minPath, previousNode);
			return;
		}
	}
	
}

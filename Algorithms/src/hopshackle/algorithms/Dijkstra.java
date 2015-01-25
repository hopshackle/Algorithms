package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

import hopshackle.algorithms.Edge;

public class Dijkstra {

	private List<List<Edge>> adjacencyList = new ArrayList<List<Edge>>();
	private List<Integer> processedNodes = new ArrayList<Integer>();
	private Hashtable<Integer, Integer> minimumDistances = new Hashtable<Integer, Integer>();
	private PriorityQueue<DijkstraNode> nextNodeToTry = new PriorityQueue<DijkstraNode>();

	public Dijkstra(File file) {
		int maxNode = 0;
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer headNode = Integer.valueOf(st.nextToken());
			if (headNode >= maxNode) {
				for (int loop = maxNode; loop <= headNode; loop++)
					adjacencyList.add(new ArrayList<Edge>());
				maxNode = headNode;
			}
			while (st.hasMoreTokens()) {
				String adjacentNode = st.nextToken();
				StringTokenizer st2 = new StringTokenizer(adjacentNode, ",");
				int tailNode = Integer.valueOf(st2.nextToken());
				int edgeLength = Integer.valueOf(st2.nextToken());
				Edge newEdge = new Edge(headNode, tailNode, edgeLength);
				if (tailNode >= maxNode) {
					for (int loop = maxNode; loop <= tailNode; loop++)
						adjacencyList.add(new ArrayList<Edge>());
					maxNode = tailNode;
				}
				updateGraph(newEdge);
			}
		}
	}

	private void updateGraph(Edge e) {
		List<Edge> currentEdges = adjacencyList.get(e.headNode);
		if (currentEdges == null) {
			currentEdges = new ArrayList<Edge>();
		}
		currentEdges.add(e);
	}

	public void generateShortestPathsFrom(int start) {
		processNode(start, 0);
		try {
			do {
				DijkstraNode shortestPathToUnexploredSpace = nextNodeToTry.remove();
				if (processedNodes.contains(shortestPathToUnexploredSpace.node)) continue; 	// already found a shorter path to this one

				processNode(shortestPathToUnexploredSpace.node, shortestPathToUnexploredSpace.getDijkstraDistance());
			} while (true);
		} catch (NoSuchElementException e) {
			// No more routes to try
			return;
		}
	}

	public int minPath(int destination) {
		if (processedNodes.contains(destination)) return minimumDistances.get(destination);
		return Integer.MAX_VALUE;
	}

	private void processNode(int start, int distanceToThisNode) {
		minimumDistances.put(start, distanceToThisNode);
		processedNodes.add(start);
		List<Edge> edgesFromAddedNode = adjacencyList.get(start);
		for (Edge e : edgesFromAddedNode) {
			if (!processedNodes.contains(e.tailNode)) {
				nextNodeToTry.add(new DijkstraNode(e.tailNode, e, distanceToThisNode));
			}
		}
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\Dijkstra.txt";
		if (args.length > 0)
			filename = args[0];
		Dijkstra dijkstra = new Dijkstra(new File(filename));
		dijkstra.generateShortestPathsFrom(1);
		//		7,37,59,82,99,115,133,165,188,197
		System.out.println("#7: " + dijkstra.minPath(7));
		System.out.println("#37: " + dijkstra.minPath(37));
		System.out.println("#59: " + dijkstra.minPath(59));
		System.out.println("#82: " + dijkstra.minPath(82));
		System.out.println("#99: " + dijkstra.minPath(99));
		System.out.println("#115: " + dijkstra.minPath(115));
		System.out.println("#133: " + dijkstra.minPath(133));
		System.out.println("#165: " + dijkstra.minPath(165));
		System.out.println("#188: " + dijkstra.minPath(188));
		System.out.println("#197: " + dijkstra.minPath(197));
	}
}

class DijkstraNode implements Comparable<DijkstraNode> {

	final int node;
	final Edge linkingEdge;
	final int distanceToEdgeOfExploredSpace;

	DijkstraNode(int candidateNode, Edge candidateEdge, int distanceToPrecursorNode) {
		node = candidateNode;
		linkingEdge = candidateEdge;
		distanceToEdgeOfExploredSpace = distanceToPrecursorNode;
	}

	int getDijkstraDistance() {
		return distanceToEdgeOfExploredSpace + linkingEdge.length;
	}

	@Override
	public int compareTo(DijkstraNode dn) {
		return getDijkstraDistance() - dn.getDijkstraDistance();
	}
}

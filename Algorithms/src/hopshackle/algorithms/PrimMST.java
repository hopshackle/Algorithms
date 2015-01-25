package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

public class PrimMST {
	
	private List<Edge> edges;
	private List<Edge> MST = new ArrayList<Edge>();
	private int numberOfNodes;
	private List<Integer> exploredNodes = new ArrayList<Integer>();
	private List<Integer> unconnectedNodes = new ArrayList<Integer>();

	public PrimMST(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());
		
		edges = new ArrayList<Edge>();
		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer headNode = Integer.valueOf(st.nextToken());
			Integer tailNode = Integer.valueOf(st.nextToken());
			Integer cost = Integer.valueOf(st.nextToken());
			edges.add(new Edge(headNode, tailNode, cost));
			if (!unconnectedNodes.contains(headNode))
				unconnectedNodes.add(headNode);
			if (!unconnectedNodes.contains(tailNode))
				unconnectedNodes.add(tailNode);
		}
		
		exploredNodes.add(unconnectedNodes.remove(0));	// pick start vertex
		
		do {
			Edge nextEdge = findLowestWeightEdgeIntoUnexploredSpace();
			if (!exploredNodes.contains(nextEdge.headNode)) {
				exploredNodes.add(nextEdge.headNode);
			}
			if (!exploredNodes.contains(nextEdge.tailNode)) {
				exploredNodes.add(nextEdge.tailNode);
			}
			unconnectedNodes.removeAll(exploredNodes);
			MST.add(nextEdge);
			
		} while (exploredNodes.size() != numberOfNodes);
	
		long totalWeight = 0;
		for (Edge e : MST) {
			totalWeight += e.length;
		}
		System.out.println(String.format("Total edges: %d, and total weight of edges: %d", MST.size(), totalWeight));
		
	}

	private Edge findLowestWeightEdgeIntoUnexploredSpace() {
		List<Edge> edgesToRemove = new ArrayList<Edge>();
		Edge candidateEdge = null;
		int lowestWeightSoFar = Integer.MAX_VALUE;
		for (Edge e : edges) {
			int borderStatus = edgeStatus(e);	// 1 means in known space; 2 means in unknown space; 3 means straddles the two
			if (borderStatus == 1)
				edgesToRemove.add(e);
			if (borderStatus == 3 && e.length < lowestWeightSoFar) {
				candidateEdge = e;
				lowestWeightSoFar = e.length;
			}
		}
		
		edges.removeAll(edgesToRemove);
		return candidateEdge;
	}

	private int edgeStatus(Edge e) {
		boolean headNodeInKnownSpace = exploredNodes.contains(e.headNode);
		boolean tailNodeInKnownSpace = exploredNodes.contains(e.tailNode);
		
		if (headNodeInKnownSpace && tailNodeInKnownSpace)
			return 1;
		if (!headNodeInKnownSpace && !tailNodeInKnownSpace)
			return 2;
		return 3;
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\edges.txt";
		if (args.length > 0)
			filename = args[0];
		new PrimMST(new File(filename));
	}

}

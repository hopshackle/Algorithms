package hopshackle.algorithms;

import hopshackle.simulation.*;

import java.io.*;
import java.util.*;

public class MinCut {

	private List<List<Integer>> adjacencyMatrix = new ArrayList<List<Integer>>(200);
	private List<Edge> edges = new ArrayList<Edge>();
	private int totalNodes;

	public MinCut(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer lineNumber = Integer.valueOf(st.nextToken());
			totalNodes++;
			List<Integer> connectedNodes = new ArrayList<Integer>();
			adjacencyMatrix.add(connectedNodes);
			while (st.hasMoreTokens()) {
				Integer node = Integer.valueOf(st.nextToken());
				connectedNodes.add(node);
				if (lineNumber < node) // to avoid doublecounting
					edges.add(new Edge(lineNumber, node));
			}
		}

		List<Edge> edgeCopy = HopshackleUtilities.cloneList(edges);
		int minCut = Integer.MAX_VALUE;
		for (int outerLoop = 0; outerLoop < 1200; outerLoop++) {
			for (int loop = 0; loop < totalNodes - 2; loop++) {
				Edge edgeToPrune = pickAnEdgeAtRandom();
				pruneEdgeFromGraph(edgeToPrune);
			}

			System.out.println(edges.size());
			if (edges.size() < minCut)
				minCut = edges.size();

			edges = HopshackleUtilities.cloneList(edgeCopy);
		}
		System.out.println("Minimum: " + minCut);
	}

	private Edge pickAnEdgeAtRandom() {
		int randomNumber = (int) (Math.random() * edges.size());
		return edges.get(randomNumber);
	}

	private void pruneEdgeFromGraph(Edge edgeToPrune) {
		int tailNode = edgeToPrune.tailNode;
		int headNode = edgeToPrune.headNode;
		List<Edge> selfLoops = new ArrayList<Edge>();
		List<Edge> changedEdges = new ArrayList<Edge>();
		List<Edge> oldVersionOfChangedEdges = new ArrayList<Edge>();

		for (Edge e : edges) {
			int newTailNode = 0; 
			int newHeadNode = 0;
			if (e.tailNode == tailNode) 
				newTailNode = headNode;
			if (e.headNode == tailNode)
				newHeadNode = headNode;

			if (newTailNode > 0 || newHeadNode > 0) {
				if (newTailNode == 0) newTailNode = e.tailNode;
				if (newHeadNode == 0) newHeadNode = e.headNode;
				if (newTailNode == newHeadNode)
					selfLoops.add(e);
				else {
					changedEdges.add(new Edge(newTailNode, newHeadNode));
					oldVersionOfChangedEdges.add(e);
				}
			}

		}

		edges.removeAll(oldVersionOfChangedEdges);
		edges.removeAll(selfLoops);
		edges.addAll(changedEdges);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\MinimumCut.txt";
		if (args.length > 0)
			filename = args[0];
		new MinCut(new File(filename));
	}

}

class Edge {

	final int tailNode;
	final int headNode;
	final int length;
	
	public Edge(Integer tail, Integer head) {
		this(tail, head , 1);
	}

	public Edge(Integer tail, Integer head, Integer length) {
		tailNode = tail;
		headNode = head;
		this.length = length;
	}

	public String toString() {
		return tailNode + " -> " + headNode;
	}
}

package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.*;

public class KruskalClustering {

	PriorityQueue<Edge> edges = new PriorityQueue<Edge>(20, new Comparator<Edge>() {
		@Override
		public int compare(Edge e1, Edge e2) {
			// return e1 - e2 (if the first argument should go first in a sorted list, then return a negative number)
			return e1.length - e2.length;
		}
	});

	int finalEdgeLength;
	int numberOfNodes, numberOfClusters;
	List<Integer> nodes, nodePointers;

	public KruskalClustering(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());

		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer headNode = Integer.valueOf(st.nextToken());
			Integer tailNode = Integer.valueOf(st.nextToken());
			Integer cost = Integer.valueOf(st.nextToken());
			edges.add(new Edge(headNode, tailNode, cost));
		}

		nodes = new ArrayList<Integer>(numberOfNodes);
		for (int i = 1; i <= numberOfNodes; i++) {
			nodes.add(i);
		}
		nodePointers = new ArrayList<Integer>(nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			nodePointers.add(i);	// so that nodePointer(i) contains the index of nodes that contains the name of the component
		}
		numberOfClusters = nodes.size();

		do {
			Edge candidateEdge = edges.remove();
			int component1 = getComponent(candidateEdge.headNode);
			int component2 = getComponent(candidateEdge.tailNode);
			
//			System.out.println(String.format("Candidate edge %s links components %d and %d", candidateEdge.toString(), component1, component2));
			
			if (component1 == component2)
				continue;

			mergeComponents(component1, component2);

			finalEdgeLength = candidateEdge.length;
			System.out.println("Length of edge added to remove cluster #" + numberOfClusters + " is " + finalEdgeLength);
			numberOfClusters--;
			
		} while (numberOfClusters > 1 && !edges.isEmpty());

	}

	private int getComponent(int node) {
		int index = 0, parent = 0;
		boolean foundUltimateParent = false;
		do {
			index = nodes.indexOf(node);
			int parentPointer = nodePointers.get(index);
			parent = nodes.get(parentPointer);
			if (node == parent) {	// i.e. parent
				foundUltimateParent = true;
			} else {
				node = parent;
			}
		} while (!foundUltimateParent);
		return parent;
	}

	private void mergeComponents(int component1, int component2) {
		int index1 = nodes.indexOf(component1);
		int index2 = nodes.indexOf(component2);
		nodePointers.set(index1, index2);
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\kruskalclustering.txt";
		if (args.length > 0)
			filename = args[0];
		new KruskalClustering(new File(filename));
	}
}

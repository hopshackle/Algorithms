package hopshackle.algorithms;

import hopshackle.simulation.*;

import java.io.*;
import java.util.*;

public class SCCComputer {

	private List<List<Integer>> adjacencyList = new ArrayList<List<Integer>>();
	private List<List<Integer>> reverseAdjacencyList = new ArrayList<List<Integer>>();
	private int[] finishingTimes;
	private int[] orderForSecondLoop;
	private int[] SCCMarker;
	private HashMap<Integer, Integer> SCCCounter;
	private boolean[] explored;//
	private int currentTime;
	private int numberOfNodes;

	public SCCComputer(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());
		
		finishingTimes = new int[numberOfNodes+1];
		orderForSecondLoop = new int[numberOfNodes+1];
		SCCMarker = new int[numberOfNodes+1];
		
		
		for (int loop = 0; loop < numberOfNodes+1; loop++) {
			adjacencyList.add(new ArrayList<Integer>());
			reverseAdjacencyList.add(new ArrayList<Integer>());
		}

		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer headNode = Integer.valueOf(st.nextToken());
			Integer tailNode = Integer.valueOf(st.nextToken());
			updateGraph(adjacencyList, headNode, tailNode);
			updateGraph(reverseAdjacencyList, tailNode, headNode);
		}
	}
	
	
	public int[] calculateSCC() {
		
		runFirstLoop();
		for (int loop = 1; loop < numberOfNodes+1; loop++) {
			orderForSecondLoop[finishingTimes[loop]] = loop;
		}
		runSecondLoop();
		
		return SCCMarker;

		/*

		int[] pointersFromNodeToSCC = SCCMarker.clone();
		SCCCounter = new HashMap<Integer, Integer>();
		for (int loop = 1; loop < numberOfNodes+1; loop++) {
			if (SCCMarker[loop] > 0) {
				int thisSCCName = SCCMarker[loop];
				int nodesInThisSCC = 0;
				for (int countLoop = loop; countLoop < numberOfNodes+1; countLoop++) {
					if (thisSCCName == SCCMarker[countLoop]) {
						nodesInThisSCC++;
						SCCMarker[countLoop] = 0;
					}
				}
				SCCCounter.put(thisSCCName, nodesInThisSCC);
			}
		}

		return pointersFromNodeToSCC;
		*/
	}
	
	public void printResults() {
		for (int n = 0; n < 5; n++) {
			int highest = Collections.max(SCCCounter.values());
			System.out.println("#" + (n+1) + " has " + highest + " nodes.");
			for (int parentNode : SCCCounter.keySet()) {
				if (SCCCounter.get(parentNode) == highest) {
					SCCCounter.put(parentNode, 1);
					break;
				}
			}
		}

		for (Integer SCCName : SCCCounter.keySet()) {
			if (SCCCounter.get(SCCName) > 200)
				System.out.println("Name: " + SCCName + ", Size: " + SCCCounter.get(SCCName));
		}
	}

	private void updateGraph(List<List<Integer>> graph, Integer head, Integer tail) {
		graph.get(head).add(tail);
	}

	private void runFirstLoop() {
		explored = new boolean[numberOfNodes+1];
		currentTime = numberOfNodes;
		for (int loop = 1; loop < numberOfNodes+1; loop++) {
			if (!explored[loop])
				DFS(reverseAdjacencyList, loop, 0, finishingTimes, null);
		}
	}
	private void runSecondLoop() {
		explored = new boolean[numberOfNodes+1];
		currentTime = numberOfNodes;
		for (int loop = 1; loop < numberOfNodes+1; loop++) {
			if(!explored[orderForSecondLoop[loop]])
				DFS(adjacencyList, orderForSecondLoop[loop], orderForSecondLoop[loop], null, SCCMarker);
		}
	}

	private void DFS(List<List<Integer>> graph, int startNode, int parentNode, int[] finishingTimesArray, int[] parentNodeArray) {
		explored[startNode] = true;
		for (Integer toNode : graph.get(startNode)) {
			if (!explored[toNode])
				DFS(graph, toNode, parentNode, finishingTimesArray, parentNodeArray);
		}
		if (finishingTimesArray != null) {
			finishingTimesArray[startNode] = currentTime;
			currentTime--;
		}
		if (parentNodeArray != null)
			parentNodeArray[startNode] = parentNode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\SCC.txt";
		if (args.length > 0)
			filename = args[0];
		SCCComputer thing = new SCCComputer(new File(filename));
		thing.calculateSCC();
		thing.printResults();
	}

}




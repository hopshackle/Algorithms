package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FloydWarshall {

	private int[][] thisIteration;
	private int[][] lastIteration;
	private int numberOfNodes;
	private List<Edge> edges;


	public FloydWarshall(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());
		thisIteration = new int[numberOfNodes][numberOfNodes];

		edges = new ArrayList<Edge>();
		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			Integer headNode = Integer.valueOf(st.nextToken());
			Integer tailNode = Integer.valueOf(st.nextToken());
			Integer cost = Integer.valueOf(st.nextToken());
			edges.add(new Edge(headNode, tailNode, cost));
		}

		int infinity = (Integer.MAX_VALUE / 2) - 3;
		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < numberOfNodes; j++){
				thisIteration[i][j] = infinity;
			}
			thisIteration[i][i] = 0;
		}

		for (Edge e : edges) {
			thisIteration[e.headNode - 1][e.tailNode - 1] = e.length;
		}

		// Have now Initialised the array

		for (int k = 0; k < numberOfNodes; k++) {
			lastIteration = thisIteration.clone();
			for (int i = 0; i < numberOfNodes; i++) {
				for (int j = 0; j < numberOfNodes; j++){
					int firstOption = lastIteration[i][j];
					int secondOption = lastIteration[i][k] + lastIteration[k][j];
					if (secondOption > infinity) secondOption = infinity;
					thisIteration[i][j] = Math.min(firstOption, secondOption);
				}
			}
		}

		// We have now run the algorithm; time to analyse results

		int shortestPath = Integer.MAX_VALUE;
		int shortestSelfPath = 0;

		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < numberOfNodes; j++){
				if (thisIteration[i][j] < shortestPath)
					shortestPath = thisIteration[i][j];
			}
			if (thisIteration[i][i] < shortestSelfPath)
				shortestSelfPath = thisIteration[i][i];
		}

		System.out.println("Shortest Path has length of " + shortestPath);
		System.out.println("Shortest Self Path has length of " + shortestSelfPath);

	}

	public static void main(String[] args) {

		String filename = "C:\\Users\\James\\Downloads\\floyd.txt";
		if (args.length > 0)
			filename = args[0];
		new FloydWarshall(new File(filename));
	}

}

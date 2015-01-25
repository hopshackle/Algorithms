package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.*;

public class TravellingSalesman {

	private HashMap<Integer, float[]> currentLengthsPerSet;
	private HashMap<Integer, float[]> lastLengthsPerSet;
	private double[][] coordinates;
	private float[][] distances;
	private int numberOfNodes;    

	public TravellingSalesman(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());
		distances = new float[numberOfNodes][numberOfNodes];
		coordinates = new double[numberOfNodes][2];
		float[] startLength = new float[numberOfNodes];	// base case for start vertex
		currentLengthsPerSet = new HashMap<Integer, float[]>();
		currentLengthsPerSet.put(1, startLength);

		for (int nodeIndex = 0; nodeIndex < numberOfNodes; nodeIndex++) {
			String line = rawData.get(nodeIndex);
			StringTokenizer st = new StringTokenizer(line);
			double x = Double.valueOf(st.nextToken());
			double y = Double.valueOf(st.nextToken());
			coordinates[nodeIndex][0] = x;
			coordinates[nodeIndex][1] = y;
		}

		for (int startNode = 0; startNode < numberOfNodes; startNode++) {
			for (int endNode = startNode+1; endNode < numberOfNodes; endNode++) {
				double distance = Math.pow(coordinates[startNode][0] - coordinates[endNode][0], 2) +
						Math.pow(coordinates[startNode][1] - coordinates[endNode][1], 2);
				distance = Math.sqrt(distance);
				distances[startNode][endNode] = (float)distance;
				distances[endNode][startNode] = (float)distance;
			}
		}

		for (int sizeOfS = 2; sizeOfS <= numberOfNodes; sizeOfS++) {
			System.out.println("Starting sets S of size " + sizeOfS);
			lastLengthsPerSet = (HashMap<Integer, float[]>) currentLengthsPerSet.clone();
			currentLengthsPerSet.clear();
			// Now iterate over all of the entries in lastLength
			for (int lastS : lastLengthsPerSet.keySet()) {
				for (int j = 1; j <= numberOfNodes; j++) {
					int jBit = (int)Math.pow(2, j-1);
					// first check that vertex is not in S already
					if ((lastS & jBit) > 0) continue;

					// Now add vertex j, and check we haven't processed this new S already
					int newS = lastS + jBit;
					if (currentLengthsPerSet.containsKey(newS)) continue;

					float[] lengthsForNewS = new float[numberOfNodes];
					// to populate this, I need to iterate over each node in newS
					// treat this as the last node
					// and then find the shortest path to it
					for (int lastNode = 2; lastNode <= numberOfNodes; lastNode++) {
						int lastBit =  (int)Math.pow(2, lastNode-1);
						if ((newS & lastBit) == 0) continue;
						// Now calculate length of shortest length path from 1 to lastNode including all S
						// i.e. by checking each member of S (except for lastNode) as the penultimate node 
						//	on the route to lastNode
						// In addition 1, the start node, cannot be the penultimate node except in the
						// special case when we only have two nodes in newS
						float shortestPath = Float.MAX_VALUE;
						for (int penultimateNode = 1; penultimateNode <= numberOfNodes; penultimateNode++) {
							if (penultimateNode == lastNode) continue;
							if (penultimateNode == 1 && sizeOfS > 2) continue;
							int penultimateBit = (int)Math.pow(2, penultimateNode-1);
							if ((newS & penultimateBit) == 0) continue;	// penultimateNode not in S
							int previousS = newS - lastBit;
							float[] lengths = lastLengthsPerSet.get(previousS);
							float pathLength = lengths[penultimateNode-1] + distances[penultimateNode-1][lastNode-1];
							if (pathLength < shortestPath) 
								shortestPath = pathLength;
						}
						lengthsForNewS[lastNode-1] = shortestPath;
					}
					currentLengthsPerSet.put(newS, lengthsForNewS);
				}
			}
		}

		// at this stage we should have data for routes using all the nodes in the graph
		float[] lengthsToAll = currentLengthsPerSet.get((int)Math.pow(2, numberOfNodes) - 1);	// i.e. the data for all nodes
		float actualShortestTour = Float.MAX_VALUE;
		for (int i = 1; i < numberOfNodes; i++) {
			float tourOption = lengthsToAll[i] + distances[i][0];
			if (tourOption < actualShortestTour)
				actualShortestTour = tourOption;
		}

		System.out.println("Minimum TSP tour has length "+ actualShortestTour);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filename = "C:\\Users\\user\\Downloads\\tsp.txt";
		if (args.length > 0)
			filename = args[0];
		new TravellingSalesman(new File(filename));

	}

}

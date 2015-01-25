package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

public class HammingEdges {

	private int numberOfNodes;
	private List<List<HammingNode>> hammingNodesInBuckets = new ArrayList<List<HammingNode>>(25);
	private List<Edge> edges = new ArrayList<Edge>();
	public static String newline = System.getProperty("line.separator");

	public HammingEdges(File file) {
		for (int i = 0; i<25; i++) 
			hammingNodesInBuckets.add(new ArrayList<HammingNode>());

		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.get(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());

		for (int i = 1; i < rawData.size(); i++) {
			HammingNode hn = new HammingNode(rawData.get(i), i);
			hammingNodesInBuckets.get(hn.count).add(hn);
		}

		System.out.println("All HammingNodes created and bucketed.");

		for (int bucket = 0; bucket < 24; bucket++) {
			List<HammingNode> thisBucket = hammingNodesInBuckets.get(bucket);
			List<HammingNode> nextBucket = hammingNodesInBuckets.get(bucket+1);
			for (HammingNode hn1 : thisBucket) {
				for (HammingNode hn2 : nextBucket) {
					if (hn1.withinDistance(hn2, 1)) {
						edges.add(new Edge(hn1.nodeRef, hn2.nodeRef, 1));		// all edges with distance 1
					}
				}
			}
			System.out.println(String.format("Completed checking of bucket %d with bucket %d", bucket, bucket+1));
		}

		for (int bucket = 0; bucket < 25; bucket++) {
			List<HammingNode> thisBucket = hammingNodesInBuckets.get(bucket);
			for (HammingNode hn1 : thisBucket) {
				for (HammingNode hn2 : thisBucket) {
					if (hn1 != hn2 && hn1.withinDistance(hn2, 2)) {
						edges.add(new Edge(hn1.nodeRef, hn2.nodeRef, 2));	// edges with distance 2 between nodes in same bucket
					}
				}
				System.out.println(String.format("Completed checking of bucket %d with bucket %d", bucket, bucket));
				if (bucket < 23) {	// all 25 buckets need to be compared internally; but only 23 of them have an inter-bucket comparison
					List<HammingNode> nextBucket = hammingNodesInBuckets.get(bucket+2);
					for (HammingNode hn2 : nextBucket) {
						if (hn1.withinDistance(hn2, 2)) {
							edges.add(new Edge(hn1.nodeRef, hn2.nodeRef, 2));	// edges with distance 2 between nodes in different buckets
						}
					}
					System.out.println(String.format("Completed checking of bucket %d with bucket %d", bucket, bucket+2));
				}
			}
		}
		
		System.out.println("Total edges found: " + edges.size());

		// Now to write the data in a format that can be processed by KruskalClustering
		try {
			String fileName = "C:\\Users\\James\\Downloads\\hammingedgeoutput.txt";
			FileWriter outputWriter = new FileWriter(fileName, false);
			outputWriter.write(String.valueOf(numberOfNodes) + newline);
			for (Edge e : edges) {
				outputWriter.write(e.headNode + " " + e.tailNode + " " + e.length + newline);
			}
			outputWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\hammingedges.txt";
		if (args.length > 0)
			filename = args[0];
		new HammingEdges(new File(filename));
	}

}


class HammingNode {

	int nodeRef;
	int count;
	boolean[] foci = new boolean[24];

	public HammingNode(String data, int ref) {
		nodeRef = ref;
		StringTokenizer d = new StringTokenizer(data);
		count = 0;
		for (int loop = 0; loop < 24; loop++) {
			foci[loop] = d.nextToken().equals("1");
			if (foci[loop]) count++;
		}
	}

	public boolean withinDistance(HammingNode hn2, int maxDistance) {
		// only check foci differences up to maxDistance
		int distance = 0;
		for (int i = 0; i<24; i++) {
			if (foci[i] != hn2.foci[i]) {
				distance++;
				if (distance > maxDistance) return false;
			}
		}
		return true;
	}

}
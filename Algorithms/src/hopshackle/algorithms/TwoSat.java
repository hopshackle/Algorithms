package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

public class TwoSat {

	private int numberOfNodes;
	private int[] nodes;
	public static String newline = System.getProperty("line.separator");


	public TwoSat(File inputFile) {
		List<String> rawData = HopshackleUtilities.createListFromFile(inputFile);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		numberOfNodes = Integer.valueOf(hd.nextToken());	

		String fileName = "C:\\Users\\user\\Downloads\\SCC2Sat.txt";
		File outputFile = new File(fileName);
		
		try {
			FileWriter outputWriter = new FileWriter(outputFile, false);
			outputWriter.write(String.valueOf(numberOfNodes*2) + newline);
			
			for (String line : rawData) {
				StringTokenizer st = new StringTokenizer(line);
				int constraint1 = Integer.valueOf(st.nextToken());
				int constraint2 = Integer.valueOf(st.nextToken());
				int complement1 = constraint1 + numberOfNodes;
				if (constraint1 < 0) {
					complement1 = -constraint1;
					constraint1 = numberOfNodes - constraint1;
				}
				int complement2 = constraint2 + numberOfNodes;
				if (constraint2 < 0) {
					complement2 = -constraint2;
					constraint2 = numberOfNodes - constraint2;
				}

				// We need two edges. One is for "If !constraint1, then constraint2
				// The other is "if !constraint2 then constraint1
				// node numbering has node=true to be the node number (i.e. 1 to n)
				// and node=false to be node number + n (i.e. n+1 to 2n)
				outputWriter.write(complement1 + " " + constraint2 + newline);
				outputWriter.write(complement2 + " " + constraint1 + newline);
			}
			outputWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		rawData.clear();
		
		SCCComputer computer = new SCCComputer(outputFile);
		int[] SCC = computer.calculateSCC();
		
	//	computer.printResults();
		
		// Now check to see if any two nodes of same variable are in the same SCC
		for (int i = 1; i <= numberOfNodes; i++) {
	//		System.out.println(i + " has " + SCC[i] + " and " + SCC[i+numberOfNodes]);
			if (SCC[i] == SCC[i+numberOfNodes]) {
				System.out.println("TwoSat problem is not satisfiable");
				return;
			}
		}
		
		System.out.print("TwoSat problem is satisfiable");
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "C:\\Users\\user\\Downloads\\TwoSat.txt";
		if (args.length > 0)
			filename = args[0];
		new TwoSat(new File(filename));
	}

}

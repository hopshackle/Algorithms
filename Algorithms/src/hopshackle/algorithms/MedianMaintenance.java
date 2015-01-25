package hopshackle.algorithms;

import java.io.*;
import java.util.*;

import hopshackle.simulation.*;

public class MedianMaintenance {

	PriorityQueue<Integer> lowNumbers, highNumbers;

	public MedianMaintenance(File file) {
		List<String> integersAsStrings = HopshackleUtilities.createListFromFile(file);
		List<Integer> integers = HopshackleUtilities.convertToIntegers(integersAsStrings);

		lowNumbers = new PriorityQueue<Integer>();
		highNumbers = new PriorityQueue<Integer>();

		int total = 0;
		for (int n : integers) {
			total += calculateNewMedian(n);
	
		}
		
		System.out.println(total);
	}

	private int calculateNewMedian(int newNumber) {
		int lowTotal = lowNumbers.size();
		int highTotal = highNumbers.size();
		int topLow = 0;
		if (lowTotal > 0) 
			topLow = -lowNumbers.peek();

		if (newNumber < topLow) {
			lowNumbers.add(-newNumber);
			lowTotal++;
		} else {
			highNumbers.add(newNumber);
			highTotal++;
		}
		
		if (highTotal > lowTotal - 1) {
			int numberToMove = highNumbers.poll();
			lowNumbers.add(-numberToMove);
		}
		if (lowTotal > highTotal - 1) {
			int numberToMove = -lowNumbers.poll();
			highNumbers.add(numberToMove);
		}
		
		switch (lowNumbers.size() - highNumbers.size()) {
		case -1:
			return highNumbers.peek();
		case 0:
		case +1:
			return -lowNumbers.peek();
		}
		
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("C:\\Users\\James\\Downloads\\Median.txt");
		MedianMaintenance MM = new MedianMaintenance(file);
	}

}

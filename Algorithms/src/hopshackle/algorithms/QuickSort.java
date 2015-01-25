package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

public class QuickSort {
	
	long swaps = 0;

	public QuickSort(File file) {
		List<String> integersAsStrings = HopshackleUtilities.createListFromFile(file);
		List<Integer> integers = HopshackleUtilities.convertToIntegers(integersAsStrings);
		
		long comparisons = sortArray(integers);
		
		System.out.println("Total Comparisons: " + comparisons);
		System.out.println("Total Swaps: " + swaps);
	}
	
	
	private long sortArray(List<Integer> integers) {
		int n = integers.size();
		if (n < 2) return 0;	// base case
		int j = 1;
		int pivot = getPivotAndSwapIntoFirstPosition(integers);
		for (int loop = 1; loop < n; loop++) {
			if (integers.get(loop) < pivot) {
				swapPositions(integers, loop, j);
				j++;
			}
		}
		swapPositions(integers, 0, j-1); // put pivot into correct position
		
		long comparisons1 = sortArray(integers.subList(0, j-1));
		long comparisons2 = sortArray(integers.subList(j, n));
		
		return (n-1) + comparisons1 + comparisons2;
	}


	private int getPivotAndSwapIntoFirstPosition(List<Integer> integers) {
		int lastIndex = integers.size() - 1;
		int middleIndex = (integers.size() - 1) / 2;
		int first = integers.get(0);
		int last = integers.get(lastIndex);
		int middle = integers.get(middleIndex);
		
		int indexToUse = 0;
		if (first > middle && middle > last)
			indexToUse = middleIndex;
		if (last > middle && middle > first)
			indexToUse = middleIndex;
		if (first > last && last > middle)
			indexToUse = lastIndex;
		if (middle > last && last > first)
			indexToUse = lastIndex;

		swapPositions(integers, indexToUse, 0);
		return integers.get(0);
	}


	private void swapPositions(List<Integer> list, int i, int j) {
		if (i == j)
			return;
		swaps++;
		Integer temp = list.get(j);
		list.set(j, list.get(i));
		list.set(i, temp);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\Quicksort.txt";
		if (args.length > 0)
		filename = args[0];
		new QuickSort(new File(filename));
	}

}

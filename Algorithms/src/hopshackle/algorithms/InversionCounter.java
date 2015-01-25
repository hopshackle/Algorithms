package hopshackle.algorithms;

import hopshackle.simulation.*;
import java.io.*;
import java.util.*;

public class InversionCounter {

	public InversionCounter(File file) {
		List<String> integersAsStrings = HopshackleUtilities.createListFromFile(file);
		List<Integer> integers = HopshackleUtilities.convertToIntegers(integersAsStrings);
		
		MergedList ml = sortAndCountInversions(integers);
		
		System.out.println("Total Inversions: " + ml.inversions);
	}
	
	private MergedList sortAndCountInversions(List<Integer> integers) {
		int length = integers.size();
		if (length < 2) {
			return new MergedList(0, integers);
		}
		List<Integer> firstHalf = integers.subList(0, (length/2));
		List<Integer> secondHalf = integers.subList((length/2), length);
		
		MergedList leftList = sortAndCountInversions(firstHalf);
		MergedList rightList = sortAndCountInversions(secondHalf);
		MergedList ml = mergeAndCountInversions(leftList.list, rightList.list);
		ml.inversions += leftList.inversions;
		ml.inversions += rightList.inversions;
		return ml;
	}

	private MergedList mergeAndCountInversions(List<Integer> left, List<Integer> right) {
		int totalLength = left.size() + right.size();
		int leftLength = left.size();
		int rightLength = right.size();
		List<Integer> output = new ArrayList<Integer>();
		int leftPointer = 0;
		int rightPointer = 0;
		long inversions = 0;
		for (int loop = 0; loop < totalLength; loop++) {
			if ((rightPointer == rightLength) || leftPointer < leftLength && left.get(leftPointer) < right.get(rightPointer)) {
				output.add(left.get(leftPointer));
				leftPointer++;
				// No inversions to add
			} else {
				output.add(right.get(rightPointer));
				rightPointer++;
				inversions += leftLength - leftPointer;
			}
		}
		return new MergedList(inversions, output);
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\IntegerArray.txt";
		if (args.length > 0)
		filename = args[0];
		new InversionCounter(new File(filename));
	}
	
}

class MergedList {
	long inversions;
	List<Integer> list;
	
	MergedList(long i, List<Integer> l) {
		inversions = i;
		list = l;
	}
}
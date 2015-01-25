package hopshackle.algorithms;

import hopshackle.simulation.*;

import java.io.File;
import java.util.*;

public class TwoSum {
	
	SortedSet<Integer> distinctIntegers;

	public TwoSum(File file) {
		List<String> integersAsStrings = HopshackleUtilities.createListFromFile(file);
		List<Integer> integers = HopshackleUtilities.convertToIntegers(integersAsStrings);
		
		distinctIntegers = new TreeSet<Integer>(integers);
	}
	
	public boolean distinctSumsTo(int targetNumber) {
		for (int n : distinctIntegers.subSet(0, (targetNumber/2 +1))) {
			for (int m : distinctIntegers.subSet(targetNumber - n, targetNumber))  {
				if (n == m) continue;
				if (n + m == targetNumber) return true;
			}
		}
		return false;
	}
	
	public int distinctSumsInRange(int lowerBound, int upperBound) {
		int count = 0;
		for (int n = lowerBound; n <= upperBound; n++)
			if (distinctSumsTo(n)) count++;
		
		return count;
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\TwoSum.txt";
		if (args.length > 0)
			filename = args[0];
		TwoSum twosum = new TwoSum(new File(filename));
		System.out.println(twosum.distinctSumsTo(2500));
		System.out.println(twosum.distinctSumsTo(4000));
		System.out.println(twosum.distinctSumsInRange(2500, 4000));
		
	}
}

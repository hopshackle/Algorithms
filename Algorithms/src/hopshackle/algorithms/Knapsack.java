package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.*;

public class Knapsack {

	int numberOfItems;
	int totalBudget;
	Hashtable<Long, Long> cachedValues = new Hashtable<Long, Long>();
	List<KnapsackItem> allItems = new ArrayList<KnapsackItem>();
	long[][] results;

	public Knapsack(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String header = rawData.remove(0);
		StringTokenizer hd = new StringTokenizer(header);
		totalBudget = Integer.valueOf(hd.nextToken());
		numberOfItems = Integer.valueOf(hd.nextToken());

		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line);
			int value = Integer.valueOf(st.nextToken());
			int cost = Integer.valueOf(st.nextToken());
			allItems.add(new KnapsackItem(cost, value));
		}
		
		long output = getValueOf(numberOfItems, totalBudget);
		
		System.out.println("Answer is " + output);

//		results = new long[numberOfItems+1][totalBudget+1];

//		for (int i=1; i<totalBudget+1; i++) {
			// results[0][1..K] should already be initialised to 0
//			int firstItemValue = allItems.get(0).value;
//			int firstItemCost = allItems.get(0).cost;
//			if (i >= firstItemCost)
//				results[1][i] = firstItemValue;
//		}

//		long firstOption;
//		long secondOption;
//		for (int i=2; i<=numberOfItems; i++) {
//			KnapsackItem nextItem = allItems.get(i-1);
//			for (int v=1; v<=totalBudget; v++) {
//				if (nextItem.cost <= v)
//					firstOption = results[i-1][v-nextItem.cost] + nextItem.value;
//				else
//					firstOption = 0;
				
//				secondOption = results[i-1][v];
				
//				results[i][v] = Math.max(firstOption, secondOption);
//			}
//			System.out.println(String.format("Max value for %d items is %d", i, results[i][totalBudget]));
//		}
	}
	
	public long getValueOf(int items, int budget) {
		if (budget <= 0 || items <= 0) return 0;
		
		Long key = (((long)items * ((long)totalBudget +1)) + (long)budget);
		if (cachedValues.containsKey(key))
			return cachedValues.get(key);
		
		KnapsackItem nextItem = allItems.get(items-1);
		long firstOption = getValueOf(items - 1, budget - nextItem.cost) + nextItem.value;
		if (budget - nextItem.cost < 0) firstOption = 0;
		long secondOption = getValueOf(items - 1, budget);
		
		long actualValue = Math.max(firstOption, secondOption);
		cachedValues.put(key, actualValue);
		
//		System.out.println(String.format("Calculated Value for %d items and %d budget is %d (as maximum of %d and %d)", items, budget, actualValue, firstOption, secondOption));
		return actualValue;
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\knapsack.txt";
		if (args.length > 0)
			filename = args[0];
		new Knapsack(new File(filename));
	}
}

class KnapsackItem {

	int cost;
	int value;

	public KnapsackItem(int cost, int value) {
		this.cost = cost;
		this.value = value;
	}
}
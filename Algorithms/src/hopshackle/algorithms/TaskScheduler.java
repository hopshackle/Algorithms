package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.*;
import java.util.*;

public class TaskScheduler {

	private int numberOfJobs;
	private PriorityQueue<Job> jobQueue;
	
	public TaskScheduler(File file, Comparator<Job> jobComparator) {
		List<String> fileContents = HopshackleUtilities.createListFromFile(file);
		jobQueue = new PriorityQueue<Job>(1000, jobComparator);
	
		numberOfJobs = Integer.valueOf(fileContents.get(0));
		for (int jobIndex = 1; jobIndex <= numberOfJobs; jobIndex++) {
			String line = fileContents.get(jobIndex);
			String[] entriesStr = line.split(" ");
			Job j = new Job(Integer.valueOf(entriesStr[0]), Integer.valueOf(entriesStr[1]));
			jobQueue.add(j);
		}
		
		int totalTime = 0;
		long sumWeightedCompletionTimes = 0;
	
		Job nextJob = jobQueue.poll();
		do {
			totalTime += nextJob.length;
			sumWeightedCompletionTimes += nextJob.weight * totalTime;
			nextJob = jobQueue.poll();
		} while (nextJob != null);
		
		System.out.println(String.format("Total time: %d, and Weighted Sum: %d", totalTime, sumWeightedCompletionTimes));
	}

	public static void main(String[] args) {
		String filename = "C:\\Users\\James\\Downloads\\JobList.txt";
		if (args.length > 0)
		filename = args[0];
		
		Comparator<Job> jobComparator = new Comparator<Job>() {

			@Override
			public int compare(Job j1, Job j2) {
				// return j2 - j1 (if the first argument should go first in a sorted list, then return a negative number)
				int one = j1.weight - j1.length;
				int two = j2.weight - j2.length;
				if (one == two) {
					one = j1.weight;
					two = j2.weight;
				}
				
				return two - one;
				
		//		double one = (double)j1.weight / (double)j1.length;
		//		double two = (double)j2.weight / (double)j2.length;
				
		//		if (one > two) return -1;
		//		if (two > one) return 1;
		//		return 0;
			}
		};
		
		new TaskScheduler(new File(filename), jobComparator);
		
		System.out.println("Finished");
	}
}

class Job {
	
	int weight;
	int length;
	
	public Job(int weight, int length) {
		this.weight = weight;
		this.length = length;
	}
}

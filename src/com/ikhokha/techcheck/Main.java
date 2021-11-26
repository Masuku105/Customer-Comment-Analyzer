package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class Main {

	public static void main(String[] args) {
		
		try {
			// get files from docs folder
			File[] files = new File("docs").listFiles((file, s) -> s.endsWith(".txt"));			
			int nThread = files.length < 10 ? files.length : files.length / 4;
			// create thread pool. nThread = files.length or less
			ExecutorService executor = Executors.newFixedThreadPool(nThread);
			// create stack of future to store it's file analysis report
			Stack<Future<CommentReport>> futures = new Stack<>();
			ArrayList<CommentReport> reports = new ArrayList<>();
			// iterate over file, analysing each
			for (File file : files) {
				var c = new CommentAnalyzer(file);
				futures.push(executor.submit(c.analyze));
			}
			// while the stack has results to be processed
			while (!futures.empty()) {
				// get/pop comment future
				var result = futures.pop();
				// get the future, so that result.isDone() is true
				var comment = result.get();
				// add comment to report
				reports.add(comment);
			}
			executor.shutdown();
			printReportResult(reports);
		} catch (ExecutionException | InterruptedException e) {
			System.out.println("Thread execution error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	private static void printReportResult(ArrayList<CommentReport> reports) {
		// print out comment report for current file
		// consolidate results
		CommentReport r = new CommentReport();
		for (int i = 0; i < reports.size(); i++) {
			r.Spam += reports.get(i).Spam;
			r.Questions += reports.get(i).Questions;
			r.Movers += reports.get(i).Movers;
			r.Shakers += reports.get(i).Shakers;
			r.Shorter += reports.get(i).Shorter;
		}
		System.out.println("===================================");

		System.out.printf("SPAM: %s%n", r.Spam);
		System.out.printf("QUESTIONS: %s%n", r.Questions);
		System.out.printf("MOVERS_MENTIONS: %s%n", r.Movers);
		System.out.printf("SHAKERS_MENTIONS: %s%n", r.Shakers);
		System.out.printf("SHORTER_THAN_15: %s%n", r.Shorter);

		System.out.println("===================================");
	}

}

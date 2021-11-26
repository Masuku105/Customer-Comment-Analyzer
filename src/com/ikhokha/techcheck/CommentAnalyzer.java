package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommentAnalyzer {
	private File _file;
	public CommentAnalyzer(File file) {
		_file = file;
	}
	public Callable<CommentReport> analyze = ()  -> {
		CommentReport report = new CommentReport();
		try {
		
			var reader = new BufferedReader(new FileReader(_file));		
			var lines = reader.lines().collect(Collectors.toList());

			// filter the information needed
			long movers = lines.stream().filter(s -> s.toLowerCase().contains("mover")).count();
			long shakers = lines.stream().filter(s -> s.toLowerCase().contains("shaker")).count();
			long shortComments = lines.stream().filter(s -> s.length() < 15).count();
			long questions = lines.stream().filter(s -> s.contains("?")).count();

			// get all urls and assign to spam count
			var rgx = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)", Pattern.CASE_INSENSITIVE);
			long spam = lines.stream().filter(s -> rgx.matcher(s).find()).count();

			// add results to report
			report.Shakers = shakers;
			report.Movers = movers;
			report.Shorter = shortComments;
			report.Questions = questions;
			report.Spam = spam;
			System.out.println("File processed: " + _file.getName());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return report;
	};

}

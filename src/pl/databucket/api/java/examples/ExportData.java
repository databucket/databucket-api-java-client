package pl.databucket.api.java.examples;

import com.opencsv.CSVWriter;
import pl.databucket.api.java.client.*;
import pl.databucket.api.java.examples.structure.Bucket;
import pl.databucket.api.java.examples.structure.User;
import pl.databucket.api.java.examples.structure.Tag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportData {
	
	Databucket databucket;
	
	public static void main(String[] args) {
		ExportData exportData = new ExportData();
		exportData.exportData("./resources/export/users.csv");
	}
	
	public ExportData() {
		databucket = new Databucket("http://localhost:8080/api", false);
	}

	
	private void exportData(String filePath) {
		File file = new File(filePath); 
	    try { 
	        // create FileWriter object with file as parameter 
	        FileWriter outputFile = new FileWriter(file); 
	  
	        // create CSVWriter object filewriter object as parameter 
	        CSVWriter writer = new CSVWriter(outputFile); 
	  
	        // adding header to csv 
	        String[] header = { "ID", "Tag", "First name", "Last name", "City", "Email" }; 
	        writer.writeNext(header); 
	        
	        // Get all bundles meets the given conditions
	        Condition[] conditions = {
					new Condition(Source.FIELD, Field.TAG_NAME, Operator.equal, Source.CONST, Tag.ACTIVE),
					new Condition(Source.PROPERTY, User.CITY, Operator.like, Source.CONST, "%Rock%")
					};
	        
	        List<Bundle> bundles = databucket.getBundles(Bucket.USERS, conditions);
	        	        
	        for (Bundle bundle : bundles) {
				String[] row = {
						String.valueOf(bundle.getBundleId()),
						bundle.getTagName(),
						(String) bundle.getProperty(User.FIRST_NAME),
						(String) bundle.getProperty(User.LAST_NAME),
						(String) bundle.getProperty(User.CITY),
						(String) bundle.getProperty(User.EMAIL)
						};
				writer.writeNext(row);
			}
	  
	        // closing writer connection and file
	        writer.close(); 
	        outputFile.close();
	        
	        System.out.println("Done.");
	        
	    } catch (IOException e) { 
	        e.printStackTrace(); 
	    }
	}

}

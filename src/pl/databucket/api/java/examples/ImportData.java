package pl.databucket.api.java.examples;

import com.opencsv.CSVReader;
import pl.databucket.api.java.client.Bundle;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.structure.Bucket;
import pl.databucket.api.java.examples.structure.User;
import pl.databucket.api.java.examples.structure.Tag;

import java.io.FileReader;

public class ImportData {
	
	Databucket databucket;
	
	public static void main(String[] args) {
		ImportData importData = new ImportData();
		importData.importData("./resources/import/users.csv");
	}
	
	public ImportData() {
		databucket = new Databucket("http://localhost:8080/api", false);
	}
	
	private void importData(String filePath) {
		try { 
	        FileReader filereader = new FileReader(filePath); 
	        CSVReader csvReader = new CSVReader(filereader); 
	        String[] nextRecord; 
	  
	        // skip first row with column names
	        csvReader.readNext();
	        
	        // we are going to read data line by line 
	        while ((nextRecord = csvReader.readNext()) != null) {
	        	Bundle bundle = new Bundle(Bucket.USERS);
				bundle.setTagName(Tag.ACTIVE);
				bundle.setProperty(User.FIRST_NAME, nextRecord[0]);
				bundle.setProperty(User.LAST_NAME, nextRecord[1]);
				bundle.setProperty(User.COMPANY, nextRecord[2]);
				bundle.setProperty(User.ADDRESS, nextRecord[3]);
				bundle.setProperty(User.CITY, nextRecord[4]);
				bundle.setProperty(User.STATE, nextRecord[5]);
				bundle.setProperty(User.POST, nextRecord[6]);
				bundle.setProperty(User.PHONE, nextRecord[7]);
				bundle.setProperty(User.EMAIL, nextRecord[8]);
				bundle.setProperty(User.WEB, nextRecord[9]);
				
				// insert the bundle into Databucket
				databucket.insertBundle(bundle);
	        } 
	        
	        csvReader.close();
	        
	        System.out.println("Done.");
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	}
}

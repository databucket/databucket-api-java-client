package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.Bundle;
import pl.databucket.api.java.client.Databucket;
import pl.databucket.api.java.examples.structure.Bucket;
import pl.databucket.api.java.examples.structure.User;
import pl.databucket.api.java.examples.structure.Tag;

public class InsertBundle {

	Databucket databucket;
	
	public static void main(String[] args) {
		InsertBundle insertBundle = new InsertBundle();
		insertBundle.insertBundle();
	}
	
	public InsertBundle() {
		databucket = new Databucket("http://localhost:8080/api", false);
	}
	
	private void insertBundle() {
		Bundle bundle = new Bundle(Bucket.USERS);
		bundle.setTagName(Tag.ACTIVE);
		bundle.setProperty(User.FIRST_NAME, "John");
		bundle.setProperty(User.LAST_NAME, "Brown");
		bundle.setProperty(User.ADDRESS, "123 Avenue");
		bundle.setProperty(User.COMPANY, "Google");
		bundle.setProperty(User.EMAIL, "jb@gmail.com");
		bundle.setProperty(User.PHONE, "0965-223-212");
		bundle.setProperty(User.WEB, "https://www.jbrown.com");

		bundle = databucket.insertBundle(bundle);
		
		System.out.println(bundle.toString());	
	}
	
}

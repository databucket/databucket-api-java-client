package pl.databucket.api.java.examples;

import pl.databucket.api.java.client.*;
import pl.databucket.api.java.examples.structure.Bucket;
import pl.databucket.api.java.examples.structure.User;
import pl.databucket.api.java.examples.structure.Tag;

public class ModifyBundle {
	
	Databucket databucket;
	
	public static void main(String[] args) {
		ModifyBundle modifyBundle = new ModifyBundle();
		modifyBundle.modifyBundle();
	}
	
	public ModifyBundle() {
		databucket = new Databucket("http://localhost:8080/api", false);
	}
	
	private void modifyBundle() {
		Condition[] conditions = {
				new Condition(Source.FIELD, Field.TAG_NAME, Operator.equal, Source.CONST, Tag.ACTIVE),
				new Condition(Source.PROPERTY, User.CITY, Operator.like, Source.CONST, "%Rock%")
				};
		// Get and lock one bundle for given conditions
		Bundle bundle = databucket.lockBundle(Bucket.USERS, conditions, true);
		
		// Get one bundle for given conditions
//		Bundle bundle = databucket.getBundle(Bucket.USERS, conditions, true);
		
		bundle.setTagName(Tag.TRASH);
		bundle.setLocked(false);
		
		bundle = databucket.updateBundle(bundle);
		
		System.out.println(bundle.toString());	
	}
}

package pl.databucket.api.java.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Bundle {
	
	private int bundleId;
	private String bucketName;
	private Boolean locked;
	private String lockedBy;
	private String tagName;
	private Map<String, Object> properties;
	private Date createdAt;
	private String createdBy;
	private Date updatedAt;
	private String updatedBy;
	
	public Bundle(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public Bundle(String bucketName, int bundleId, String tagName, Boolean locked, String lockedBy, Map<String, Object> properties, Date createdAt, String createdBy, Date updatedAt, String updatedBy) {
		this.bucketName = bucketName;
		this.bundleId = bundleId;
		this.tagName = tagName;
		this.locked = locked;
		this.lockedBy = lockedBy;
		this.properties = properties;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
	}
	
	public String getBucketName() {
		return bucketName;
	}
	
	public int getBundleId() {
		return bundleId;
	}

	public Boolean getLocked() {
		return locked;
	}
	
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	public String getLockedBy() {
		return lockedBy;
	}

	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public Map<String, Object> getProperties() {
		return this.properties;
	}
	
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public void deleteProperty(String propertyPath) {
		if (this.properties != null) {
			removeValueByPath(this.properties, propertyPath);
		}
	}
	
	public void setProperty(String propertyPath, Object value) {
		if (properties == null)
			properties = new HashMap<String, Object>();
		
		setValueByPath(this.properties, propertyPath, value);
	}
	
	public Object getProperty(String propertyPath) {
		if (this.properties == null)
			return null;
		else
			return getValueByPath(this.properties, propertyPath);
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}	
	
	@Override
	public String toString() {
		String bundleStr = "";
		bundleStr += "bucketName: " + this.getBucketName() + "\n";
		bundleStr += "bundleId: " + this.getBundleId() + "\n";
		bundleStr += "tagName: " + this.getTagName() + "\n";
		bundleStr += "locked: " + this.getLocked() + "\n";
		bundleStr += "lockedBy: " + this.getLockedBy() + "\n";
		bundleStr += "createdAt: " + this.getCreatedAt() + "\n";
		bundleStr += "createdBy: " + this.getCreatedBy() + "\n";
		bundleStr += "updatedAt: " + this.getUpdatedAt() + "\n";
		bundleStr += "updatedBy: " + this.getUpdatedBy() + "\n";
		bundleStr += "properties: " + this.getProperties().toString();
		return bundleStr;
	}
	
	@SuppressWarnings("unchecked")
	private Object getValueByPath(Object source, String path) {
		if (path.contains(".")) {
			int pos = path.indexOf(".");
			String name = path.substring(0, pos);
			String restPath = path.substring(pos + 1);
			if (source instanceof Map && ((Map<String, Object>) source).containsKey(name))
				return getValueByPath(((Map<String, Object>) source).get(name), restPath);
			else
				return null;
		} else
			if (source instanceof Map && ((Map<String, Object>) source).containsKey(path))
				return ((Map<String, Object>) source).get(path);
			else
				return null;
				
	}
	
	@SuppressWarnings("unchecked")
	private void setValueByPath(Object source, String path, Object value) {
		if (path.contains(".")) {
			int pos = path.indexOf(".");
			String name = path.substring(0, pos);
			String restPath = path.substring(pos + 1);
			if (source instanceof Map && !((Map<String, Object>) source).containsKey(name))
				((Map<String, Object>) source).put(name, new HashMap<String, Object>());
			setValueByPath(((Map<String, Object>) source).get(name), restPath, value);			
		} else 
			((Map<String, Object>) source).put(path, value);
	}
	
	@SuppressWarnings("unchecked")
	private void removeValueByPath(Object source, String path) {
		if (path.contains(".")) {
			int pos = path.indexOf(".");
			String name = path.substring(0, pos);
			String restPath = path.substring(pos + 1);
			if (source instanceof Map && ((Map<String, Object>) source).containsKey(name))
				removeValueByPath(((Map<String, Object>) source).get(name), restPath);
		} else
			if (source instanceof Map && ((Map<String, Object>) source).containsKey(path))
				((Map<String, Object>) source).remove(path);

	}
}

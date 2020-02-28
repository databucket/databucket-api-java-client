package pl.databucket.api.java.client;

import java.net.Proxy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Databucket {
	
	private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private String serviceUrl;
	private String userName = System.getProperty("user.name");
	private Gson gson;
	private Client client = null;
	private Map<String, Object> httpHeaders = new HashMap<String, Object>();
	
	/**
	 * Constructor
	 * @param serviceUrl - Databucket api endpoint URL, e.g. <i>http://localhost:8080/api</i>
	 * @param logs - extended http logs (helpful for debugging)<ul><li><b>true</b> - enable logs</li><li><b>false</b> - disable logs</li></ul>
	 */
	public Databucket(String serviceUrl, boolean logs) {
		this.serviceUrl = serviceUrl;
		client = Client.create();
		if (logs)
			client.addFilter(new LoggingFilter(System.out));
		gson = new GsonBuilder().disableHtmlEscaping().create();
	}
	
	/**
	 * Constructor
	 * @param serviceUrl - Databucket api endpoint URL, e.g. <i>http://localhost:8080/api</i>
	 * @param proxy - proxy configuration, e.g. {@code new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));}
	 * @param logs - extended http logs (helpful for debugging)<ul><li><b>true</b> - enable logs</li><li><b>false</b> - disable logs</li></ul>
	 */
	public Databucket(String serviceUrl, Proxy proxy, boolean logs) {
		this.serviceUrl = serviceUrl;
		
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.initializeProxy(proxy);
		URLConnectionClientHandler clientHandler = new URLConnectionClientHandler(connectionFactory);
		client = new Client(clientHandler);
		if (logs)
			client.addFilter(new LoggingFilter(System.out));
				
		gson = new GsonBuilder().disableHtmlEscaping().create();
	}
	
	/**
	 * Saves a custom user name. By default, a system user name is taken.
	 * @param userName - user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Adds custom HTTP header
	 * @param name - header item name
	 * @param value - header item value
	 */
	public void addHeader(String name, Object value) {
		httpHeaders.put(name, value);
	}
	
	/**
	 * Builds headers configuration before send request
	 * @param builder - a map of headers
	 */
	private void setHeaders(Builder builder) {
		for (Map.Entry<String, Object> entry : httpHeaders.entrySet())  
			builder = builder.header(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Inserts a new bundle
	 * @param bundle - a <b>Bundle</b> object
	 * @return a <b>Bundle</b> object supplemented by fields configured during creation
	 */
	public Bundle insertBundle(Bundle bundle) {
		String resource = String.format("/buckets/%s/bundles", bundle.getBucketName());
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("userName", userName);
        
		Map<String, Object> json = new HashMap<String, Object>();
		
		if (bundle.getLocked() != null)
			json.put("locked", bundle.getLocked());
		
		if (bundle.getTagName() != null)
			json.put("tag_name", bundle.getTagName());
		
		if (bundle.getProperties() != null) 
			json.put("properties", gson.toJson(bundle.getProperties(), Map.class));
		
		String payload = gson.toJson(json);
		
		WebResource webResource = client.resource(serviceUrl + resource);
		webResource = webResource.queryParams(queryParams);
		Builder builder = webResource.type(MediaType.APPLICATION_JSON);
		setHeaders(builder);
		
		ClientResponse response = builder.post(ClientResponse.class, payload);
		String responseBody = response.getEntity(String.class);
				
        if (response.getStatus() == 201) {
        	@SuppressWarnings("unchecked")
			Map<String, Object> result = gson.fromJson(responseBody, Map.class);
        	try {
        		return getBundle(bundle.getBucketName(), ((Double) result.get(ResponseField.BUNDLE_ID)).intValue());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        } else 
        	throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
	}
	
	
	/**
	 * Searches for one bundle in the bucket with the given id
	 * @param bucketName - bucket name
	 * @param bundleId - bundle id
	 * @return a <b>Bundle</b> object if any bundle with given id exists or <b>null</b>
	 */
	@SuppressWarnings("unchecked")
	public Bundle getBundle(String bucketName, int bundleId) {
		String resource = String.format("/buckets/%s/bundles/%d", bucketName, bundleId);

		WebResource webResource = client.resource(serviceUrl + resource);
		Builder builder = webResource.type(MediaType.APPLICATION_JSON);
		setHeaders(builder);
		
		ClientResponse response = builder.get(ClientResponse.class);
		String responseBody = response.getEntity(String.class);
		
		if (response.getStatus() == 200) {
			Map<String, Object> result = gson.fromJson(responseBody, Map.class);
        	try {
				List<Map<String, Object>> bundles = (List<Map<String, Object>>) result.get(ResponseField.BUNDLES);
        		if (bundles.size() > 0) {
        			return jsonToBundle(bucketName, bundles.get(0));
        		} else 
        			return null;
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        } else 
        	throw new RuntimeException("Response status: " + response.getStatus());
	}
	
	/**
	 * Searches for first one bundle in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @return a <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	public Bundle getBundle(String bucketName, Condition[] conditions) {
		return getBundle(bucketName, conditions, false);
	}
	
	
	/**
	 * Searches for one random bundle in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @param random <ul><li><b>true</b> - search random bundles</li><li><b>false</b> - search first n bundles</li></ul>
	 * @return a <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	public Bundle getBundle(String bucketName, Condition[] conditions, boolean random) {
		List<Bundle> bundles = getBundles(bucketName, conditions, random, 1);
		if (bundles.size() > 0)
			return bundles.get(0);
		else
			return null;
	}
	
	/**
	 * Searches for all bundles in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @return a list of <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	public List<Bundle> getBundles(String bucketName, Condition[] conditions) {
		return getBundles(bucketName, conditions, false, null);
	}
	
	
	/**
	 * Searches for bundles in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @param random <ul><li><b>true</b> - search random bundles</li><li><b>false</b> - search first n bundles</li></ul>
	 * @param count - number of searched bundles
	 * @return a list of <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	@SuppressWarnings("unchecked")
	public List<Bundle> getBundles(String bucketName, Condition[] conditions, boolean random, Integer count) {
		String resource = String.format("/buckets/%s/bundles/custom", bucketName);
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		if (count != null)
			queryParams.add("limit", count.toString());		
		if (random)
			queryParams.add("sort", "rand()");
		
		WebResource webResource = client.resource(serviceUrl + resource);
		webResource = webResource.queryParams(queryParams);
		Builder builder = webResource.type(MediaType.APPLICATION_JSON);
		setHeaders(builder);
		
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("conditions", conditions);
		
		String payload = gson.toJson(json);
		
		ClientResponse response = builder.post(ClientResponse.class, payload);
		String responseBody = response.getEntity(String.class);
		
		if (response.getStatus() == 200) {
			Map<String, Object> result = gson.fromJson(responseBody, Map.class);
        	try {
				List<Map<String, Object>> jsonBundles = (List<Map<String, Object>>) result.get(ResponseField.BUNDLES);
        		if (jsonBundles.size() > 0) {
        			List<Bundle> bundles = new ArrayList<Bundle>();
        			for (Map<String, Object>  jsonBundle: jsonBundles) {
						Bundle bundle = jsonToBundle(bucketName, jsonBundle);
						bundles.add(bundle);
					}
        			return bundles;
        		} else
        			return null;
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        } else 
        	throw new RuntimeException("Response status: " + response.getStatus());
	}
	
	/**
	 * Searches and getting locked for one random bundle in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @param random <ul><li><b>true</b> - search random bundles</li><li><b>false</b> - search first n bundles</li></ul>
	 * @return a <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	public Bundle lockBundle(String bucketName, Condition[] conditions, boolean random) {
		List<Bundle> bundles = lockBundles(bucketName, conditions, random, 1);
		if (bundles.size() > 0)
			return bundles.get(0);
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Searches and getting locked for bundles in the bucket with the given name
	 * @param bucketName - bucket name
	 * @param conditions - an array of conditions
	 * @param random <ul><li><b>true</b> - search random bundles</li><li><b>false</b> - search first n bundles</li></ul>
	 * @param count - number of searched bundles
	 * @return a list of <b>Bundle</b> object if any bundle meets the given conditions or <b>null</b>
	 */
	public List<Bundle> lockBundles(String bucketName, Condition[] conditions, boolean random, Integer count) {
		String resource = String.format("/buckets/%s/bundles/custom/lock", bucketName);

		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("userName", userName);
		if (count != null)
			queryParams.add("limit", count.toString());		
		if (random)
			queryParams.add("sort", "rand()");
		
		WebResource webResource = client.resource(serviceUrl + resource);
		webResource = webResource.queryParams(queryParams);
		Builder builder = webResource.type(MediaType.APPLICATION_JSON);
		setHeaders(builder);
		
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("conditions", conditions);
		
		String payload = gson.toJson(json);
		
		ClientResponse response = builder.post(ClientResponse.class, payload);
		String responseBody = response.getEntity(String.class);
		
		if (response.getStatus() == 200) {
			Map<String, Object> result = gson.fromJson(responseBody, Map.class);
        	try {
				List<Map<String, Object>> jsonBundles = (List<Map<String, Object>>) result.get(ResponseField.BUNDLES);
				if (jsonBundles.size() > 0) {
        			List<Bundle> bundles = new ArrayList<Bundle>();
        			for (Map<String, Object>  jsonBundle: jsonBundles) {
						Bundle bundle = jsonToBundle(bucketName, jsonBundle);
						bundles.add(bundle);
					}
        			return bundles;
        		} else
        			return null;
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        } else 
        	throw new RuntimeException("Response status: " + response.getStatus());
	}
	
	/**
	 * Updates the bucket details
	 * @param bundle - a <b>Bundle</b> object to update 
	 * @return updated <b>Bundle</b> object
	 */
	public Bundle updateBundle(Bundle bundle) {
		String resource = String.format("/buckets/%s/bundles/%d", bundle.getBucketName(), bundle.getBundleId());
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("userName", userName);
        
		Map<String, Object> json = new HashMap<String, Object>();
		
		if (bundle.getLocked() != null)
			json.put("locked", bundle.getLocked());
		
		if (bundle.getTagName() != null)
			json.put("tag_name", bundle.getTagName());
		
		if (bundle.getProperties() != null) 
			json.put("properties", gson.toJson(bundle.getProperties(), Map.class));
		
		String payload = gson.toJson(json);
		
		WebResource webResource = client.resource(serviceUrl + resource);
		webResource = webResource.queryParams(queryParams);
		Builder builder = webResource.type(MediaType.APPLICATION_JSON);
		setHeaders(builder);
		
		ClientResponse response = builder.put(ClientResponse.class, payload);
		String responseBody = response.getEntity(String.class);
				
        if (response.getStatus() == 200) {
        	try {
        		return getBundle(bundle.getBucketName(), bundle.getBundleId());
        	} catch (Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        } else 
        	throw new RuntimeException("Response status: " + response.getStatus() + "\n\n" + responseBody);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Convert a map into a <b>Bundle</b> object
	 * @param bucketName
	 * @param jsonObj
	 * @return
	 */
	private Bundle jsonToBundle(String bucketName, Map<String, Object> jsonObj) {
		int bundleId = -1;
		String tagName = null;
		Boolean locked = null;
		String lockedBy = null;
		Map<String, Object> properties = null;
		String createdBy = null;
		Date createdAt = null;
		String updatedBy = null;
		Date updatedAt = null;
		
		if (jsonObj.containsKey(Field.BUNDLE_ID))
			bundleId = ((Double) jsonObj.get(Field.BUNDLE_ID)).intValue();
		
		if (jsonObj.containsKey(Field.TAG_NAME))
			tagName = (String) jsonObj.get(Field.TAG_NAME);
			
		if (jsonObj.containsKey(Field.LOCKED))
			locked = (Boolean) jsonObj.get(Field.LOCKED);
		
		if (jsonObj.containsKey(Field.LOCKED_BY))
			lockedBy = ((String) jsonObj.get(Field.LOCKED_BY));
		
		if (jsonObj.containsKey(Field.PROPERTIES))
			properties = (Map<String, Object>) jsonObj.get(Field.PROPERTIES);

		if (jsonObj.containsKey(Field.CREATED_BY))
			createdBy = (String) jsonObj.get(Field.CREATED_BY);
		
		if (jsonObj.containsKey(Field.CREATED_AT)) {
			String dateTimeStr = (String) jsonObj.get(Field.CREATED_AT);
			DateFormat format = new SimpleDateFormat(DATE_FORMAT);
			try {
				createdAt = format.parse(dateTimeStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (jsonObj.containsKey(Field.UPDATED_BY))
			updatedBy = (String) jsonObj.get(Field.UPDATED_BY);
		
		if (jsonObj.containsKey(Field.UPDATED_AT)) {
			Object obj = jsonObj.get(Field.UPDATED_AT);
			if (obj != null) {
				String dateTimeStr = (String) jsonObj.get(Field.UPDATED_AT);
				DateFormat format = new SimpleDateFormat(DATE_FORMAT);
				try {
					updatedAt = format.parse(dateTimeStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return new Bundle(bucketName, bundleId, tagName, locked, lockedBy, properties, createdAt, createdBy, updatedAt, updatedBy);		
	}

}

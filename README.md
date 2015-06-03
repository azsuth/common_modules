# Common Android Studio Modules

This project contains several common Android "libraries" that can be imported into other Android Studio projects as modules.  All modifications/additions should be done in this project and then re-imported into others using this code.

# Modules
## appcache
- A global singleton app cache
- Uses enum singleton pattern for cleanliness, 
- Stores a map of String keys to Object values
- Two ways of retrieving objects:
	- Casting
		- get(String key)
		- code must cast expected object
		- not type safe; many Unchecked Cast warnings and potential for RuntimeExceptions
	- TypeReference
		- get(String key, TypeReference<T> typeReference)
		- AppCache checks if the retrieved object matches the expected in TypeReference
		- type safe; if retrieved does not match expected, returns null
- Support for different types of caches by adding additional enum cases, i.e. AppCache.NETWORK, AppCache.USER_INPUT, AppCache.TEST_INSTANCE
- Common built in TypeReferences, i.e. int, boolean, String, etc...
- TypeReference supports no-arg constructors, n-arg constructors and generic types
- Unit tests and benchmarks in AppCacheTests
- IMPORTANT NOTE: currently does not support array types, use ArrayList instead
- Usage:

```java
AppCache.INSTANCE.put("APPCACHE_INT_KEY", 42);
Integer cacheInt = AppCache.INSTANCE.get("APPCACHE_INT_KEY", AppCache.INTEGER_TYPE_REFERENCE);

AppCache.INSTANCE.put("APPCACHE_ARRAY_KEY", new ArrayList<String>());
ArrayList<String> cacheArray = AppCache.INSTANCE.get("APPCACHE_ARRAY_KEY",
	new AppCache.TypeReference<ArrayList<String>>() {});
	
AppCache.INSTANCE.removeAll();
```

## volleyjacksonrequest
- Wrapper for Volley and Jackson
- Set global default ObjectMapper with setDefaultObjectMapper(ObjectMapper defaultMapper)
- Set global default RetryPolicy with setDefaultRetryPolicy(RetryPolicy retryPolicy)
- Start a request with static VJRequest.request, VJRequest.get or VJRequest.post
	- Returns a RequestBuilder<T> or an intermidiate class to guide necessary parameters
	- request method, url, error listener and response type are required
	- defaults for unset options:
		- requestBody: null
		- mapper: VJRequest.getDefaultMapper()
		- retryPolicy: VJRequest.getDefaultRetryPolicy()
		- successListener: empty
		- headers: empty
	- RequestBuilder.build() will construct and return a VJRequest
	- RequestBuilder.execute(RequestQueue requestQueue) will build the VJRequest and add it to the specified RequestQueue
- Usage:

```java
VJRequest.get(new TypeReference<MyDate>() {})
	.from(MY_DATE_JSON_ENDPOINT)
	.success(new Response.Listener<MyDate>() {
		
		@Override
		public void onResponse(MyDate response) {
			Toast.makeText(context, response.displayValue, Toast.LENGTH_SHORT).show();
		}
		
	})
	.failure(new Response.ErrorListener() {
	
		@Override
		public void onErrorResponse(VolleyError error) {
			Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
	}).execute(myRequestQueue);
```

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

## volleyjacksonrequest
- Wrapper for Volley and Jackson
- Set global default ObjectMapper with setDefaultObjectMapper(ObjectMapper defaultMapper)
- Set global default RetryPolicy with setDefaultRetryPolicy(RetryPolicy retryPolicy)
- Start a request with static VJRequest.request(TypeReference<T> responseType)
	- Returns a RequestBuilder<T>
	- from(String url) and failure(Response.ErrorListener errorListener) are required
	- defaults for unset options:
		- method: Request.Method.GET
		- requestBody: null
		- mapper: VJRequest.getDefaultMapper()
		- retryPolicy: VJRequest.getDefaultRetryPolicy()
		- successListener: empty
	- RequestBuilder.build() will construct and return a VJRequest
	- RequestBuilder.execute(RequestQueue requestQueue) will build the VJRequest and add it to the specified RequestQueue

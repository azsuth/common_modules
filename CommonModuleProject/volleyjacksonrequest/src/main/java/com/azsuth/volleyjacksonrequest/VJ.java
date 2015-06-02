package com.azsuth.volleyjacksonrequest;

/**
 * Created by andrewsutherland on 5/8/15.
 */

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VJ<T> extends JsonRequest<T> {

    /**
     * Set a default mapper for all jackson mapping.
     *
     * If this method is called with a non-null ObjectMapper, all future
     * requests that do not specify a custom mapper will use the configured
     * default mapper.
     *
     * @param defaultMapper ObjectMapper to set as the default
     */
    public static void setDefaultObjectMapper(ObjectMapper defaultMapper) {
        VJ.defaultMapper = defaultMapper;
    }

    /**
     * Set a default retry policy for all requests.
     *
     * If this method is called with a non-null RetryPolicy, all future
     * requests that do not specify a custom policy will use the
     * configured default policy.
     *
     * @param defualtRetryPolicy RetryPolicy to set as the default
     */
    public static void setDefaultRetryPolicy(RetryPolicy defualtRetryPolicy) {
        VJ.retryPolicy = defualtRetryPolicy;
    }

    /**
     * Singular mapper associated with all requests that do not specify a custom
     * ObjectMapper.
     */
    private static ObjectMapper defaultMapper;

    /**
     * Singular RetryPolicy associated with all requests that do not specify
     * a custom RetryPolicy.
     */
    private static RetryPolicy retryPolicy;

    /**
     * Returns the default ObjectMapper.
     *
     * If a default mapper doesn't already exist, create a new one with no
     * special configurations.
     *
     * @return ObjectMapper the default ObjectMapper
     */
    private static synchronized ObjectMapper getDefaultMapper() {
        if (defaultMapper == null) {
            defaultMapper = new ObjectMapper();
        }
        return defaultMapper;
    }

    /**
     * Returns the default RetryPolicy.
     *
     * If a default policy doesn't already exist, use the default
     * Volley retry policy.
     *
     * @return RetryPolicy the default RetryPolicy
     */
    private static synchronized RetryPolicy getDefaultRetryPolicy() {
        if (retryPolicy == null) {
            retryPolicy = new DefaultRetryPolicy();
        }
        return retryPolicy;
    }

    private TypeReference<T> responseType;
    private ObjectMapper mapper;

    /**
     * Creates a new VJRequest.  Can only be created by using a RequestCreator.
     *
     * @param method int one of com.android.volley.Request.Method ints
     * @param url
     * @param requestBody any post parameters
     * @param responseType TypeReference<T> anonymous class that wraps the object that the response will be mapped to
     * @param mapper ObjectMapper custom mapper to be used instead of the default
     * @param successListener Response.Listener<T> to be called on successful network request and object mapping
     * @param errorListener Response.ErrorListener to be called on network error or object mapping error
     */
    private VJ(int method, String url, JSONObject requestBody, TypeReference<T> responseType, ObjectMapper mapper, RetryPolicy retryPolicy, Response.Listener<T> successListener, Response.ErrorListener errorListener) {
        super(method, url, (requestBody == null ? null : requestBody.toString()), successListener, errorListener);

        this.responseType = responseType;
        this.mapper = mapper;
        setRetryPolicy(retryPolicy);
    }

    /**
     * Parses a network response and maps it to the specified object.
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T mappedResponse = mapper.readValue(jsonString, responseType);
            return Response.success(mappedResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Starts a request.
     *
     * @param responseType wrapper for the expected response type
     * @param <T> expected response type
     *
     * @return a RequestBuilder to configure request options
     */
    public static <T> RequestBuilder<T> request(TypeReference<T> responseType) {
        return new RequestBuilder<>(responseType);
    }

    /**
     * Builder for a request.
     *
     * Required:
     * url
     * errorListener
     *
     * Defaults:
     * method - Request.Method.GET
     * requestBody - null
     * mapper - default ObjectMapper from VJRequest.getDefaultMapper()
     * retryPolicy - default RetryPolicy from VJRequest.getDefaultRetryPolicy()
     * successListener - empty listener
     *
     * @param <T> expected response type
     */
    public static class RequestBuilder<T> {
        private Integer method;
        private String url;
        private TypeReference<T> responseType;
        private JSONObject requestBody;
        private HashMap<String, String> headers;
        private ObjectMapper mapper;
        private RetryPolicy retryPolicy;
        private Response.Listener<T> successListener;
        private Response.ErrorListener errorListener;

        private RequestBuilder(TypeReference<T> responseType) {
            this.responseType = responseType;

            headers = new HashMap<>();
        }

        public RequestBuilder<T> from(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder<T> withRequestMethod(int method) {
            this.method = method;
            return this;
        }

        public RequestBuilder<T> withRequestBody(JSONObject requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public RequestBuilder<T> withHeader(String headerKey, String headerValue) {
            headers.put(headerKey, headerValue);
            return this;
        }

        public RequestBuilder<T> withHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public RequestBuilder<T> withObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public RequestBuilder<T> withRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public RequestBuilder<T> withSuccess(Response.Listener<T> successListener) {
            this.successListener = successListener;
            return this;
        }

        public RequestBuilder<T> withFailure(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        /**
         * Builds a VJRequest.
         *
         * Checks required parameters and assigns any null default parameters.
         *
         * @return a VJRequest
         */
        public VJ<T> build() {
            if (url == null) {
                throw new IllegalArgumentException("Url is required to build a request...duh");
            }

            if (errorListener == null) {
                throw new IllegalArgumentException("Error listener is required to build a request");
            }

            if (method == null) {
                method = Method.GET;
            }

            if (successListener == null) {
                successListener = new Response.Listener<T>() {

                    @Override
                    public void onResponse(T response) {
                        // don't care about response.
                    }

                };
            }

            if (mapper == null) {
                mapper = getDefaultMapper();
            }

            if (retryPolicy == null) {
                retryPolicy = getDefaultRetryPolicy();
            }

            return new VJ<T>(method, url, requestBody, responseType, mapper, retryPolicy, successListener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = super.getHeaders();
                    headers.putAll(RequestBuilder.this.headers);

                    return headers;
                }

            };
        }

        /**
         * Builds a VJRequest and adds it to the supplied RequestQueue.
         *
         * @param requestQueue to add built VJRequest to
         */
        public void execute(RequestQueue requestQueue) {
            requestQueue.add(build());
        }
    }
}


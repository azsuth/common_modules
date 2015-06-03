package com.azsuth.commonmoduleproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.azsuth.appcache.AppCache;
import com.azsuth.commonmoduleproject.model.Test;
import com.azsuth.commonmoduleproject.model.Tests;
import com.azsuth.volleyjacksonrequest.VJRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Random;


public class MainActivity extends Activity {
    private static final String BASE_URL = "http://mocksolstice.herokuapp.com/common_modules/common/play/";

    private static final String APP_CACHE_FIELD_KEY = "APP_CACHE_FIELD_KEY";
    private static final String APP_CACHE_INT_KEY = "APP_CACHE_INT_KEY";

    private RequestQueue requestQueue;

    private EditText cacheField;
    private Button writeCacheButton, readCacheButton, writeIntButton, readIntButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init caching
        cacheField = (EditText) findViewById(R.id.cache_field);
        writeCacheButton = (Button) findViewById(R.id.write_cache_button);
        readCacheButton = (Button) findViewById(R.id.read_cache_button);
        writeIntButton = (Button) findViewById(R.id.write_int_button);
        readIntButton = (Button) findViewById(R.id.read_int_button);

        writeCacheButton.setOnClickListener(cacheClickListener);
        readCacheButton.setOnClickListener(cacheClickListener);
        writeIntButton.setOnClickListener(cacheClickListener);
        readIntButton.setOnClickListener(cacheClickListener);

        // init vjr
        requestQueue = Volley.newRequestQueue(this);
        findViewById(R.id.simple_request_button).setOnClickListener(vjrClickListener);
        findViewById(R.id.complex_request_button).setOnClickListener(vjrClickListener);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cacheField.getWindowToken(), 0);
    }

    private View.OnClickListener cacheClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.write_cache_button:
                    String newCacheValue = cacheField.getText().toString();
                    AppCache.INSTANCE.put(APP_CACHE_FIELD_KEY, newCacheValue);

                    cacheField.setText(null);
                    break;
                case R.id.read_cache_button:
                    if (AppCache.INSTANCE.has(APP_CACHE_FIELD_KEY)) {
                        String cacheValue = AppCache.INSTANCE.get(APP_CACHE_FIELD_KEY, new AppCache.TypeReference<String>() {
                        });

                        cacheField.setText(cacheValue);
                    } else {
                        Toast.makeText(MainActivity.this, "No stored value", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.write_int_button:
                    AppCache.INSTANCE.put(APP_CACHE_INT_KEY, new Random().nextInt(100));
                    break;
                case R.id.read_int_button:
                    if (AppCache.INSTANCE.has(APP_CACHE_INT_KEY)) {
                        Integer cacheInt = AppCache.INSTANCE.get(APP_CACHE_INT_KEY, AppCache.INTEGER_TYPE_REFERENCE);

                        Toast.makeText(MainActivity.this, String.valueOf(cacheInt), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "No stored int", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

            closeKeyboard();
        }

    };

    private View.OnClickListener vjrClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.simple_request_button:
                    VJRequest.get(new TypeReference<Test>() {})
                            .from(BASE_URL + "testbasic")
                            .onSuccess(new Response.Listener<Test>() {

                                @Override
                                public void onResponse(Test response) {
                                    Toast.makeText(MainActivity.this, String.format("Test values: %s, %d", response.test1, response.test2), Toast.LENGTH_SHORT).show();
                                }

                            })
                            .onFailure(new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, String.format("Error: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                                }

                            }).execute(requestQueue);
                    break;
                case R.id.complex_request_button:
                    VJRequest.post("request_body")
                            .to(BASE_URL + "testcomplex")
                            .withResponseType(new TypeReference<Tests>() {})
                            .onSuccess(new Response.Listener<Tests>() {

                                @Override
                                public void onResponse(Tests response) {
                                    StringBuilder string = new StringBuilder();
                                    string.append(String.format("%d results:", response.results));

                                    for (Test test : response.tests) {
                                        string.append(String.format("\ntest1: %s, test2: %d", test.test1, test.test2));
                                    }

                                    Toast.makeText(MainActivity.this, string.toString(), Toast.LENGTH_SHORT).show();
                                }

                            })
                            .onFailure(new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, String.format("Error: %s", error.getMessage()), Toast.LENGTH_SHORT).show();
                                }

                            }).execute(requestQueue);
                    break;
            }
        }

    };
}

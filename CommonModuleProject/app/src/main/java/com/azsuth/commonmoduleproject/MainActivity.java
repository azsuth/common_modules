package com.azsuth.commonmoduleproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.azsuth.appcache.AppCache;
import com.azsuth.commonmoduleproject.model.TimeAndDate;
import com.azsuth.volleyjacksonrequest.VJRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Random;


public class MainActivity extends Activity {
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
        findViewById(R.id.time_and_date_button).setOnClickListener(vjrClickListener);
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
                case R.id.time_and_date_button:
                    VJRequest<TimeAndDate> timeAndDateRequest = new VJRequest<>(Request.Method.GET, "http://date.jsontest.com", new TypeReference<TimeAndDate>() {
                    }, new Response.Listener<TimeAndDate>() {

                        @Override
                        public void onResponse(TimeAndDate response) {
                            AppCache.INSTANCE.put("test", response);
                            Toast.makeText(MainActivity.this, String.format("Currently: %s %s", response.date, response.time), Toast.LENGTH_SHORT).show();
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });

                    requestQueue.add(timeAndDateRequest);
                    break;
            }
        }

    };

    private class TextWatcherAdapter implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // override for functionality
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // override for functionality
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // override for functionality
        }
    }
}

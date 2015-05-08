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
import com.azsuth.AppCache;
import com.azsuth.AppCacheTypeReference;
import com.azsuth.commonmoduleproject.model.TimeAndDate;
import com.azsuth.volleyjacksonrequest.VJRequest;
import com.fasterxml.jackson.core.type.TypeReference;


public class MainActivity extends Activity {
    private static final String APP_CACHE_FIELD_KEY = "APP_CACHE_FIELD_KEY";

    private RequestQueue requestQueue;

    private EditText cacheField;
    private Button writeCacheButton, readCacheButton, clearCacheButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init caching
        cacheField = (EditText) findViewById(R.id.cache_field);
        writeCacheButton = (Button) findViewById(R.id.write_cache_button);
        readCacheButton = (Button) findViewById(R.id.read_cache_button);
        clearCacheButton = (Button) findViewById(R.id.clear_cache_button);

        writeCacheButton.setOnClickListener(cacheClickListener);
        readCacheButton.setOnClickListener(cacheClickListener);
        clearCacheButton.setOnClickListener(cacheClickListener);

        cacheField.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    writeCacheButton.setEnabled(false);
                } else {
                    writeCacheButton.setEnabled(true);
                }
            }

        });

        writeCacheButton.setEnabled(false);
        readCacheButton.setEnabled(false);
        clearCacheButton.setEnabled(false);

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

                    readCacheButton.setEnabled(true);
                    clearCacheButton.setEnabled(true);
                    break;
                case R.id.read_cache_button:
                    String cacheValue = AppCache.INSTANCE.get(APP_CACHE_FIELD_KEY, new AppCacheTypeReference<String>() {
                    });

                    cacheField.setText(cacheValue);
                    break;
                case R.id.clear_cache_button:
                    AppCache.INSTANCE.remove(APP_CACHE_FIELD_KEY);

                    cacheField.setText(null);

                    readCacheButton.setEnabled(false);
                    clearCacheButton.setEnabled(false);
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
                    VJRequest<TimeAndDate> timeAndDateRequest = new VJRequest<TimeAndDate>(Request.Method.GET, "http://date.jsontest.com", new TypeReference<TimeAndDate>() {
                    }, new Response.Listener<TimeAndDate>() {

                        @Override
                        public void onResponse(TimeAndDate response) {
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

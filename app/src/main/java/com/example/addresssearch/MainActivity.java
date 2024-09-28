package com.example.addresssearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView resultsRecyclerView;
    private AddressesAdapter addressesAdapter;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private long searchDelay = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);

        addressesAdapter = new AddressesAdapter();
        resultsRecyclerView.setAdapter(addressesAdapter);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                if (s.length() > 0) {
                    handler.postDelayed(searchRunnable, searchDelay);
                } else {
                    addressesAdapter.updateResults(Collections.emptyList(), "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Log.d("performSearch", "Query is empty");
            addressesAdapter.updateResults(Collections.emptyList(), query);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.opencagedata.com/geocode/v1/json?q=" + Uri.encode(query) + "&key=c35b0b6fe7bb4d94afacd3f213c072cd";
        Log.d("performSearch", "Request URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("performSearch", "API Response: " + response);
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray resultsArray = jsonResponse.getJSONArray("results");

                        List<String> searchResults = new ArrayList<>();
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject result = resultsArray.getJSONObject(i);
                            String address = result.optString("formatted", null);
                            if (address != null) {
                                searchResults.add(address);
                            } else {
                                Log.w("performSearch", "No formatted address found for result index: " + i);
                            }
                        }

                        Log.d("performSearch", "Addresses found: " + searchResults.toString());
                        addressesAdapter.updateResults(searchResults, query);
                    } catch (JSONException e) {
                        Log.e("performSearch", "JSON Parsing error: " + e.getMessage());
                        e.printStackTrace();
                        addressesAdapter.updateResults(Collections.emptyList(), query);
                    }
                },
                error -> {
                    Log.e("performSearch", "API Request error: " + error.getMessage());
                    error.printStackTrace();
                    addressesAdapter.updateResults(Collections.emptyList(), query);
                });

        queue.add(stringRequest);
        Log.d("performSearch", "Request added to queue");
    }
}

package com.uteating.foodapp.activity.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uteating.foodapp.adapter.Home.FindAdapter;
import com.uteating.foodapp.databinding.ActivityFindBinding;

import java.io.File;
import java.util.ArrayList;

public class FindActivity extends AppCompatActivity {
    private ActivityFindBinding binding;
    private ArrayList<String> history_search;
    private FindAdapter adapter;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initUI();
    }

    private void initUI() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        binding.searhView.setIconifiedByDefault(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rcHistorySearch.setLayoutManager(linearLayoutManager);
        adapter = new FindAdapter(history_search, userId, this);
        binding.rcHistorySearch.setAdapter(adapter);
        binding.rcHistorySearch.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.searhView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                SharedPreferences sharedPreferences = getSharedPreferences("history_search", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!s.equals(history_search.get(0))) {
                    history_search.add(0, s); // Add to the start
                    if (history_search.size() > 3) {
                        history_search.remove(3); // Remove the oldest item if the list exceeds 3
                    }
                }
                editor.clear();
                if (history_search.size() > 2) {
                    editor.putString("3rd", history_search.get(2));
                }
                if (history_search.size() > 1) {
                    editor.putString("2nd", history_search.get(1));
                }
                if (history_search.size() > 0) {
                    editor.putString("1st", history_search.get(0));
                }
                editor.commit();
                Intent intent = new Intent(FindActivity.this, ResultSearchActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("text", s);
                Log.d("text size", String.valueOf(history_search.size()));
                startActivityForResult(intent, 101);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        fetchData();
    }

    private void initData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        history_search = new ArrayList<>();
    }

    private void fetchData() {
        File sharedPrefsFile = new File(this.getFilesDir().getParent() + "/shared_prefs/" + "history_search.xml");
        SharedPreferences sharedPreferences = getSharedPreferences("history_search", this.MODE_PRIVATE);

        if (!sharedPrefsFile.exists()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("1st", "Trà sữa");
            editor.putString("2nd", "Cơm");
            editor.putString("3rd", "Bún");
            editor.apply(); // Don't forget to apply changes
        }
        history_search.add(sharedPreferences.getString("1st", ""));
        history_search.add(sharedPreferences.getString("2nd", ""));
        history_search.add(sharedPreferences.getString("3rd", ""));

        // Notify adapter after data is fetched
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        if (requestCode == 101 && resultCode == 101) {
            if (data != null) {
                ArrayList<String> arr = data.getStringArrayListExtra("history_search");
                if (arr != null) {
                    history_search.clear(); // Clear the existing data
                    history_search.addAll(arr); // Add the new data
                    Log.d("Size return", String.valueOf(history_search.size()));
                    for (String i: history_search){
                        Log.d("value", i);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}

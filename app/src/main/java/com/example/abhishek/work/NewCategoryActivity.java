package com.example.abhishek.work;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.Model.CategoryData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.ViewModels.CategoriesViewModel;
import com.example.abhishek.work.adapters.CategoryListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;
    private CategoriesViewModel categoriesViewModel;

    private FetchData fetchData;
    private ArrayList<CategoryData> arrayList;
    private JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        recyclerView = (RecyclerView) findViewById(R.id.new_category_activity_recycler_view_id);
        arrayList = new ArrayList<>();
        adapter = new CategoryListAdapter(NewCategoryActivity.this, arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fetchData = new FetchData(getApplication());

        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        categoriesViewModel.getCategories(fetchData).observe(this, new Observer<ArrayList<CategoryData>>() {
            @Override
            public void onChanged(@Nullable ArrayList<CategoryData> categoryData) {
                arrayList.addAll(categoryData);
                adapter.notifyDataSetChanged();
            }
        });


        ServerResponse serverResponse = fetchData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    ArrayList<CategoryData> list = new ArrayList<>();
                    JSONArray jsonArray = responseJSONObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        CategoryData categoryData = new CategoryData();
                        categoryData.setName(j.getString("name"));
                        categoryData.setId(j.getInt("id"));
                        list.add(categoryData);
                    }
                    categoriesViewModel.setCategoriesList(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private void updateUI(boolean isNetworkAbailable){
        if (!isNetworkAbailable){
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    //connected
                    updateUI(true);
                }else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}

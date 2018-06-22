package com.example.abhishek.work;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.ViewModels.ProductsViewModel;
import com.example.abhishek.work.adapters.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewProductActivity extends AppCompatActivity {

    private String name;
    private int id;

    private FetchData fetchData;
    private ServerResponse serverResponse;

    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ArrayList<ProductData> arrayList;
    private JSONArray jsonArray;

    private ProductsViewModel productsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Log.e("onCreate", "newProduct");

        recyclerView = (RecyclerView) findViewById(R.id.new_product_activity_recyclerview_id);
        arrayList = new ArrayList<>();
        adapter = new ProductListAdapter(NewProductActivity.this, arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        id = intent.getIntExtra("id", -1);

        if (id < 0) {
            Toast.makeText(this, "some error occured !", Toast.LENGTH_SHORT).show();
            finishActivity(2205);
        }

        fetchData = new FetchData(getApplication());

        productsViewModel = ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getProductsList(fetchData,name,id).observe(this, new Observer<ArrayList<ProductData>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ProductData> productData) {
                arrayList.addAll(productData);
                adapter.notifyDataSetChanged();
            }
        });

        serverResponse = fetchData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    ArrayList<ProductData> list = new ArrayList<>();
                    jsonArray = responseJSONObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        ProductData productData = new ProductData();
                        productData.setName(jsonObject.getString("name"));
                        productData.setProductID(jsonObject.getInt("id"));
                        productData.setAttribute_set_id(jsonObject.getInt("attribute-set-id"));
                        productData.setPrice(jsonObject.getDouble("price"));
                        productData.setPhoto(jsonObject.getString("image-url"));
                        list.add(productData);
                    }
                    productsViewModel.setProductsList(list);
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

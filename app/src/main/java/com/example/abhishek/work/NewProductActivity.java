package com.example.abhishek.work;

import android.content.Intent;
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

        fetchData = new FetchData(NewProductActivity.this);
        fetchData.getProducts(name,id);
        serverResponse = fetchData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    Log.e("get_Product response", responseJSONObject.toString());
                    jsonArray = responseJSONObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        ProductData productData = new ProductData();
                        productData.setName(jsonObject.getString("name"));
                        productData.setProductID(jsonObject.getInt("id"));
                        productData.setAttribute_set_id(jsonObject.getInt("attribute-set-id"));
                        productData.setPrice(jsonObject.getDouble("price"));
                        productData.setPhoto(jsonObject.getString("image-url"));
                        arrayList.add(productData);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart", "newProduct");
        /*
        arrayList.clear();
        if (id > 0) {
            fetchData.getProducts(name, id);
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("OnResume", "newProduct");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("OnPause", "newProduct");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("onStop", "newProduct");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy", "newProduct");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onReCreate", "newProduct");
        /*
        try {
            if (jsonArray != null) {
                arrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    ProductData productData = new ProductData();
                    productData.setName(jsonObject.getString("name"));
                    productData.setProductID(jsonObject.getInt("id"));
                    productData.setAttribute_set_id(jsonObject.getInt("attribute-set-id"));
                    productData.setPrice(jsonObject.getDouble("price"));
                    productData.setPhoto(jsonObject.getString("image-url"));
                    arrayList.add(productData);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

}

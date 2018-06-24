package com.example.abhishek.work;

import android.animation.Animator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.Model.CategoryData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.ViewModels.CategoriesViewModel;
import com.example.abhishek.work.adapters.CategoryListAdapter;
import com.example.abhishek.work.adapters.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewCategoryActivity extends AppCompatActivity {

    private ImageButton searchBtn;
    private EditText searchEdittext;
    private TextView titleTextView;
    private boolean isSearchOn = false;

    private ArrayList<ProductData> productsList;
    private ProductListAdapter productListAdapter;

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

        searchBtn = (ImageButton) findViewById(R.id.new_category_activity_search_btn_id);
        searchEdittext = (EditText) findViewById(R.id.new_category_activity_search_edittext_id);
        titleTextView = (TextView) findViewById(R.id.new_category_activity_title_textview_id);

        //For categories
        recyclerView = (RecyclerView) findViewById(R.id.new_category_activity_recycler_view_id);
        arrayList = new ArrayList<>();
        adapter = new CategoryListAdapter(NewCategoryActivity.this, arrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //For products
        productsList = new ArrayList<>();
        productListAdapter = new ProductListAdapter(NewCategoryActivity.this, productsList);


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
                    String responseFrom = responseJSONObject.getString("responseFrom");
                    if (responseFrom.equals("magento_get_categories")) {
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
                    } else if (responseFrom.equals("magento_search_product")) {
                        //TODO parse response and make arraylist

                        recyclerView.setAdapter(productListAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearchOn = true;
                showSearching();
            }
        });

        searchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String searchTerm = "";
        searchTerm = searchEdittext.getText().toString().trim();
        if (!searchTerm.isEmpty()) {
            fetchData.searchProduct(searchTerm);
        }
    }

    private void showSearching() {
        View view = searchEdittext;
        View btnView = searchBtn;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int cx = (btnView.getLeft() + btnView.getRight()) / 2;
            int cy = (btnView.getTop() + btnView.getBottom()) / 2;
            int finalRadius = searchEdittext.getWidth();
            Animator showAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            showAnimator.setDuration(300);
            searchEdittext.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.INVISIBLE);
            showAnimator.start();
            searchEdittext.requestFocus();
            titleTextView.setVisibility(View.INVISIBLE);
            arrayList.clear();
            adapter.notifyDataSetChanged();
        } else {
            titleTextView.setVisibility(View.INVISIBLE);
            searchEdittext.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSearching() {
        /*
        View view = searchEdittext;
        View btnView = searchBtn;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int cx = (btnView.getLeft() + btnView.getRight()) / 2;
            int cy = (btnView.getTop() + btnView.getBottom()) / 2;
            int finalRadius = searchEdittext.getWidth();
            Animator hideAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
            hideAnimator.setDuration(3000);
            hideAnimator.start();
            titleTextView.setVisibility(View.VISIBLE);
            searchEdittext.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
        }else {
            titleTextView.setVisibility(View.VISIBLE);
            searchEdittext.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.VISIBLE);
        }
        */
        titleTextView.setVisibility(View.VISIBLE);
        searchEdittext.setVisibility(View.INVISIBLE);
        searchBtn.setVisibility(View.VISIBLE);
        loadCategoriesData();
    }

    private void loadCategoriesData() {
        categoriesViewModel.getCategories(fetchData).observe(this, new Observer<ArrayList<CategoryData>>() {
            @Override
            public void onChanged(@Nullable ArrayList<CategoryData> categoryData) {
                arrayList.addAll(categoryData);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSearchOn) {
            isSearchOn = false;
            hideSearching();
            productsList.clear();
            productListAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            super.onBackPressed();
        }
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

    private void updateUI(boolean isNetworkAbailable) {
        if (!isNetworkAbailable) {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        } else {
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
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    //connected
                    updateUI(true);
                } else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}

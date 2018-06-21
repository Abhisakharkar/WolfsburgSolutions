package com.example.abhishek.work.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.ServerOperations.FetchData;

import java.util.ArrayList;

public class ProductsViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<ProductData>> productsList;

    public ProductsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<ProductData>> getProductsList(FetchData fetchData, String name, int id) {
        if (productsList == null) {
            productsList = new MutableLiveData<>();
            fetchData.getProducts(name,id);
        }
        return productsList;
    }

    public void setProductsList(ArrayList<ProductData> productsList){
        this.productsList.setValue(productsList);
    }
}

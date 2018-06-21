package com.example.abhishek.work.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.abhishek.work.Model.CategoryData;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoriesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<CategoryData>> categoriesList;

    public LiveData<ArrayList<CategoryData>> getCategories(FetchData fetchData) {
        if (categoriesList == null) {
            categoriesList = new MutableLiveData<ArrayList<CategoryData>>();
            fetchData.getCategories();
        }
        return categoriesList;
    }

    public void setCategoriesList(ArrayList<CategoryData> categoriesList){
        this.categoriesList.setValue(categoriesList);
    }
}

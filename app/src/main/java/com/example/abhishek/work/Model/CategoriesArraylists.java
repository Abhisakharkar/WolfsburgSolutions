package com.example.abhishek.work.Model;

import java.util.ArrayList;

public class CategoriesArraylists {

    private static CategoriesArraylists categoriesArraylists;

    private ArrayList<CategoryData> categoriesLevel1ArrayList;
    private ArrayList<CategoryData> categoriesLevel2ArrayList;
    private ArrayList<CategoryData> categoriesLevel3ArrayList;

    public static CategoriesArraylists getInstance(){
        if (categoriesArraylists == null){
            categoriesArraylists = new CategoriesArraylists();
        }
        return categoriesArraylists;
    }

    public ArrayList<CategoryData> getCategoriesLevel1ArrayList() {
        return categoriesLevel1ArrayList;
    }

    public void setCategoriesLevel1ArrayList(ArrayList<CategoryData> categoriesLevel1ArrayList) {
        this.categoriesLevel1ArrayList = categoriesLevel1ArrayList;
    }

    public ArrayList<CategoryData> getCategoriesLevel2ArrayList() {
        return categoriesLevel2ArrayList;
    }

    public void setCategoriesLevel2ArrayList(ArrayList<CategoryData> categoriesLevel2ArrayList) {
        this.categoriesLevel2ArrayList = categoriesLevel2ArrayList;
    }

    public ArrayList<CategoryData> getCategoriesLevel3ArrayList() {
        return categoriesLevel3ArrayList;
    }

    public void setCategoriesLevel3ArrayList(ArrayList<CategoryData> categoriesLevel3ArrayList) {
        this.categoriesLevel3ArrayList = categoriesLevel3ArrayList;
    }
}

package com.example.abhishek.work.ServerOperations;

import android.content.Context;

import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

import java.util.HashMap;

public class SendData {

    private String URL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/";
    private Context context;
    private ServerResponse serverResponse = new ServerResponse();
    private JSONObject reqBody = new JSONObject();
    private HashMap<String,String> header = new HashMap<>();

    public SendData(Context context){
        this.context = context;
    }

    public void addProductToShop(String retailerID,String productID,String price,String desc,boolean avail,boolean star,String comment){
        URL = URL + "add_product";
        String image = "product_"+"RetailerID_"+"extension";

        //price : double
        //retailerID : (temp) fixed int


        try{
            header.put("Content-Type","application/json");
            //send all this info

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

package com.example.abhishek.work.ServerOperations;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendData {

    private String URL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/";
    private Context context;
    //private ServerResponse serverResponse = new ServerResponse();
    private ServerResponse serverResponse;
    private JSONObject reqBody = new JSONObject();
    private HashMap<String,String> header = new HashMap<>();

    public SendData(Context context){
        this.context = context;
    }

    public void addProductToShop(String retailerID,String productID,String price,String desc,int avail,int star,String comment){
        URL = URL + "add_product";
        String image = "product_"+retailerID+".jpeg";
        //photo : blank

        try{
            header.put("Content-Type","application/json");
            //send all this info
            header.put("retailerID",retailerID);
            header.put("productID",productID);
            header.put("price",price);
            header.put("desc",desc);
            header.put("avail",String.valueOf(avail));
            header.put("star",String.valueOf(star));
            header.put("comment",comment);

            reqBody.put("retailerID",retailerID);
            reqBody.put("productID",productID);
            reqBody.put("price",price);
            reqBody.put("desc",desc);
            reqBody.put("avail",String.valueOf(avail));
            reqBody.put("star",String.valueOf(star));
            reqBody.put("comment",comment);

            sendRequest();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST
                , URL
                , new JSONObject(header)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                serverResponse.saveResponse(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = header;
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "text/plain; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return reqBody.toString().getBytes();
            }
        };
        requestQueue.add(jsonObjectRequest);
    }


    public ServerResponse getServerResponseInstance(){
        if (serverResponse == null){
            serverResponse = new ServerResponse();
        }
        return this.serverResponse;
    }
}
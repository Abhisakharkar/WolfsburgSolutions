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

public class FetchData {

    private String URL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/";
    private Context context;
    private ServerResponse serverResponse = new ServerResponse();
    private JSONObject reqBody = new JSONObject();
    private HashMap<String,String> header = new HashMap<>();

    public FetchData(Context context){
        this.context = context;
    }

    public void getCategories(){
        URL = URL+"magento_get_category";
        sendRequest();
    }

    public void getProducts(String name,int id){
        URL = URL+"magento_product_display";
        try {
            reqBody.put("name", name);
            reqBody.put("id_category",id);

            header.put("Content-Type","application/json");
            header.put("name",name);
            header.put("id_category",String.valueOf(id));
            sendRequest();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getProductDetails(String product_SKU){
        URL = URL+"magento_info_product";

        //send only this
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
        return this.serverResponse;
    }
}

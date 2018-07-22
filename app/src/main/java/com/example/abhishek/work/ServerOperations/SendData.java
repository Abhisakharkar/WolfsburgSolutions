package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SendData {

    private String serverURL = "http://ec2-13-58-16-206.us-east-2.compute.amazonaws.com:6868";
    private Context context;
    //private ServerResponse serverResponse = new ServerResponse();
    private ServerResponse serverResponse;
    private JSONObject reqBody = new JSONObject();
    private HashMap<String,String> headers;

    public SendData(Context context){
        this.context = context;
    }

    public ServerResponse getServerResponseInstance(){
        if (serverResponse == null){
            serverResponse = new ServerResponse();
        }
        return this.serverResponse;
    }

    public void sendLatLoc(double latitude,double longitude){
        headers = new HashMap<>();
        headers.put("latloc",String.valueOf(latitude));
        headers.put("longloc",String.valueOf(longitude));

        String url = serverURL + "/get_location_ids";
        sendRequest(url,headers);
    }

    public void sendtime(HashMap<String,String> headers){
        this.headers = new HashMap<>();
        this.headers = headers;
        String url = serverURL + "/update_full_retailer_data";
        sendRequest(url,headers);
    }

    public void updateDeliverySettings(int maxDist,int maxFreeDist,int charge,int minAmount){
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("maxDeliveryDistanceInMeters",String.valueOf(maxDist));
        headers.put("maxFreeDeliveryDistanceInMeters",String.valueOf(maxFreeDist));
        headers.put("chargePerHalfKiloMeterForDelivery",String.valueOf(charge));
        headers.put("minAmountForFreeDelivery",String.valueOf(minAmount));

        String url = serverURL + "/update_full_retailer_data";
        sendRequest(url,headers);
    }

    public void updateProduct(String key,String value,int productId){
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put(key,value);
        headers.put("productId",String.valueOf(productId));

        String url = serverURL + "/update";
        sendRequest(url,headers);
    }

    public void addProductToShop(String retailerID,String productID,String price,String desc,int avail,int star,String comment){
       // String image = "product_"+retailerID+".jpeg";
        //photo : blank

        try{
            headers = new HashMap<>();
            headers.put("Content-Type","application/json");
            headers.put("productId",productID);
            headers.put("price",price);
            //headers.put("photo","0");
            headers.put("description",desc);
            headers.put("availability",String.valueOf(avail));
            headers.put("star",String.valueOf(star));
            headers.put("textField",comment);

            String url = serverURL+"/add_retailer_product";
            sendRequest(url,headers);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateProductToShop(String retailerID,String productID,String price,String desc,int avail,int star,String comment){
        String image = "product_"+retailerID+".jpeg";
        //photo : blank

        try{
            headers.put("Content-Type","application/json");
            headers.put("productId",productID);
            headers.put("price",price);
            headers.put("description",desc);
            headers.put("availability",String.valueOf(avail));
            headers.put("star",String.valueOf(star));
            headers.put("textField",comment);

            String url = serverURL+"/update_retailer_product";
            sendRequest(url,headers);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendRequest(String url,Map<String,String> headers){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST
                , url
                , new JSONObject(headers)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                serverResponse.saveResponse(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error",error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header = new HashMap<>();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                header.put("Authorization", "Bearer "+sharedPreferences.getString("token", ""));
                return header;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }










    //deprecated
    public void sendImageUploadRequest(Bitmap photoBitmap, final String imageName, int retailerId) {

        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/upload";
        headers.put("retailerId",String.valueOf(retailerId));
        headers.put("imageName",imageName);

        //bitmap to byte convert
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        final byte[] photoByteArray = byteArrayOutputStream.toByteArray();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url
                , new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("Network_Response",response.toString());
                //TODO process response here OR implement serverResponse for NetworkResponse type of response
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response_Error",error.toString());
                serverResponse.saveResponseError(error.getMessage().toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params = headers;
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {

                Map<String,DataPart> params = new HashMap<>();
                params.put("image",new DataPart(imageName + ".jpeg",photoByteArray));
                return params;
            }
        };
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }
}

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

    private String serverURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868";
    private Context context;
    //private ServerResponse serverResponse = new ServerResponse();
    private ServerResponse serverResponse;
    private JSONObject reqBody = new JSONObject();
    private HashMap<String,String> header = new HashMap<>();

    public SendData(Context context){
        this.context = context;
    }

    public void addProductToShop(String retailerID,String productID,String price,String desc,int avail,int star,String comment){
        String image = "product_"+retailerID+".jpeg";
        //photo : blank

        try{
            header.put("Content-Type","application/json");
            header.put("retailerID",retailerID);
            header.put("productID",productID);
            header.put("price",price);
            header.put("photo","0");
            header.put("description",desc);
            header.put("availability",String.valueOf(avail));
            header.put("star",String.valueOf(star));
            header.put("textField",comment);

            String url = serverURL+"/add_retailer_product";
            sendRequest(url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateProductToShop(String retailerID,String productID,String price,String desc,int avail,int star,String comment){
        String image = "product_"+retailerID+".jpeg";
        //photo : blank

        try{
            header.put("Content-Type","application/json");
            header.put("retailerID",retailerID);
            header.put("productID",productID);
            header.put("price",price);
            header.put("description",desc);
            header.put("availability",String.valueOf(avail));
            header.put("star",String.valueOf(star));
            header.put("textField",comment);

            String url = serverURL+"/update_retailer_product";
            sendRequest(url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendRequest(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST
                , url
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
                Log.e("Response Error",error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = header;
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                params.put("token", sharedPreferences.getString("token", ""));
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



    public void sendImageUploadRequest(Bitmap photoBitmap, final String imageName, int retailerId) {

        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/upload";
        header.put("retailerId",String.valueOf(retailerId));
        header.put("imageName",imageName);

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
                params = header;
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


    public ServerResponse getServerResponseInstance(){
        if (serverResponse == null){
            serverResponse = new ServerResponse();
        }
        return this.serverResponse;
    }
}

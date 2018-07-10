package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FetchData {

    private String serverURL = "http://ec2-18-220-165-73.us-east-2.compute.amazonaws.com:6868";
    private Context context;
    private ServerResponse serverResponse = new ServerResponse();
    private JSONObject reqBody = new JSONObject();
    private HashMap<String, String> header;

    public FetchData(Context context) {
        this.context = context;
    }

    public void getCategories() {
        String url = serverURL + "/magento_get_categories";
        header = new HashMap<>();
        sendRequest(url,header);
    }

    public void getProducts(String name, int id) {

        String url = serverURL + "/magento_product_display";
        header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("name", name);
        header.put("id_category", String.valueOf(id));
        sendRequest(url,header);

    }

    public void searchProduct(String searchTerm) {
        String url = serverURL + "/magento_search_product";
        header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("searchTerm",searchTerm);
        sendRequest(url,header);
    }

    public void getProductDetails(int attributeSetId) {

        String url = serverURL + "/magento_get_attribute_with_group";

        header.put("attributeSetId",String.valueOf(attributeSetId));
        header.put("Content-Type", "application/json");

        sendRequest(url,header);

    }

    private void sendRequest(String url,Map<String,String> headers) {
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
                //Log.e("VolleyError",error.getMessage().toString()+"...");
                error.printStackTrace();
                //serverResponse.saveResponseError(error.getMessage().toString() + "|...");
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> header = new HashMap<>();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                header.put("token", sharedPreferences.getString("token", ""));

                return header;
            }
        }
        /*
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                String SET_COOKIE_KEY = "Set-Cookie";
                String SESSION_COOKIE = "sessionid";

                Map<String,String> headers = response.headers;

                if (headers.containsKey(SET_COOKIE_KEY)
                        && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
                    String cookie = headers.get(SET_COOKIE_KEY);
                    if (cookie.length() > 0) {
                        String[] splitCookie = cookie.split(";");
                        String[] splitSessionId = splitCookie[0].split("=");
                        cookie = splitSessionId[1];

                        Log.e("Session Cookie", cookie);
                    }
                }

                return super.parseNetworkResponse(response);
            }
        }
        */
        ;
        requestQueue.add(jsonObjectRequest);
    }


    public ServerResponse getServerResponseInstance() {
        return this.serverResponse;
    }
}

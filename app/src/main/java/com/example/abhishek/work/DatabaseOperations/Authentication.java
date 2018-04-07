package com.example.abhishek.work.DatabaseOperations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Authentication {

    //Database URLs
    private String databaseURL = "http://d1e6119b.ngrok.io/";

    //Request Objects
    private Context context;
    private String reqBody = "";
    private HashMap<String, String> headers;

    private JSONObject jsonObject, responseJSONObject;

    //Custom Response receive listener
    public ServerResponse serverResponse = new ServerResponse();

    //cunstructor
    public Authentication(Context context) {
        this.context = context;
    }


    //method to check if required profile data of user is saved in database or not
    public void checkData(String email) {

        headers = new HashMap<>();
        headers.put("req_type", "checkDataIsAvailable");
        headers.put("email", email);
        try {
            jsonObject = new JSONObject();
            jsonObject.put("req_type", "checkDataIsAvailable");
            jsonObject.put("email", email);
            reqBody = jsonObject.toString();

            sendRequest(databaseURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //send profile data to server
    public void sendUserProfile(String proprietor,String shopName,String mobileNo
            ,double longitude,double latitude
            ,String cityName,String stateName,String countryName){

        try {

            jsonObject = new JSONObject();
            jsonObject.put("proprietor",proprietor);
            jsonObject.put("enterpriseName",shopName);
            jsonObject.put("contactNo",mobileNo);
            jsonObject.put("city",cityName);
            jsonObject.put("state",stateName);
            jsonObject.put("country",countryName);
            jsonObject.put("longitude",longitude);
            jsonObject.put("latitude",latitude);

            databaseURL = databaseURL+"addRetailerToTemp.js";

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void signUp(String email, String password) {

        headers = new HashMap<>();
        //headers.put("req_type", "signUp");
        headers.put("mail", email.toString());
        headers.put("password", password.toString());
        try {
            jsonObject = new JSONObject();
            //jsonObject.put("req_type", "signUp");
            jsonObject.put("mail", email.toString());
            jsonObject.put("password", password.toString());
            Toast.makeText(context, email + " " + password, Toast.LENGTH_SHORT).show();
            reqBody = jsonObject.toString();
            databaseURL = databaseURL+"signUp.js";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signInWithEmail(String email, String password) {

        headers = new HashMap<>();
        headers.put("req_type", "signInWithEmail");
        headers.put("mail", email);
        headers.put("password", password);
        try {
            jsonObject = new JSONObject();
            jsonObject.put("req_type", "signInWithEmail");
            jsonObject.put("mail", email);
            jsonObject.put("password", password);
            reqBody = jsonObject.toString();
            databaseURL = databaseURL + "checkInPermanent.js";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(headers)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("sendRequest Response", response.toString());
                try {
                    //responseJSONObject = new JSONObject(response);
                    Toast.makeText(context, "response : " + response.toString(), Toast.LENGTH_SHORT).show();
                    serverResponse.saveResponse(responseJSONObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response_Error", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
/*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("sendRequest Response", response.toString());
                try {
                    responseJSONObject = new JSONObject(response);
                    Toast.makeText(context, "response : "+ response.toString(), Toast.LENGTH_SHORT).show();
                    serverResponse.saveResponse(responseJSONObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response_Error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = headers;
                return header;
            }

            @Override
            public String getBodyContentType() {
                return "text/plain; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return reqBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        requestQueue.add(stringRequest);
*/
    }
}

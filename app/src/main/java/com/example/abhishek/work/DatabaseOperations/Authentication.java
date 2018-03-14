package com.example.abhishek.work.DatabaseOperations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.ServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Authentication {

    //Database URLs
    private String databaseURL = "My Local Server Url";

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

    public void signUp(String email, String password) {

        headers = new HashMap<>();
        headers.put("req_type", "signUp");
        headers.put("email", email);
        headers.put("password", password);
        try {
            jsonObject = new JSONObject();
            jsonObject.put("req_type", "signUp");
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            reqBody = jsonObject.toString();

            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signInWithEmail(String email, String password) {

        headers = new HashMap<>();
        headers.put("req_type", "signInWithEmail");
        headers.put("email", email);
        headers.put("password", password);
        try {
            jsonObject = new JSONObject();
            jsonObject.put("req_type", "signInWithEmail");
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            reqBody = jsonObject.toString();

            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("sendRequest Response", response.toString());
                try {
                    responseJSONObject = new JSONObject(response);
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
    }
}

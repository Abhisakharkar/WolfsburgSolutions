package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Authentication {

    //Database URLs
    private String databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/";

    //Request Objects
    private Context context;
    private String reqBody = "";
    private HashMap<String, String> headers;

    private JSONObject jsonObject, responseJSONObject;

    //Custom Response receive listener
    public ServerResponse serverResponse = new ServerResponse();

    //constructor
    public Authentication(Context context) {
        this.context = context;
    }

    public ServerResponse getServerResponseInstance(){
        return serverResponse;
    }

    //send profile data to server
    public void sendUserProfile(String proprietor, String shopName, String mobileNo
            , double longitude, double latitude
            , String cityName, String stateName, String countryName) {

        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("proprietor", proprietor);
        headers.put("enterpriseName", shopName);
        headers.put("contactNo", mobileNo);
        headers.put("city", cityName);
        headers.put("state", stateName);
        headers.put("country", countryName);
        headers.put("longitude", String.valueOf(longitude));
        headers.put("latitude", String.valueOf(latitude));

        try {
            jsonObject = new JSONObject();
            jsonObject.put("proprietor", proprietor);
            jsonObject.put("enterpriseName", shopName);
            jsonObject.put("contactNo", mobileNo);
            jsonObject.put("city", cityName);
            jsonObject.put("state", stateName);
            jsonObject.put("country", countryName);
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/details_input_temp";

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //check if user is present in permanent table in server at the time of SignUp
    public void checkInPermanent(String mail, String password) {
        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("password", password);
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("mail", mail);
            jsonObject.put("password", password);
            reqBody = jsonObject.toString();
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/check_in_perm";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //user not present in permanent or temp table in server
    //sign up the user
    public void signUp(String email, String password) {

        headers = new HashMap<>();
        //headers.put("req_type", "signUp");
        headers.put("mail", email.toString());
        headers.put("password", password.toString());
        headers.put("enterpriseName", "");
        headers.put("propritor", "");
        headers.put("contactNo", "0");
        headers.put("profilePhoto", "");
        headers.put("retailerId","1001");
        headers.put("latLocation", "0.0");
        headers.put("longLocation", "0.0");
        headers.put("address", "");
        headers.put("city", "");
        headers.put("state", "");
        headers.put("country", "");
        headers.put("membership", "0");
        headers.put("subDate", "2010-01-01");
        headers.put("openCloseIsManual", "0");
        headers.put("shopOpenTime", "00:00:00");
        headers.put("shopCloseTime", "00:00:00");
        headers.put("shopOpenTime2", "00:00:00");
        headers.put("shopCloseTime2", "00:00:00");
        headers.put("shopPhoto", "");
        headers.put("shopActLicense", "");
        headers.put("currentState", "0");
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            //jsonObject.put("req_type", "signUp");
            jsonObject.put("mail", email.toString());
            jsonObject.put("password", password.toString());
            jsonObject.put("enterpriseName", "");
            jsonObject.put("propritor", "");
            jsonObject.put("retailerId","1001");
            jsonObject.put("contactNo", "0");
            jsonObject.put("profilePhoto", "");
            jsonObject.put("latLocation", "0.0");
            jsonObject.put("longLocation", "0.0");
            jsonObject.put("address", "");
            jsonObject.put("city", "");
            jsonObject.put("state", "");
            jsonObject.put("country", "");
            jsonObject.put("membership", "0");
            jsonObject.put("subDate", "2010-01-01");
            jsonObject.put("openCloseIsManual", "0");
            jsonObject.put("shopOpenTime", "00:00:00");
            jsonObject.put("shopCloseTime", "00:00:00");
            jsonObject.put("shopOpenTime2", "00:00:00");
            jsonObject.put("shopCloseTime2", "00:00:00");
            jsonObject.put("shopPhoto", "");
            jsonObject.put("shopActLicense", "");
            jsonObject.put("currentState", "0");
            reqBody = jsonObject.toString();
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/add_retailer_info_temp";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //sign in the user
    public void signInWithEmail(String email, String password) {

        headers = new HashMap<>();
        headers.put("req_type", "signInWithEmail");
        headers.put("mail", email);
        headers.put("password", password);
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("req_type", "signInWithEmail");
            jsonObject.put("mail", email);
            jsonObject.put("password", password);
            reqBody = jsonObject.toString();
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/check_in_perm";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send user email for verification (to verification script)
    public void verifyEmail(String mail) {
        databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/verify_email_id";
        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("mail", mail);
            reqBody = jsonObject.toString();
            sendRequest(databaseURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send verification code from email to server to complete verification
    public void sendVerificationCode(int code) {

        headers = new HashMap<>();
        headers.put("code", String.valueOf(code));
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("code", code);
            reqBody = jsonObject.toString();

            //TODO send mail and password also

            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/verifiication_complete";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isProfileDataComplete(String mail) {
        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("Content-Type", "application/json");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("mail", mail);

            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/isDataFilled";
            sendRequest(databaseURL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //actually send request with given body and headers to given url
    private void sendRequest(String URL) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(headers)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("sendRequest Response", response.toString());
                try {
                    //temp
                    JSONObject j = new JSONObject();
                    j = response;
                    Log.e("j",j.toString());

                    //responseJSONObject = new JSONObject(response);
                    serverResponse.saveResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response_Error",databaseURL + " : " + error.toString());
            }
        });

        //retry policy
        /*
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                Log.e("Retry Error", error.toString());
            }
        });
        */

        requestQueue.add(jsonObjectRequest);
    }
}

package com.example.abhishek.work.ServerOperations;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Authentication {

    private Context context;
    private String serverUrl = "http://ec2-13-59-88-132.us-east-2.compute.amazonaws.com:6868";
    public ServerResponse serverResponse;
    private Map<String, String> headers;

    public Authentication(Context context) {
        this.context = context;
        serverResponse = new ServerResponse();
    }

    public ServerResponse getServerResponseInstance() {
        return serverResponse;
    }

    public void checkEmailExists(String mail) {
        headers = new HashMap<>();
        headers.put("mail", mail);
        String url = serverUrl + "/check_mail_exist";
        sendRequestNew(url, headers);
    }

    public void signUpNew(String mail, String password) {
        //subSciptionDateTime (current date and time)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateTime = simpleDateFormat.format(Calendar.getInstance().getTime());

        //device ID
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("password", password);
        headers.put("subscriptionDateTime", dateTime);
        headers.put("deviceId", deviceId);

        String url = serverUrl + "/sign_up";
        sendRequestNew(url, headers);
    }

    public void signIn(String mail, String password) {
        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("password", password);

        String url = serverUrl + "/sign_in";
        sendRequestNew(url, headers);
    }

    public void signInFromThisDevice(int retailerId) {
        headers = new HashMap<>();
        headers.put("retailerId", String.valueOf(retailerId));
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        headers.put("deviceId", deviceId);

        String url = serverUrl + "/update_device_id";
        sendRequestNew(url, headers);
    }

    public void sendVerificationCode(String code) {
        headers = new HashMap<>();
        headers.put("code", code);
        String url = serverUrl + "/verify_mail";
        sendRequestNew(url, headers);
    }

    public void updateProfile(String proprietor, String shopName, String mobileNo, String longitude, String latitude, String address, String licenseNo, String localityId, String subLocality1Id) {

        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("enterpriseName", shopName);
        headers.put("proprietor", proprietor);
        headers.put("shopActLicenseNo", licenseNo);
        headers.put("mobileNo", mobileNo);
        headers.put("latLoc",latitude);
        headers.put("longLoc",longitude);
        headers.put("localityId",localityId);
        headers.put("subLocality1Id",subLocality1Id);

//        headers.put("latLoc", String.valueOf(latitude));
//        headers.put("longLoc", String.valueOf(longitude));
        headers.put("addLine1", address);
        headers.put("mandatoryData",Integer.toString(1));
        String url = serverUrl + "/update_full_retailer_data";
        sendRequestNew(url, headers);
    }

    private void sendRequestNew(String url, Map<String, String> headers) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(headers)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("sendRequest Response", response.toString());
                try {
                    //responseJSONObject = new JSONObject(response);
                    serverResponse.saveResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyResponseError", error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> header = new HashMap<>();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                header.put("Authorization", "bearer "+sharedPreferences.getString("token", ""));
                Log.e("Authentication token",sharedPreferences.getString("token","no-token"));
                return header;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    //old code

    //Database URLs
    private String databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/";

    //Request Objects
    private String reqBody = "";

    private JSONObject jsonObject, responseJSONObject;

    //send profile data to server
    public void sendUserProfile(int retailerID, String proprietor, String shopName, String mobileNo
            , double longitude, double latitude
            , String cityName, String stateName, String countryName) {

        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("retailerId", String.valueOf(retailerID));
        headers.put("enterpriseName", shopName);
        headers.put("propritor", proprietor);
        headers.put("contactNo", mobileNo);
        headers.put("profilePhoto", "");
        headers.put("latLocation", String.valueOf(latitude));
        headers.put("longLocation", String.valueOf(longitude));
        headers.put("address", "");
        headers.put("city", cityName);
        headers.put("state", stateName);
        headers.put("country", countryName);
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

        try {
            jsonObject = new JSONObject();
            jsonObject.put("enterpriseName", shopName);
            jsonObject.put("propritor", proprietor);
            jsonObject.put("retailerId", retailerID);
            jsonObject.put("contactNo", mobileNo);
            jsonObject.put("profilePhoto", "");
            jsonObject.put("latLocation", String.valueOf(latitude));
            jsonObject.put("longLocation", String.valueOf(longitude));
            jsonObject.put("address", "");
            jsonObject.put("city", cityName);
            jsonObject.put("state", stateName);
            jsonObject.put("country", countryName);
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
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/update_retailer";
            sendRequest(databaseURL);
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
        headers.put("retailerId", "1675");
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
            jsonObject.put("retailerId", "1001");
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
    public void sendVerificationCode(int code, String mail, String password) {

        headers = new HashMap<>();
        headers.put("code", String.valueOf(code));
        headers.put("mail", mail);
        headers.put("password", password);
        headers.put("Content-Type", "application/json");
        try {
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/verifiication_complete";
            sendRequest(databaseURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isProfileDataComplete(String mail) {
        headers = new HashMap<>();
        headers.put("mail", mail);
        headers.put("Content-Type", "application/json");
        try {
            databaseURL = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com:6868/is_data_filled";
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
                    Log.e("j", j.toString());

                    //responseJSONObject = new JSONObject(response);
                    serverResponse.saveResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response_Error", databaseURL + " : " + error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }


}

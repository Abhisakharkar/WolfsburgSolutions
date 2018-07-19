package com.example.abhishek.work;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.ServerOperations.ImageUpload;
import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.LocationResponseListener.LocationResponse;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.LocationResponseListener.OnLocationResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class ProfileActivity extends AppCompatActivity implements
        View.OnClickListener,OnMapReadyCallback {

    private Context context;
    private GoogleMap mMap;

    public static final int LOC_PERM_REQ_CODE = 201;
    public static final int LOC_ENABLE_REQ_CODE = 202;
    public static final int STOARAGE_PERM_REQ_CODE = 203;
    public static final int CAMERA_REQ_CODE = 204;
    public static final int GALLERY_REQ_CODE = 205;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FrameLayout mFrameLayout;

    private FetchData fetchData;
    private Authentication authentication = new Authentication(ProfileActivity.this);
    private SendData sendData = new SendData(ProfileActivity.this);

    // ****************           NEW                 *******************
    private EditText proprietorEdittext, mobileNoEdittext, shopNameEdittext, addressLine1Edittext, licenseNoEdittext;
    private TextInputLayout proprietorLayout, mobileNoLayout, shopNameLayout, addressLine1Layout, licenseNoLayout;
    private TextView sublocalityTextView, localityTextView;
    private ImageView dpImageView, spImageView, lpImageView;
    private Button saveProfileBtn;
    private ImageButton locationChangeBtn;
    private RelativeLayout mRelativeLayout;

    //image dialog layouts
    private View dpLayout, spLayout, lpLayout;
    private ImageView dpDialogImageview, spDialogImageview, lpDialogImageview;
    private int retailerId;

    //booleans to check what is changed when saving
    private boolean proprietorBool = false, mobileNoBool = false, shopNameBool = false, addressLine1Bool = false,
            licenseNoBool = false, locationBool = false, spBool = false, dpBool = false, lpBool = false;
    private String imageRequestFrom = null;

    Bitmap licensePhotoBitmap, shopPhotoBitmap, profilePhotoBitmap;
    private String proprietor = "", address = "", shopName = "", mobileNo = "", licenseNo = "";
    private int localityId = 0, subLocality1Id = 0;
    private double longitude = 0, latitude = 0;
    private String locality, subLocality1;

    private LayoutInflater layoutInflater;

    // ****************           OLD              *******************
    //private String fname, lname, mob_no, address, shop_name, proprietor, category;
    private EditText mob_no_edit, address_edit, shop_name_edit, proprieter_edit;
    private Button save_profile_btn, shop_location_btn;
    private TextView shop_pic_textview, shop_license_pic_textview;
    private Spinner shop_category_spinner;
    private ImageButton imageButton;
    private ImageView shop_pic_imageview, shop_license_imageview;

    //location
    private String cityName = "", countryName = "", stateName = "";
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationResponse locationResponse;

    private boolean isShopPic = false;
    private boolean isShopLicensePic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;

        // *****************        NEW         ******************
        proprietorEdittext = (EditText) findViewById(R.id.profile_activity_proprietor_edittext_id);
        mobileNoEdittext = (EditText) findViewById(R.id.profile_activity_mobile_no_edittext_id);
        shopNameEdittext = (EditText) findViewById(R.id.profile_activity_shop_name_edittext_id);
        addressLine1Edittext = (EditText) findViewById(R.id.profile_activity_addline_1_edittext_id);
        licenseNoEdittext = (EditText) findViewById(R.id.profile_activity_license_no_edittext_id);
        proprietorLayout = (TextInputLayout) findViewById(R.id.profile_activity_proprietor_layout_id);
        mobileNoLayout = (TextInputLayout) findViewById(R.id.profile_activity_mobile_no_layout_id);
        shopNameLayout = (TextInputLayout) findViewById(R.id.profile_activity_shop_name_layout_id);
        addressLine1Layout = (TextInputLayout) findViewById(R.id.profile_activity_addline_1_layout_id);
        licenseNoLayout = (TextInputLayout) findViewById(R.id.profile_activity_license_no_layout_id);
        sublocalityTextView = (TextView) findViewById(R.id.profile_activity_sublocality_txtview_id);
        localityTextView = (TextView) findViewById(R.id.profile_activity_locality_textview_id);
        saveProfileBtn = (Button) findViewById(R.id.profile_activity_save_profile_btn_id);
        locationChangeBtn = (ImageButton) findViewById(R.id.profile_activity_location_btn_id);
        spImageView = (ImageView) findViewById(R.id.profile_activity_shop_imageview_id);
        dpImageView = (ImageView) findViewById(R.id.profile_activity_dp_imageview_id);
        mFrameLayout=(FrameLayout)findViewById(R.id.maps_frame_layout);
        mRelativeLayout=(RelativeLayout)findViewById(R.id.maps_layout);
        lpImageView = (ImageView) findViewById(R.id.profile_activity_license_photo_imageview_id);

        // *****************        OLD         ******************
        imageButton = (ImageButton) findViewById(R.id.pro_pic_btn_id);
        mob_no_edit = (EditText) findViewById(R.id.mob_no_edittext_id);
        address_edit = (EditText) findViewById(R.id.shop_address_edittext_id);
        shop_name_edit = (EditText) findViewById(R.id.shop_name_edittext_id);
        proprieter_edit = (EditText) findViewById(R.id.proprieter_edittext_id);
        //shop_pic_imageview = (ImageView) findViewById(R.id.shop_pic_view_id);
        //shop_license_imageview = (ImageView) findViewById(R.id.shop_license_pic_view_id);
        shop_category_spinner = (Spinner) findViewById(R.id.shop_category_spinner_id);
        save_profile_btn = (Button) findViewById(R.id.save_profile_btn_id);
        shop_location_btn = (Button) findViewById(R.id.shop_location_btn_id);
        shop_pic_textview = (TextView) findViewById(R.id.profile_activity_shop_photo_textview_id);
        shop_license_pic_textview = (TextView) findViewById(R.id.profile_activity_shop_license_photo_textview_id);

        save_profile_btn.setOnClickListener(this);
        shop_location_btn.setOnClickListener(this);
        shop_pic_textview.setOnClickListener(this);
        shop_license_pic_textview.setOnClickListener(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        fetchData = new FetchData(ProfileActivity.this);

         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check if data is saved previously
        boolean isDataFilled = sharedPreferences.getBoolean("isDataFilled", false);
        if (isDataFilled) {
            proprietor = sharedPreferences.getString("proprietor", "");
            mobileNo = sharedPreferences.getString("mobileNo", "");
            shopName = sharedPreferences.getString("shopName", "");
            address = sharedPreferences.getString("addLine1", "");
            latitude = Double.parseDouble(sharedPreferences.getString("latitude", ""));
            longitude = Double.parseDouble(sharedPreferences.getString("longitude", ""));
            localityId = sharedPreferences.getInt("localityId", 0);
            subLocality1Id = sharedPreferences.getInt("subLocality1Id", 0);
            locality = sharedPreferences.getString("locality", "");
            subLocality1 = sharedPreferences.getString("subLocality1", "");
            if (!locality.isEmpty()) {
                localityTextView.setText(locality);
            }
            if (!subLocality1.isEmpty()) {
                sublocalityTextView.setText(subLocality1);
            }
            licenseNo = sharedPreferences.getString("shopActLicenseNo", "");

            proprietorEdittext.setText(proprietor);
            mobileNoEdittext.setText(mobileNo);
            shopNameEdittext.setText(shopName);
            addressLine1Edittext.setText(address);
            licenseNoEdittext.setText(licenseNo);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            retailerId = sharedPreferences.getInt("retailerId", 0);
            try {
                //license photo
                String licensePhotoName = retailerId + ".lp.jpeg";
                File licensePhotoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", licensePhotoName);
                licensePhotoBitmap = BitmapFactory.decodeStream(new FileInputStream(licensePhotoFile), null, options);
                if (licensePhotoBitmap != null) {
                    lpImageView.setImageBitmap(licensePhotoBitmap);
                } else {
                    Log.e("license photo error", "license bitmap null");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String shopPhotoName = retailerId + ".sp.jpeg";
                File shopPhotoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", shopPhotoName);
                shopPhotoBitmap = BitmapFactory.decodeStream(new FileInputStream(shopPhotoFile), null, options);
                if (shopPhotoBitmap != null) {
                    spImageView.setImageBitmap(shopPhotoBitmap);
                } else {
                    Log.e("shop photo error", "shop bitmap null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String profilePhotoName = retailerId + ".dp.jpeg";
                File profilePhotoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", profilePhotoName);
                profilePhotoBitmap = BitmapFactory.decodeStream(new FileInputStream(profilePhotoFile), null, options);
                if (profilePhotoBitmap != null) {
                    dpImageView.setImageBitmap(profilePhotoBitmap);
                } else {
                    Log.e("profile photo error", "profile bitmap null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            proprietor = sharedPreferences.getString("proprietor", "");
            if (!proprietor.isEmpty()) {
                mobileNo = sharedPreferences.getString("mobileNo", "");
                shopName = sharedPreferences.getString("shopName", "");
                address = sharedPreferences.getString("shopAddress", "");
                cityName = sharedPreferences.getString("city", "");
                stateName = sharedPreferences.getString("state", "");
                countryName = sharedPreferences.getString("country", "");
                latitude = Double.parseDouble(sharedPreferences.getString("latitude", ""));
                longitude = Double.parseDouble(sharedPreferences.getString("longitude", ""));

                proprieter_edit.setText(proprietor);
                mob_no_edit.setText(mobileNo);
                shop_name_edit.setText(shopName);
                address_edit.setText(address);
            }
            //TODO Nothing
            //TODO get data for the first time
        }

        //SendData server response
        ServerResponse serverResponse = new ServerResponse();
        serverResponse = sendData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    JSONObject localityData = responseJSONObject.getJSONObject("localityData");
                    locality = localityData.getString("locality");
                    localityTextView.setText(locality);
                    editor.putString("locality", locality);
                    editor.putInt("localityId", localityData.getInt("localityId"));
                    editor.putBoolean("localityTier", localityData.getInt("tier") > 0);
                    editor.putBoolean("localityWholesaleTier", localityData.getInt("wholesaleTier") > 0);
                    editor.commit();
                    int length = responseJSONObject.getInt("length");
                    if (length == 2) {
                        JSONObject sublocality1Data = responseJSONObject.getJSONObject
                                ("subLocality1Data");
                        String sublocality = sublocality1Data.getString("subLocality1");
                        subLocality1 = sublocality;
                        sublocalityTextView.setText(subLocality1);
                        editor.putString("subLocality1", subLocality1);
                        editor.putInt("subLocality1Id", sublocality1Data.getInt("subLocality1Id"));
                        editor.putBoolean("subLocality1Tier", sublocality1Data.getInt("tier") > 0);
                        editor.putBoolean("subLocality1WholesaleTier", sublocality1Data.getInt("wholesaleTier") > 0);
                        editor.commit();
                    }

                    if (length == 3) {
                        JSONObject sublocality2Data = responseJSONObject.getJSONObject
                                ("subLocality2Data");
                        String sublocality2 = sublocality2Data.getString("subLocality2");
                        editor.putString("subLocality2", sublocality2);
                        editor.commit();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });


        //server response
        authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {

                    String responseFrom = responseJSONObject.getString("responseFrom");
                    if (responseFrom.equals("get_sublocality")) {

                        //implementation changed

                    } else if (responseFrom.equals("update_full_retailer_data")) {
                        boolean update = responseJSONObject.getBoolean("update");
                        if (update) {

                            editor.putString("proprietor", proprietor);
                            editor.putString("mobileNo", mobileNo);
                            editor.putString("shopName", shopName);
                            editor.putString("addLine1", address);
                            editor.putString("locality", locality);
                            editor.putString("subLocality1", subLocality1);
                            editor.putString("shopActLicenseNo", licenseNo);
                            editor.putString("longitude", String.valueOf(longitude));
                            editor.putString("latitude", String.valueOf(latitude));
                            editor.putBoolean("isDataFilled", true);
                            editor.commit();

                            Toast.makeText(context, "Profile Saved Successfully !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                            finish();

                        } else {
                            Toast.makeText(context, "Problem in saving profile !\nTry again later.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });


        //location
        locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
        locationResponse = new LocationResponse();
        locationResponse.setOnLocationResponseReceiveListener(new OnLocationResponseReceiveListener() {
            @Override
            public void onLocationResponseReceive(double longitude, double latitude) {
                try {
                    sendData.sendLatLoc(latitude, longitude);
                    //TODO process response

                    /*
                    Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    cityName = addresses.get(0).getLocality();
                    countryName = addresses.get(0).getCountryName();
                    stateName = addresses.get(0).getAdminArea();
                    editor.putString("city", cityName);
                    editor.putString("state", stateName);
                    editor.putString("country", countryName);
                    editor.commit();
                    */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.profile_activity_save_profile_btn_id:

                proprietor = proprietorEdittext.getText().toString();
                mobileNo = mobileNoEdittext.getText().toString();
                shopName = shopNameEdittext.getText().toString();
                licenseNo = licenseNoEdittext.getText().toString();
                address = addressLine1Edittext.getText().toString();
                latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
                longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
                localityId = sharedPreferences.getInt("localityId", 0);
                subLocality1Id = sharedPreferences.getInt("subLocality1Id", 0);


                if (!proprietor.isEmpty()) {
                    if (!mobileNo.isEmpty()) {
                        if (!licenseNo.isEmpty()) {
                            if (Patterns.PHONE.matcher(mobileNo).matches()) {
                                if (locality != null || subLocality1 != null) {
                                    if (!locality.isEmpty() || !subLocality1.isEmpty()) {

                                        //TODO save updated data from response of below
                                        //send data to server
                                        authentication.updateProfile(proprietor, shopName, mobileNo, Double.toString(longitude), Double.toString(latitude), address, licenseNo, Integer.toString(localityId), Integer.toString(subLocality1Id));
                                        //authentication.updateProfile("", "", proprietor, shopName,
                                        //mobileNo, longitude, latitude, address, cityName, stateName, countryName);
                                    }
                                } else {
                                    Toast.makeText(context, "Please set Location !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Check mobile number !", Toast.LENGTH_SHORT).show();
                                mobileNoLayout.setError("Check mobile number !");
                            }
                        } else {
                            Toast.makeText(context, "Enter License No !", Toast.LENGTH_SHORT).show();
                            licenseNoLayout.setError("Enter License No !");
                        }
                    } else {
                        Toast.makeText(context, "Please enter mobile number !", Toast.LENGTH_SHORT).show();
                        mobileNoLayout.setError("Enter mobile number !");
                    }
                } else {
                    Toast.makeText(context, "Please enter proprietor name !", Toast.LENGTH_SHORT).show();
                    proprietorLayout.setError("Enter proprietor name !");
                }
                break;

            case R.id.profile_activity_location_btn_id:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            getLocation();
                        } else {
                            showEnableGPSDialog();
                        }
                    } else {
                        String permissions[] = {"android.permission.ACCESS_FINE_LOCATION"};
                        requestPermissions(permissions, LOC_PERM_REQ_CODE);
                    }
                } else {
                    getLocation();
                }

                break;

            case R.id.profile_activity_dp_imageview_id:

                dpLayout = layoutInflater.inflate(R.layout.dialog_profile_dp, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                dpDialogImageview = (ImageView) dpLayout.findViewById(R.id.dialog_profile_dp_imageview_id);
                Button dpChangeBtn = (Button) dpLayout.findViewById(R.id.dialog_profile_dp_change_btn_id);
                if (profilePhotoBitmap != null) {
                    dpDialogImageview.setImageBitmap(profilePhotoBitmap);
                }
                dpChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageRequestFrom = "dp";
                        checkPermissionsAndGetImage();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        imageRequestFrom = null;
                    }
                });
                builder.setView(dpLayout);
                builder.create();
                builder.show();
                break;

            case R.id.profile_activity_shop_imageview_id:
                AlertDialog.Builder spBuilder = new AlertDialog.Builder(ProfileActivity.this);
                spLayout = layoutInflater.inflate(R.layout.dialog_profile_sp, null);
                spDialogImageview = (ImageView) spLayout.findViewById(R.id.dialog_profile_sp_imageview_id);
                if (shopPhotoBitmap != null) {
                    spDialogImageview.setImageBitmap(shopPhotoBitmap);
                }
                Button spChangeBtn = (Button) spLayout.findViewById(R.id.dialog_profile_sp_btn_id);
                spChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageRequestFrom = "sp";
                        checkPermissionsAndGetImage();
                    }
                });
                spBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        imageRequestFrom = null;
                    }
                });
                spBuilder.setView(spLayout);
                spBuilder.create();
                spBuilder.show();
                break;

            case R.id.profile_activity_license_photo_imageview_id:
                AlertDialog.Builder lpBuiler = new AlertDialog.Builder(ProfileActivity.this);
                lpLayout = layoutInflater.inflate(R.layout.dialog_profile_lp, null);
                lpDialogImageview = (ImageView) lpLayout.findViewById(R.id.dialog_profile_lp_imageview_id);
                if (licensePhotoBitmap != null) {
                    lpDialogImageview.setImageBitmap(licensePhotoBitmap);
                }
                Button lpChangeBtn = (Button) lpLayout.findViewById(R.id.dialog_profile_lp_btn_id);
                lpChangeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageRequestFrom = "lp";
                        checkPermissionsAndGetImage();
                    }
                });
                lpBuiler.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        imageRequestFrom = null;
                    }
                });
                lpBuiler.setView(lpLayout);
                lpBuiler.create();
                lpBuiler.show();
        }

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationRequest = new LocationRequest();
                locationRequest.setInterval(100);
                locationRequest.setFastestInterval(50);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

                fusedLocationProviderClient = new FusedLocationProviderClient(this);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();

                        editor.putString("longitude", String.valueOf(longitude));
                        editor.putString("latitude", String.valueOf(latitude));
                        editor.commit();

                        locationResponse.saveLocationResponse(longitude, latitude);
                        fusedLocationProviderClient.removeLocationUpdates(this);
                    }
                }, null);
            } else {
                showEnableGPSDialog();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String permissions[] = {"android.permission.ACCESS_FINE_LOCATION"};
                requestPermissions(permissions, LOC_PERM_REQ_CODE);
            }
        }
    }

    private void showEnableGPSDialog() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse locationSettingsResponse = task.getResult(ApiException.class);
                    //just get location now
                    if (task.isSuccessful()) {
                        getLocation();
                    } else {
                        AlertDialog.Builder gpsDalog = new AlertDialog.Builder(ProfileActivity.this);
                        gpsDalog.setMessage("Please enable GPS !");
                        gpsDalog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showEnableGPSDialog();
                            }
                        });
                        gpsDalog.setCancelable(false);
                        gpsDalog.create();
                        gpsDalog.show();
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            //show dialog
                            try {
                                resolvableApiException.startResolutionForResult(ProfileActivity.this, LOC_ENABLE_REQ_CODE);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            }
                    }

                }
            }
        });
    }

    private void checkPermissionsAndGetImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                requestPermissions(permissions, STOARAGE_PERM_REQ_CODE);
            } else if (ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                requestPermissions(permissions, STOARAGE_PERM_REQ_CODE);
            } else {
                showImageSelectDialog();
            }

        } else {
            showImageSelectDialog();
        }
    }

    private void showImageSelectDialog() {

        final String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(context, "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
                            getImage(0);
                        } else {
                            String[] cameraPermission = {"android.permission.CAMERA"};
                            requestPermissions(cameraPermission, CAMERA_REQ_CODE);
                        }
                    } else {
                        getImage(0);
                    }

                } else if (item == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                            getImage(1);
                        } else {
                            //show storage permissions request dialog
                        }
                    } else {
                        getImage(1);
                    }
                }
            }
        });
        builder.show();
    }

    private void getImage(int option) {
        if (option == 0) {
            Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //make sure there is camera activity to capture image
            if (captureImageIntent.resolveActivity(getPackageManager()) != null) {

                startActivityForResult(captureImageIntent, CAMERA_REQ_CODE);
            }
        } else if (option == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //for location
        if (requestCode == LOC_PERM_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //location permission granted
                getLocation();
            } else {
                //location permission not granted
                AlertDialog.Builder locationDialog = new AlertDialog.Builder(ProfileActivity.this);
                locationDialog.setMessage("Please give Location Permission !");
                locationDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String permissions[] = {"android.permission.ACCESS_FINE_LOCATION"};
                            requestPermissions(permissions, LOC_PERM_REQ_CODE);
                        }
                    }
                });
                locationDialog.create();
                locationDialog.show();
            }
        }

        if (requestCode == STOARAGE_PERM_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //storage permissions granted
                showImageSelectDialog();
            } else {
                //storage permission not granted
                AlertDialog.Builder storageDialog = new AlertDialog.Builder(ProfileActivity.this);
                storageDialog.setMessage("Please give Storage Permission !");
                storageDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                            requestPermissions(permissions, STOARAGE_PERM_REQ_CODE);
                        }
                    }
                });
                storageDialog.create();
                storageDialog.show();

            }
        }

        //camera permissions
        if (requestCode == CAMERA_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage(0);
            } else {
                AlertDialog.Builder cameraDialog = new AlertDialog.Builder(ProfileActivity.this);
                cameraDialog.setMessage("Please give Camera Permission !");
                cameraDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String[] permissions = {"android.permission.CAMERA"};
                            requestPermissions(permissions, CAMERA_REQ_CODE);
                        }
                    }
                });
                cameraDialog.create();
                cameraDialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOC_ENABLE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                //gps enabled
                getLocation();
            } else {
                //gps not enabled
                AlertDialog.Builder gpsDalog = new AlertDialog.Builder(ProfileActivity.this);
                gpsDalog.setMessage("Please enable GPS !");
                gpsDalog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showEnableGPSDialog();
                    }
                });
                gpsDalog.setCancelable(false);
                gpsDalog.create();
                gpsDalog.show();
            }
        }

        if (requestCode == CAMERA_REQ_CODE) {

            if (resultCode == RESULT_OK) {
                //photoBitmap = (Bitmap) data.getExtras().get("data");
                Bitmap tempBitmap = (Bitmap) data.getExtras().get("data");

                if (imageRequestFrom != null) {
                    if (imageRequestFrom.equals("dp")) {
                        profilePhotoBitmap = tempBitmap;
                        dpImageView.setImageBitmap(profilePhotoBitmap);
                        dpDialogImageview.setImageBitmap(profilePhotoBitmap);
                        int retailerId = sharedPreferences.getInt("retailerId", 0);
                        String photoName = String.valueOf(retailerId) + ".dp.jpeg";
                        saveImage(profilePhotoBitmap, photoName);
                    } else if (imageRequestFrom.equals("sp")) {
                        shopPhotoBitmap = tempBitmap;
                        spImageView.setImageBitmap(shopPhotoBitmap);
                        spDialogImageview.setImageBitmap(shopPhotoBitmap);
                        int retailerId = sharedPreferences.getInt("retailerId", 0);
                        String photoName = String.valueOf(retailerId) + ".sp.jpeg";
                        saveImage(shopPhotoBitmap, photoName);
                    } else if (imageRequestFrom.equals("lp")) {
                        licensePhotoBitmap = tempBitmap;
                        lpImageView.setImageBitmap(licensePhotoBitmap);
                        lpDialogImageview.setImageBitmap(licensePhotoBitmap);
                        int retailerId = sharedPreferences.getInt("retailerId", 0);
                        String photoName = String.valueOf(retailerId) + ".lp.jpeg";
                        saveImage(licensePhotoBitmap, photoName);
                    }
                }

                //set bitmap image to imageView
                //licenseImageview.setImageBitmap(photoBitmap);
            }

            if (requestCode == GALLERY_REQ_CODE) {

                if (resultCode == RESULT_OK) {
                    //get bitmap image
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePath[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap tempBitmap = (BitmapFactory.decodeFile(picturePath));


                    if (imageRequestFrom != null) {
                        if (imageRequestFrom.equals("dp")) {
                            profilePhotoBitmap = tempBitmap;
                            dpImageView.setImageBitmap(profilePhotoBitmap);
                            dpDialogImageview.setImageBitmap(profilePhotoBitmap);
                            int retailerId = sharedPreferences.getInt("retailerId", 0);
                            String photoName = String.valueOf(retailerId) + ".dp.jpeg";
                            saveImage(profilePhotoBitmap, photoName);
                        } else if (imageRequestFrom.equals("sp")) {
                            shopPhotoBitmap = tempBitmap;
                            spImageView.setImageBitmap(shopPhotoBitmap);
                            int retailerId = sharedPreferences.getInt("retailerId", 0);
                            String photoName = String.valueOf(retailerId) + ".sp.jpeg";
                            saveImage(shopPhotoBitmap, photoName);
                        } else if (imageRequestFrom.equals("lp")) {
                            licensePhotoBitmap = tempBitmap;
                            lpImageView.setImageBitmap(licensePhotoBitmap);
                            int retailerId = sharedPreferences.getInt("retailerId", 0);
                            String photoName = String.valueOf(retailerId) + ".lp.jpeg";
                            saveImage(licensePhotoBitmap, photoName);
                        }
                    }
                    //set photo to imageView
                    //licenseImageview.setImageBitmap(photoBitmap);
                }
            }
        }
    }

    private void saveImage(Bitmap photoBitmap, String photoName) {
        if (photoBitmap == null) {
            Log.e("saveImage Error", "photoBitmap is null");
        } else {

            //save image to local private file
            FileOutputStream fileOutputStream;
            try {
                if (!(new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images").exists())) {
                    File file = new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images");
                    file.mkdir();
                }

                //save photo to memory
                File photoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", photoName);
                fileOutputStream = new FileOutputStream(photoFile);
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();

                //send photo to server
                ImageUpload imageUpload = new ImageUpload(ProfileActivity.this);
                imageUpload.uploadImage(photoName, photoFile.getAbsolutePath().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if((longitude!=0 && latitude!=0) || longitude!=0 || latitude!=0){
            LatLng mylocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker in Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,15));
            mFrameLayout.setVisibility(View.INVISIBLE);
        }
        else{
            mFrameLayout.setVisibility(View.VISIBLE);
            mRelativeLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private void updateUI(boolean isNetworkAbailable) {
        if (!isNetworkAbailable) {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "connected to internet", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    //connected
                    updateUI(true);
                } else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}

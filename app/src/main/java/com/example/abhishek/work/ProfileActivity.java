package com.example.abhishek.work;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.LocationResponseListener.LocationResponse;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.LocationResponseListener.OnLocationResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements
        View.OnClickListener {

    private Context context;

    //private String fname, lname, mob_no, address, shop_name, proprietor, category;
    private EditText mob_no_edit, address_edit, shop_name_edit, password_edit, confirm_password_edit, proprieter_edit;
    private Button save_profile_btn, shop_location_btn, shop_pic_btn, shop_license_btn;
    private Spinner shop_category_spinner;
    private ImageButton imageButton;
    private ImageView shop_pic_imageview, shop_license_imageview;

    private String proprietor = "", address = "", shopName = "", mobileNo = "";

    private Authentication authentication = new Authentication(ProfileActivity.this);

    //location
    private String cityName, countryName, stateName;
    private double longitude = 0, latitude = 0;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationResponse locationResponse;

    public static final int LOC_PERM_REQ_CODE = 201;
    public static final int LOC_ENABLE_REQ_CODE = 202;
    public static final int STOARAGE_PERM_REQ_CODE = 203;
    public static final int CAMERA_REQ_CODE = 204;
    public static final int GALLERY_REQ_CODE = 205;

    private boolean isShopPic = false;
    private boolean isShopLicensePic = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;

        imageButton = (ImageButton) findViewById(R.id.pro_pic_btn_id);
        mob_no_edit = (EditText) findViewById(R.id.mob_no_edittext_id);
        address_edit = (EditText) findViewById(R.id.shop_address_edittext_id);
        shop_name_edit = (EditText) findViewById(R.id.shop_name_edittext_id);
        proprieter_edit = (EditText) findViewById(R.id.proprieter_edittext_id);
        shop_pic_imageview = (ImageView) findViewById(R.id.shop_pic_view_id);
        shop_license_imageview = (ImageView) findViewById(R.id.shop_license_pic_view_id);
        shop_category_spinner = (Spinner) findViewById(R.id.shop_category_spinner_id);
        save_profile_btn = (Button) findViewById(R.id.save_profile_btn_id);
        shop_location_btn = (Button) findViewById(R.id.shop_location_btn_id);
        shop_license_btn = (Button) findViewById(R.id.shop_license_photo_btn_id);
        shop_pic_btn = (Button) findViewById(R.id.shop_photo_btn_id);

        save_profile_btn.setOnClickListener(this);
        shop_location_btn.setOnClickListener(this);
        shop_pic_btn.setOnClickListener(this);
        shop_license_btn.setOnClickListener(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("userdata",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //location
        locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);
        locationResponse = new LocationResponse();
        locationResponse.setOnLocationResponseReceiveListener(new OnLocationResponseReceiveListener() {
            @Override
            public void onLocationResponseReceive(double longitude, double latitude) {
                try {
                    Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    cityName = addresses.get(0).getLocality();
                    countryName = addresses.get(0).getCountryName();
                    stateName = addresses.get(0).getAdminArea();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save_profile_btn_id:

                proprietor = proprieter_edit.getText().toString();
                mobileNo = mob_no_edit.getText().toString();
                shopName = shop_name_edit.getText().toString();

                if (!proprietor.isEmpty()) {
                    if (!mobileNo.isEmpty()) {
                        if (Patterns.PHONE.matcher(mobileNo).matches()) {
                            if (!cityName.isEmpty() || !countryName.isEmpty() || !stateName.isEmpty()) {

                                //save data to sharedPref
                                editor.putString("proprietor",proprietor);
                                editor.putString("mobileNo",mobileNo);
                                editor.putString("shopName",shopName);
                                editor.putString("longitude",String.valueOf(longitude));
                                editor.putString("latitude",String.valueOf(latitude));
                                editor.putString("city",cityName);
                                editor.putString("state",stateName);
                                editor.putString("country",countryName);
                                editor.commit();

                                //send data to server
                                authentication.sendUserProfile(proprietor, shopName, mobileNo, longitude, latitude, cityName, stateName, countryName);
                                authentication.serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
                                    @Override
                                    public void onResponseReceive(JSONObject responseJSONObject) {
                                        try {
                                            boolean result = responseJSONObject.getBoolean("result");
                                            if (result) {
                                                //go to home page
                                                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                //error .... login again
                                                Toast.makeText(context, "Please try again !", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Please set Location !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Check mobile number !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Please enter mobile number !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter proprietor name !", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.shop_location_btn_id:

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

            case R.id.shop_license_photo_btn_id:

                isShopLicensePic = true;
                isShopPic = false;

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

                break;

            case R.id.shop_photo_btn_id:

                isShopLicensePic = false;
                isShopPic = true;

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
                break;
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

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQ_CODE) {
                Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                //set bitmap image to imageView
                if (isShopPic == true && isShopLicensePic == false) {
                    shop_pic_imageview.setImageBitmap(photoBitmap);
                } else if (isShopPic == false && isShopLicensePic == true) {
                    shop_license_imageview.setImageBitmap(photoBitmap);
                }
                shop_pic_imageview.setImageBitmap(photoBitmap);
                //save to file
                FileOutputStream fileOutputStream = null;
                try {
                    if (!(new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images").exists())) {
                        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images");
                        file.mkdir();
                    }
                    //set photo name
                    String photoName = "";
                    if (isShopPic == true && isShopLicensePic == false) {
                        photoName = "shop_photo";
                    } else if (isShopPic == false && isShopLicensePic == true) {
                        photoName = "shop_license_photo";
                    }
                    //save photo to memory
                    File photoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", photoName + ".jpeg");
                    fileOutputStream = new FileOutputStream(photoFile);
                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == GALLERY_REQ_CODE) {
                //get bitmap image
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap photoBitmap = (BitmapFactory.decodeFile(picturePath));

                //set photo to imageView
                if (isShopPic == true && isShopLicensePic == false) {
                    shop_pic_imageview.setImageBitmap(photoBitmap);
                } else if (isShopPic == false && isShopLicensePic == true) {
                    shop_license_imageview.setImageBitmap(photoBitmap);
                }

                //store bitmap image in app internal memory
                FileOutputStream fileOutputStream;
                try {
                    if (!(new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images").exists())) {
                        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images");
                        file.mkdir();
                    }

                    String photoName = "";
                    if (isShopPic == true && isShopLicensePic == false) {
                        photoName = "shop_photo";
                    } else if (isShopPic == false && isShopLicensePic == true) {
                        photoName = "shop_license_photo";
                    }
                    //save photo to memory
                    File photoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", photoName + ".jpeg");
                    fileOutputStream = new FileOutputStream(photoFile);
                    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}

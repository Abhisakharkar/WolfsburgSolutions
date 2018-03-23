package com.example.abhishek.work;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.abhishek.work.SupportClasses.AndroidVersionChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements
        View.OnClickListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private Context context;

    //private String fname, lname, mob_no, address, shop_name, user_id, password, confirm_password, proprieter, category;
    private EditText fname_edit, lname_edit, mob_no_edit, address_edit, shop_name_edit, password_edit, confirm_password_edit, proprieter_edit;
    private Button save_profile_btn, shop_location_btn, shop_pic_btn, shop_license_btn;
    private Spinner shop_category_spinner;
    private ImageButton imageButton;
    private ImageView shop_pic_imageview, shop_license_imageview;

    //location
    private LocationManager locationManager;
    private double longitude;
    private double latitude;

    //GoogleApiClient
    private GoogleApiClient googleApiClient;

    public static final int LOC_PERM_REQ_CODE = 201;
    public static final int STOARAGE_PERM_REQ_CODE = 202;
    public static final int CAMERA_REQ_CODE = 203;
    public static final int GALLERY_REQ_CODE = 204;

    private boolean isShopPic = false;
    private boolean isShopLicensePic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;

        imageButton = (ImageButton) findViewById(R.id.pro_pic_btn_id);
        fname_edit = (EditText) findViewById(R.id.fname_edittext_id);
        lname_edit = (EditText) findViewById(R.id.lname_edittext_id);
        mob_no_edit = (EditText) findViewById(R.id.mob_no_edittext_id);
        address_edit = (EditText) findViewById(R.id.shop_address_edittext_id);
        shop_name_edit = (EditText) findViewById(R.id.shop_name_edittext_id);
        password_edit = (EditText) findViewById(R.id.password_edittext_profile_id);
        confirm_password_edit = (EditText) findViewById(R.id.confirm_password_edittext_profile_id);
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


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        //location
        locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save_profile_btn_id:
                break;

            case R.id.shop_location_btn_id:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                        String permissions[] = {"android.permission.ACCESS_FINE_LOCATION"};
                        requestPermissions(permissions, LOC_PERM_REQ_CODE);
                    } else {
                        getLocation();
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
                            requestPermissions(cameraPermission,CAMERA_REQ_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                try{
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
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
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
            }
        }

        if (requestCode == STOARAGE_PERM_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //storage permissions granted
                showImageSelectDialog();
            } else {
                //storage permission not granted
            }
        }

        //camera permissions
        if (requestCode == CAMERA_REQ_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getImage(0);
            }
        }

    }


    private void getLocation() {

        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {

                final int locationPermission = ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (locationPermission == PackageManager.PERMISSION_GRANTED) {

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        final LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setInterval(100);
                        locationRequest.setFastestInterval(50);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

                        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();
                        locationSettingsRequestBuilder.addLocationRequest(locationRequest);
                        LocationSettingsRequest locationSettingsRequest = builder.build();

                        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
                        settingsClient.checkLocationSettings(locationSettingsRequest);

                        final FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location = locationResult.getLastLocation();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Toast.makeText(context, "latitude : " + latitude + "\n" + "longitude : " + longitude, Toast.LENGTH_SHORT).show();
                                fusedLocationProviderClient.removeLocationUpdates(this);
                            }
                        }, null);

                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle("Enable location")
                                .setMessage("Please enable location")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                }
            }
        }
    }
}

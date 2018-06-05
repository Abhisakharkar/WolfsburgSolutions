package com.example.abhishek.work;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.abhishek.work.ProfileActivity.CAMERA_REQ_CODE;
import static com.example.abhishek.work.ProfileActivity.GALLERY_REQ_CODE;
import static com.example.abhishek.work.ProfileActivity.STOARAGE_PERM_REQ_CODE;

public class LicensePhotoActivity extends AppCompatActivity {

    private Context context;
    private ImageView licenseImageview;
    private Button saveImageBtn, editImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_photo);

        licenseImageview = (ImageView) findViewById(R.id.license_photo_activity_imageview_id);
        saveImageBtn = (Button) findViewById(R.id.license_photo_activity_save_btn_id);
        editImageBtn = (Button) findViewById(R.id.license_photo_activity_edit_btn_id);

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void showImageSelectDialog() {

        final String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(LicensePhotoActivity.this);
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

        if (requestCode == STOARAGE_PERM_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //storage permissions granted
                showImageSelectDialog();
            } else {
                //storage permission not granted
                AlertDialog.Builder storageDialog = new AlertDialog.Builder(LicensePhotoActivity.this);
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
                AlertDialog.Builder cameraDialog = new AlertDialog.Builder(LicensePhotoActivity.this);
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

        if (requestCode == CAMERA_REQ_CODE) {

            if (resultCode == RESULT_OK) {
                Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");

                //set bitmap image to imageView
                licenseImageview.setImageBitmap(photoBitmap);
                //save to file
                FileOutputStream fileOutputStream = null;
                try {
                    if (!(new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images").exists())) {
                        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images");
                        file.mkdir();
                    }
                    //set photo name
                    String photoName = "shop_license_photo";

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
                Bitmap photoBitmap = (BitmapFactory.decodeFile(picturePath));

                //set photo to imageView
                licenseImageview.setImageBitmap(photoBitmap);

                //store bitmap image in app internal memory
                FileOutputStream fileOutputStream;
                try {
                    if (!(new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images").exists())) {
                        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/images");
                        file.mkdir();
                    }

                    //set name
                    String photoName = "shop_license_photo";

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

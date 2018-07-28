package com.example.abhishek.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;

import org.json.JSONObject;

public class ProductEditActivity extends AppCompatActivity {

    private EditText priceEdittext,commentEdittext,descriptionEdittext;
    private Switch star,availability;
    private Button addBtn;

    private String priceTxt,commentTxt,descriptionTxt;
    private int isStar,isAvailable;

    private LocalDatabaseHelper databaseHelper;
    private ItemData itemData;
    private Context context;
    private SendData sendData;
    private ServerResponse serverResponse;

    private String name,photo;
    private double price,selling_price;
    private int attribute_set_id,productID,retailerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        context = this;
        databaseHelper = new LocalDatabaseHelper(context);
        sendData = new SendData(context);
        serverResponse = sendData.getServerResponseInstance();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        price = intent.getDoubleExtra("price",-1.0);
        attribute_set_id = intent.getIntExtra("attribute_set_id",-1);
        productID = intent.getIntExtra("productID",-1);
        photo = intent.getStringExtra("photo");

        Log.e("productEdit name",name);

        priceEdittext = (EditText) findViewById(R.id.product_edit_activity_selling_price_edittext_id);
        commentEdittext = (EditText) findViewById(R.id.product_edit_activity_comment_edittext_id);
        descriptionEdittext = (EditText) findViewById(R.id.product_edit_activity_descriptio_edittext_id);
        star = (Switch) findViewById(R.id.product_edit_activity_star_swict_id);
        availability = (Switch) findViewById(R.id.product_edit_activity_availability_swict_id);
        addBtn = (Button) findViewById(R.id.product_edit_activity_add_btn_id);
        if(productID!=-1) {
            ItemData tempItemData = databaseHelper.getProduct(productID);
            if (tempItemData.getProductID() == -1){
                priceEdittext.setText(String.valueOf(price));
            }else {
                priceEdittext.setText(String.valueOf(tempItemData.getSellingPrice()));
                commentEdittext.setText(tempItemData.getComment());
                descriptionEdittext.setText(tempItemData.getDescription());
                star.setChecked(tempItemData.getStar()>0);
                availability.setChecked(tempItemData.getAvailability()>0);
            }
        }



        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                Log.e("product add response",responseJSONObject.toString());
                try {
                    if (responseJSONObject.getBoolean("insertSuccess")){
                        finish();
                    }else {
                        Toast.makeText(context, "Problem in adding product to shop !", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseErrorReceive(String msg) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceTxt = priceEdittext.getText().toString();
                if (priceTxt.isEmpty()){
                    selling_price = price;
                }else {
                    selling_price = Double.parseDouble(priceTxt);
                }
                commentTxt = commentEdittext.getText().toString();
                descriptionTxt = descriptionEdittext.getText().toString();
                if (star.isChecked()){
                    isStar = 1;
                }else {
                    isStar = 0;
                }
                if (availability.isChecked()){
                    isAvailable = 1;
                }else {
                    isAvailable = 0;
                }

                //now add this product to local and server database

                //adding to local database
                itemData = new ItemData();
                itemData.setProductID(productID);
                itemData.setPrice(price);
                itemData.setName(name);
                itemData.setAttribute_set_id(attribute_set_id);
                itemData.setPhoto(photo);
                //chech if input fields are not blank then add following
                itemData.setComment(commentTxt);
                itemData.setDescription(descriptionTxt);
                itemData.setSellingPrice(selling_price);
                itemData.setStar(isStar);
                itemData.setAvailability(isAvailable);

                //check if item is already present
                ItemData tempItemData = databaseHelper.getProduct(productID);
                if (tempItemData.getProductID() == -1){
                    //item already present
                    //update existing item
                    databaseHelper.insertItem(itemData);
                    //sending product data to server
                    sendData.addProductToShop(String.valueOf(productID)
                            ,String.valueOf(priceTxt),descriptionTxt,isAvailable,isStar,commentTxt);
                }else {
                    //item not present
                    //add this new item
                    databaseHelper.updateProduct(itemData);
                    //sending product data to server
                    sendData.updateProductToShop(String.valueOf(productID)
                            ,String.valueOf(priceTxt),descriptionTxt,isAvailable,isStar,commentTxt);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    private void updateUI(boolean isNetworkAbailable){
        if (!isNetworkAbailable){
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }else {
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
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    //connected
                    updateUI(true);
                }else {
                    //not connected
                    updateUI(false);
                }
            }
        }
    };
}

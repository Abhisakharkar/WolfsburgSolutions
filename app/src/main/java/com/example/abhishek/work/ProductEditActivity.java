package com.example.abhishek.work;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;

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
    private double price;
    private int attribute_set_id,productID,retailerID;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        context = this;
        databaseHelper = new LocalDatabaseHelper(context);
        sendData = new SendData(context);
        serverResponse = sendData.getServerResponseInstance();

        sharedPreferences = getApplicationContext().getSharedPreferences("userdata",MODE_PRIVATE);
        retailerID = sharedPreferences.getInt("retailerID",1);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        price = intent.getDoubleExtra("price",-1.0);
        attribute_set_id = intent.getIntExtra("attribute_set_id",-1);
        productID = intent.getIntExtra("productID",-1);
        photo = intent.getStringExtra("photo");

        priceEdittext = (EditText) findViewById(R.id.product_edit_activity_selling_price_edittext_id);
        commentEdittext = (EditText) findViewById(R.id.product_edit_activity_comment_edittext_id);
        descriptionEdittext = (EditText) findViewById(R.id.product_edit_activity_descriptio_edittext_id);
        star = (Switch) findViewById(R.id.product_edit_activity_star_swict_id);
        availability = (Switch) findViewById(R.id.product_edit_activity_availability_swict_id);
        addBtn = (Button) findViewById(R.id.product_edit_activity_add_btn_id);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceTxt = priceEdittext.getText().toString();
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
                itemData.setSellingPrice(Double.parseDouble(priceTxt));
                itemData.setStar(isStar);
                itemData.setAvailability(isAvailable);
                //
                databaseHelper.insertItem(itemData);

                //sending product data to server
                sendData.addProductToShop(String.valueOf(retailerID),String.valueOf(productID)
                        ,String.valueOf(priceTxt),descriptionTxt,isAvailable,isStar,commentTxt);

            }
        });
    }
}

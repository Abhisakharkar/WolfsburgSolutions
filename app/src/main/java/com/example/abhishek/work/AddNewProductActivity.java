package com.example.abhishek.work;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddNewProductActivity extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        spinner = (Spinner) findViewById(R.id.add_new_product_activity_spinner_1_id);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this
                ,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


    }
}

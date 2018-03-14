package com.example.abhishek.work;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.adapter.ItemsListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private Context context;
    private AppBarLayout appBarLayout;
    private TextView shopNametxt, openCloseTxt, tempTextView;
    private Switch openCloseSwitch;
    private ItemData itemData;
    private FloatingActionButton fab;

    //Recycler View
    private RecyclerView recyclerView;
    private ItemsListAdapter myListAdapter;
    private ArrayList<ItemData> arrayList;
    private RecyclerView.LayoutManager layoutManager;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        //initialize ui components
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutId);
        shopNametxt = (TextView) findViewById(R.id.shopNameTextviewId);
        openCloseTxt = (TextView) findViewById(R.id.openCloseTextviewId);
        openCloseSwitch = (Switch) findViewById(R.id.openCloseBtnId);
        recyclerView = (RecyclerView) findViewById(R.id.itemslist_recyclerview_id);
        fab = (FloatingActionButton) findViewById(R.id.new_item_fab_id);

        // Make image blur and set as collapsing toolbar Background
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.temp_toolbar_background);
        BlurBuilder blurBuilder = new BlurBuilder();
        Bitmap newImg = blurBuilder.blur(this, bitmap);
        Drawable image = new BitmapDrawable(getResources(), newImg);
        img = (ImageView) findViewById(R.id.collapsingToolbarImageViewId);
        img.setImageDrawable(image);

        //Fade in/out effect for ShopNameText,SwitchBtn,Open/CloseText
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int percent = (appBarLayout.getTotalScrollRange() / 2) + verticalOffset;
                float alpha = (float) ((float) percent / 144);
                shopNametxt.setAlpha(alpha);
                openCloseTxt.setAlpha(alpha);
                openCloseSwitch.setAlpha(alpha);
            }
        });



        //Shop Open/Close switch click listner
        openCloseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    openCloseTxt.setText("Open");
                } else {
                    openCloseTxt.setText("Closed");
                }
            }
        });

        //fab temporary implementation
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });

        arrayList = new ArrayList<ItemData>();

        ItemData i1 = new ItemData();
        i1.setName("item1");
        i1.setCategory("book");
        i1.setStar(true);
        i1.setAvailable(true);
        i1.setMrp("Rs.500");
        i1.setSellingPrice("Rs.350");

        arrayList.add(i1);

        ItemData i3 = new ItemData();
        i3.setName("item3");
        i3.setCategory("cloth");
        i3.setStar(true);
        i3.setAvailable(false);
        i3.setMrp("Rs.1200");
        i3.setSellingPrice("Rs.800");

        arrayList.add(i3);

        ItemData i2 = new ItemData();
        i2.setName("item2");
        i2.setCategory("electronics");
        i2.setStar(false);
        i2.setAvailable(true);
        i2.setMrp("Rs.1000");
        i2.setSellingPrice("Rs.950");

        arrayList.add(i2);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new ItemsListAdapter(arrayList);
        recyclerView.setAdapter(myListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
















package com.example.abhishek.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.SupportClasses.BlurBuilder;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;
import com.example.abhishek.work.adapters.ItemsListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ProcessingInstruction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView img;
    private Context context;
    private AppBarLayout appBarLayout;
    private TextView shopNametxt, openCloseTxt, tempTextView;
    private Switch openCloseSwitch;
    private ItemData itemData;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private String shopName;
    //Recycler View
    private RecyclerView recyclerView;
    private ItemsListAdapter myListAdapter;
    private ArrayList<ItemData> arrayList;
    private RecyclerView.LayoutManager layoutManager;

    //server
    private Authentication authentication;

    //local database
    private LocalDatabaseHelper databaseHelper;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        authentication = new Authentication(context);

        //initialize ui components
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutId);
        shopNametxt = (TextView) findViewById(R.id.shopNameTextviewId);
        openCloseTxt = (TextView) findViewById(R.id.openCloseTextviewId);
        openCloseSwitch = (Switch) findViewById(R.id.openCloseBtnId);
        recyclerView = (RecyclerView) findViewById(R.id.itemslist_recyclerview_id);
        fab = (FloatingActionButton) findViewById(R.id.new_item_fab_id);
        navigationView = (NavigationView) findViewById(R.id.home_activity_navigation_view_id);

        // Make image blur and set as collapsing toolbar Background
        /*
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable
                .temp_toolbar_background);
        BlurBuilder blurBuilder = new BlurBuilder();
        Bitmap newImg = blurBuilder.blur(this, bitmap);
        Drawable image = new BitmapDrawable(getResources(), newImg);
        img = (ImageView) findViewById(R.id.collapsingToolbarImageViewId);
        img.setImageDrawable(image);
        */
        shopName=sharedPreferences.getString("shopName","Not Found");
        shopNametxt.setText(shopName);
        shopNametxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
        shopNametxt.setTextColor(getResources().getColor(R.color.colorWhite));

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
                    openCloseTxt.setText("Open Now");
                } else {
                    openCloseTxt.setText("Closed Now");
                }
            }
        });

        //fab temporary implementation
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewCategoryActivity.class);
                intent.putExtra("activityCalledFrom","home");
                intent.putExtra("level",1);
                intent.putExtra("parent_id",0);
                startActivity(intent);
            }
        });

        arrayList = new ArrayList<ItemData>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new ItemsListAdapter(context, arrayList);
        recyclerView.setAdapter(myListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //navigation draver implementation
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.home_nav_menu_home_id) {

                } else if (itemId == R.id.home_nav_menu_manageTiming_id) {

                    startActivity(new Intent(HomeActivity.this, ShopTimingActivity.class));
                } else if (itemId == R.id.home_nav_menu_manageDelivery_id){

                    startActivity(new Intent(HomeActivity.this,DeliverySettingsActivity.class));
                }else if (itemId == R.id.home_nav_menu_profile_id) {

                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                } else if (itemId == R.id.home_nav_menu_orders_id) {

                    startActivity(new Intent(HomeActivity.this, OrdersActivity.class));

                } else if (itemId == R.id.home_nav_menu_membership_id) {

                } else if (itemId == R.id.home_nav_menu_advertise_id) {

                } else if (itemId == R.id.home_nav_menu_contact_id) {

                    startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
                }else if (itemId == R.id.home_nav_menu_logout_id){
                    clearAppData();
                    Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        //local database
        databaseHelper = new LocalDatabaseHelper(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        arrayList.clear();
        int productsCount = databaseHelper.getProductesCount();
        if (productsCount > 0) {
            Log.e("all products ...|", databaseHelper.getAllProducts().get(0).getName() + "| ... ");
            arrayList.addAll(databaseHelper.getAllProducts());
            myListAdapter.notifyDataSetChanged();
        }
    }

    private void clearAppData(){
        //clearing shared pref data
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userdata",MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();

        //clear databases
        getApplicationContext().deleteDatabase("ProductsData");

        //clear cashe data
        File cashe = getApplicationContext().getCacheDir();
        File appDir = new File(cashe.getParent());
        if (appDir.exists()){
            String[] appDirChildren = appDir.list();
            for (String s : appDirChildren){
                if (!s.equals("lib")){
                    deleteDir(new File(appDir,s));
                }
            }
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
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
            Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "connected to internet", Toast.LENGTH_SHORT).show();
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
















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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.ServerOperations.Authentication;
import com.example.abhishek.work.ServerOperations.FetchData;
import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.BlurBuilder;
import com.example.abhishek.work.SupportClasses.CustomAttributesParser;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.OnResponseReceiveListener;
import com.example.abhishek.work.SupportClasses.CustomEventListeners.ServerResponseListener.ServerResponse;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;
import com.example.abhishek.work.adapters.ItemsListAdapter;
import com.example.abhishek.work.SupportClasses.ImageConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ProcessingInstruction;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

//import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    private ImageView img, navigationProfilePic;
    private Context context;
    private AppBarLayout appBarLayout;
    private TextView shopNametxt, openCloseTxt, tempTextView, navigationProprietorText;
    private Switch openCloseSwitch;
    private ItemData itemData;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private String shopName, locality, subLocality1, subLocality2, proprietor;
    private double latitude, longitude;
    private DrawerLayout drawerLayout;
    //Recycler View
    private RecyclerView recyclerView;
    private int retailerId;
    private ItemsListAdapter myListAdapter;
    private ArrayList<ItemData> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    private View headerView;

    //server
    private Authentication authentication;
    private FetchData fetchData;
    private ServerResponse fetchDataServerResponse;

    //local database
    private LocalDatabaseHelper databaseHelper;

    public SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SendData sendData = new SendData(HomeActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = HomeActivity.this;
        sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authentication = new Authentication(context);
        fetchData = new FetchData(context);
        fetchDataServerResponse = fetchData.getServerResponseInstance();

        //local database
        databaseHelper = new LocalDatabaseHelper(context);

        //initialize ui components
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayoutId);
        shopNametxt = (TextView) findViewById(R.id.shopNameTextviewId);
        openCloseTxt = (TextView) findViewById(R.id.openCloseTextviewId);
        openCloseSwitch = (Switch) findViewById(R.id.openCloseBtnId);
        recyclerView = (RecyclerView) findViewById(R.id.itemslist_recyclerview_id);
        fab = (FloatingActionButton) findViewById(R.id.new_item_fab_id);
        navigationView = (NavigationView) findViewById(R.id.home_activity_navigation_view_id);
        headerView = navigationView.getHeaderView(0);
        navigationProprietorText = (TextView) headerView.findViewById(R.id.nav_header_name_textview_id);
        proprietor = sharedPreferences.getString("proprietor", "");
        drawerLayout=findViewById(R.id.drawerLayoutId);
        if (!proprietor.isEmpty()) {
            navigationProprietorText.setText(proprietor);
        }
        navigationProfilePic=(CircleImageView) headerView.findViewById(R.id.nav_header_profile_pic_id);
        // Make image blur and set as collapsing toolbar Background
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        retailerId = sharedPreferences.getInt("retailerId", 0);
        BlurBuilder blurBuilder = new BlurBuilder();
        try {
            String shopPhotoName = retailerId + ".sp.jpeg";
            File shopPhotoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", shopPhotoName);
            Bitmap shopPhotoBitmap = BitmapFactory.decodeStream(new FileInputStream(shopPhotoFile), null, options);
            Bitmap newImg = blurBuilder.blur(this, shopPhotoBitmap);
            Drawable image = new BitmapDrawable(getResources(), newImg);
            img = (ImageView) findViewById(R.id.collapsingToolbarImageViewId);
            img.setImageDrawable(image);
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.temp_toolbar_background);
            Bitmap newImg = blurBuilder.blur(this, bitmap);
            Drawable image = new BitmapDrawable(getResources(), newImg);
            img = (ImageView) findViewById(R.id.collapsingToolbarImageViewId);
            img.setImageDrawable(image);
        }
        try {
            String profilePhotoName = retailerId + ".dp.jpeg";
            File profilePhotoFile = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/images", profilePhotoName);
            Bitmap profilePhotoBitmap = BitmapFactory.decodeStream(new FileInputStream(profilePhotoFile), null, options);
            if (profilePhotoBitmap != null) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(profilePhotoBitmap, 100);
                navigationProfilePic.setImageBitmap(circularBitmap);
            } else {
                Log.e("profile photo error", "profile bitmap null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        shopName = sharedPreferences.getString("shopName", "Not Found");
        shopNametxt.setText(shopName);
        shopNametxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        shopNametxt.setTextColor(getResources().getColor(R.color.colorWhite));
        setOpenCloseTextAndSwitch();
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        locality = sharedPreferences.getString("locality", "");
        Log.e("value", "onCreate: " + latitude + " longitude" + longitude);
        if (latitude != 0 && longitude != 0) {
            String check = sharedPreferences.getString("locality", "");
            if (check.isEmpty()) {
                sendData.sendLatLoc(latitude, longitude);
            }
        }
        ServerResponse serverResponse = new ServerResponse();
        serverResponse = sendData.getServerResponseInstance();
        serverResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    Log.e("response object", "onResponseReceive: " + responseJSONObject);
                    JSONObject localityData = responseJSONObject.getJSONObject("localityData");
                    locality = localityData.getString("locality");
                    editor.putString("locality", locality);
                    editor.putInt("localityId", localityData.getInt("localityId"));
                    editor.putBoolean("localityTier", localityData.getInt("tier") > 0);
                    editor.putBoolean("localityWholesaleTier", localityData.getInt("wholesaleTier") > 0);
                    editor.commit();
                    int length = responseJSONObject.getInt("length");
                    if (length> 1) {
                        JSONObject sublocality1Data = responseJSONObject.getJSONObject("subLocality1Data");
                        subLocality1 = sublocality1Data.getString("subLocality1");
                        editor.putString("subLocality1", subLocality1);
                        editor.putInt("subLocality1Id", sublocality1Data.getInt("subLocality1Id"));
                        editor.putBoolean("subLocality1Tier", sublocality1Data.getInt("tier") > 0);
                        editor.putBoolean("subLocality1WholesaleTier", sublocality1Data.getInt("wholesaleTier") > 0);
                        editor.commit();
                    }

                    if (length >2) {
                        JSONObject sublocality2Data = responseJSONObject.getJSONObject("subLocality2Data");
                        subLocality2 = sublocality2Data.getString("subLocality2");
                        editor.putString("subLocality2", subLocality2);
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
                if (sharedPreferences.getInt("locationVerified",0)>0){
                    int temp;
                    if(isChecked){
                        temp=1;
                    }else {
                        temp=0;
                    }
                    sendData.updateShopStatusInManual(temp);
                    editor.putBoolean("currentState",isChecked);
                    editor.commit();
                    setOpenCloseTextAndSwitch();
                }else {
                    openCloseSwitch.setChecked(false);
                    Toast.makeText(context, "You can open your shop after verification", Toast.LENGTH_LONG).show();
                }

            }
        });

        //fab temporary implementation
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewCategoryActivity.class);
                intent.putExtra("activityCalledFrom", "home");
                intent.putExtra("level", 1);
                intent.putExtra("parent_id", 0);
                startActivity(intent);
            }
        });

        arrayList = new ArrayList<ItemData>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myListAdapter = new ItemsListAdapter(context, arrayList,databaseHelper);
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
                } else if (itemId == R.id.home_nav_menu_manageDelivery_id) {

                    startActivity(new Intent(HomeActivity.this, DeliverySettingsActivity.class));
                } else if (itemId == R.id.home_nav_menu_profile_id) {

                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                } else if (itemId == R.id.home_nav_menu_orders_id) {

                    startActivity(new Intent(HomeActivity.this, OrdersActivity.class));

                } else if (itemId == R.id.home_nav_menu_membership_id) {

                } else if (itemId == R.id.home_nav_menu_advertisment_id) {
                    startActivity(new Intent(HomeActivity.this,ApplyVerificationActivity.class));

                } else if (itemId == R.id.home_nav_menu_contact_id) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "wolfsburgproject@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/query");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                    //startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
                } else if (itemId == R.id.home_nav_menu_logout_id) {
                    clearAppData();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        fetchDataServerResponse.setOnResponseReceiveListener(new OnResponseReceiveListener() {
            @Override
            public void onResponseReceive(JSONObject responseJSONObject) {
                try {
                    String responseFrom = "";
                    responseFrom = responseJSONObject.getString("responseFrom");
                    if (responseFrom.equals("display_products_associated_with_retailer_id")) {
                        JSONArray products = responseJSONObject.getJSONArray("items");
                        for (int i = 0; i < products.length(); i++) {
                            ItemData itemData = new ItemData();
                            JSONObject tmpProduct = (JSONObject) products.get(i);
                            itemData.setProductID(tmpProduct.getInt("productId"));
                            itemData.setSellingPrice(tmpProduct.getInt("price"));
                            itemData.setDescription(tmpProduct.getString("description"));
                            itemData.setAvailability(tmpProduct.getInt("availability"));
                            itemData.setStar(tmpProduct.getInt("star"));
                            itemData.setComment(tmpProduct.getString("textField"));

                            databaseHelper.insertItem(itemData);
                        }
                        arrayList.clear();
                        if (databaseHelper.getProductesCount() > 0) {
                            arrayList.addAll(databaseHelper.getAllProducts());

                            String ids = "";
                            ids = String.valueOf(arrayList.get(0).getProductID());
                            for (int i = 1; i < arrayList.size(); i++) {
                                ids = ids + "," + String.valueOf(arrayList.get(i).getProductID());
                            }
                            fetchData.getProductDetailsForDatabase(ids);
                        }
                    } else if (responseFrom.equals("magento_get_product_with_ids")) {
                        JSONArray items = responseJSONObject.getJSONArray("items");
                        CustomAttributesParser customAttributesParser = new CustomAttributesParser();
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject tmpItem = items.getJSONObject(i);
                            String name = tmpItem.getString("name");
                            int productId = tmpItem.getInt("id");
                            int attribute_set_id = tmpItem.getInt("attribute_set_id");
                            int mrp = tmpItem.getInt("price");

                            JSONArray customAttr = tmpItem.getJSONArray("custom_attributes");
                            String url = customAttributesParser.getImageUrl(customAttr);
                            databaseHelper.updateProductDetails(productId, name, url, attribute_set_id, mrp);
                        }

                        arrayList.clear();
                        if (databaseHelper.getProductesCount() > 0){
                            arrayList.addAll(databaseHelper.getAllProducts());
                            myListAdapter.notifyDataSetChanged();
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
        } else {
            fetchData.getProductsDatabase();
        }
    }


    private void clearAppData() {
        //clearing shared pref data
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userdata", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();

        //clear databases
        getApplicationContext().deleteDatabase("ProductsData");

        //clear cashe data
        File cashe = getApplicationContext().getCacheDir();
        File appDir = new File(cashe.getParent());
        if (appDir.exists()) {
            String[] appDirChildren = appDir.list();
            for (String s : appDirChildren) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
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
    private void setOpenCloseTextAndSwitch(){
        openCloseSwitch.setChecked(sharedPreferences.getBoolean("currentState",false));
        if (openCloseSwitch.isChecked()) {
            openCloseTxt.setText("Open Now");
        } else {
            openCloseTxt.setText("Closed Now");
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void updateUI(boolean isNetworkAbailable) {
        if (!isNetworkAbailable) {
            Toast.makeText(context, "no internet connection", Toast.LENGTH_SHORT).show();
        } else {
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
















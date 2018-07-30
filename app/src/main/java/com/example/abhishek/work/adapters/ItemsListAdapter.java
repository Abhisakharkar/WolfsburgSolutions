package com.example.abhishek.work.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.R;
import com.example.abhishek.work.ServerOperations.SendData;
import com.example.abhishek.work.SupportClasses.LocalDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ItemsListViewHolder> {

    private List<ItemData> itemList = new ArrayList<ItemData>();
    private int position;
    private Context context;
    private SendData sendData;
    private LocalDatabaseHelper databaseHelper;

    public ItemsListAdapter(Context context, List<ItemData> itemList,LocalDatabaseHelper databaseHelper) {
        this.itemList = itemList;
        this.context = context;
        sendData = new SendData(context);
        this.databaseHelper = databaseHelper;
    }

    @Override
    public ItemsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.items_list_row, parent, false);
        ItemsListViewHolder viewHolder = new ItemsListViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemsListViewHolder holder, int position) {

        this.position = position;

        holder.itemNameTextView.setText(itemList.get(position).getName());
        Log.e("name  ...|", itemList.get(position).getName() + "| ... ");
        holder.itemMRPTextView.setText(String.valueOf(itemList.get(position).getPrice()));
        holder.itemSellingPriceEditText.setText(String.valueOf(itemList.get(position).getSellingPrice()));
        String url = "http://ec2-13-59-88-132.us-east-2.compute.amazonaws.com/magento/pub/media/catalog/product" + itemList.get(position).getPhoto();
        if (!url.equals("0") && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .into(holder.itemImageView);
        }
        if (databaseHelper.getProduct(itemList.get(position).getProductID()).getAvailability() == 1){
            holder.itemAvailabilityBtn.setSelected(true);
            holder.itemAvailabilityBtn.setChecked(true);
            holder.itemNotAvailableFrameLayout.setVisibility(View.INVISIBLE);
            //set other elements as enabled
            holder.itemImageView.setClickable(true);
            holder.itemNameTextView.setClickable(true);
            //itemCategoryTextView.setClickable(true);
            holder.itemSellingPriceEditText.setClickable(true);
            holder.itemMRPTextView.setClickable(true);
            holder.itemStarImageBtn.setClickable(true);
            //promoteItemBtn.setClickable(true);

            holder.itemImageView.setEnabled(true);
            holder.itemNameTextView.setEnabled(true);
            //itemCategoryTextView.setEnabled(true);
            holder.itemSellingPriceEditText.setEnabled(true);
            holder.itemMRPTextView.setEnabled(true);
            holder.itemStarImageBtn.setEnabled(true);
        }else {
            holder.itemAvailabilityBtn.setSelected(false);
            holder.itemAvailabilityBtn.setChecked(false);
            holder.itemNotAvailableFrameLayout.setVisibility(View.VISIBLE);
            //set other elements as disabled
            holder.itemImageView.setClickable(false);
            holder.itemNameTextView.setClickable(false);
            // itemCategoryTextView.setClickable(false);
            holder.itemSellingPriceEditText.setClickable(false);
            holder.itemMRPTextView.setClickable(false);
            holder.itemStarImageBtn.setClickable(false);
            // promoteItemBtn.setClickable(false);

            holder.itemImageView.setEnabled(false);
            holder.itemNameTextView.setEnabled(false);
            //itemCategoryTextView.setEnabled(false);
            holder.itemSellingPriceEditText.setEnabled(false);
            holder.itemMRPTextView.setEnabled(false);
            holder.itemStarImageBtn.setEnabled(false);
        }

        holder.itemStarImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("pppp ",String.valueOf(position));
                if(itemList.get(position).getStar() == 1){
                    holder.itemStarImageBtn.setImageResource(R.drawable.star_btn_hollow_vector);
                    itemList.get(position).setStar(0);

                    //send updat request
                    sendData.updateProduct("star","0",itemList.get(position).getProductID());
                    //update local database
                    databaseHelper.updateProduct(itemList.get(position));

                }else{
                    holder.itemStarImageBtn.setImageResource(R.drawable.star_btn_solid_vector);
                    itemList.get(position).setStar(1);

                    //send updat request
                    sendData.updateProduct("star","1",itemList.get(position).getProductID());
                    //update local database
                    databaseHelper.updateProduct(itemList.get(position));
                }
            }
        });

        holder.itemAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.itemAvailabilityBtn.isSelected()) {

                    //set FrameLayout visible
                    holder.itemAvailabilityBtn.setSelected(false);
                    holder.itemAvailabilityBtn.setChecked(false);
                    holder.itemNotAvailableFrameLayout.setVisibility(View.VISIBLE);

                    //send request
                    int productId = itemList.get(position).getProductID();
                    sendData.updateProduct("availability","0",productId);
                    //update local database
                    itemList.get(position).setAvailability(0);
                    databaseHelper.updateProduct(itemList.get(position));

                    //set other elements as disabled
                    holder.itemImageView.setClickable(false);
                    holder.itemNameTextView.setClickable(false);
                    // itemCategoryTextView.setClickable(false);
                    holder.itemSellingPriceEditText.setClickable(false);
                    holder.itemMRPTextView.setClickable(false);
                    holder.itemStarImageBtn.setClickable(false);
                    // promoteItemBtn.setClickable(false);

                    holder.itemImageView.setEnabled(false);
                    holder.itemNameTextView.setEnabled(false);
                    //itemCategoryTextView.setEnabled(false);
                    holder.itemSellingPriceEditText.setEnabled(false);
                    holder.itemMRPTextView.setEnabled(false);
                    holder.itemStarImageBtn.setEnabled(false);
                    // promoteItemBtn.setEnabled(false);

                } else {
                    //set framelayout invisible
                    holder.itemAvailabilityBtn.setSelected(true);
                    holder.itemAvailabilityBtn.setChecked(true);
                    holder.itemNotAvailableFrameLayout.setVisibility(View.INVISIBLE);

                    //send request
                    int productId = itemList.get(position).getProductID();
                    sendData.updateProduct("availability","1",productId);
                    //update local database
                    itemList.get(position).setAvailability(0);
                    databaseHelper.updateProduct(itemList.get(position));

                    //set other elements as enabled
                    holder.itemImageView.setClickable(true);
                    holder.itemNameTextView.setClickable(true);
                    //itemCategoryTextView.setClickable(true);
                    holder.itemSellingPriceEditText.setClickable(true);
                    holder.itemMRPTextView.setClickable(true);
                    holder.itemStarImageBtn.setClickable(true);
                    //promoteItemBtn.setClickable(true);

                    holder.itemImageView.setEnabled(true);
                    holder.itemNameTextView.setEnabled(true);
                    //itemCategoryTextView.setEnabled(true);
                    holder.itemSellingPriceEditText.setEnabled(true);
                    holder.itemMRPTextView.setEnabled(true);
                    holder.itemStarImageBtn.setEnabled(true);
                }
            }
        });

        holder.itemSellingPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    String edittextString = "";
                    edittextString = holder.itemSellingPriceEditText.getText().toString();
                    if (edittextString.isEmpty()){
                        itemList.get(position).setSellingPrice(itemList.get(position).getPrice());
                        //send updat request
                        sendData.updateProduct("price",String.valueOf(itemList.get(position).getPrice()),itemList.get(position).getProductID());
                        //update local database
                        databaseHelper.updateProduct(itemList.get(position));
                    }else {
                        if (itemList.get(position).getSellingPrice() != Double.parseDouble(edittextString)){
                            itemList.get(position).setSellingPrice(Double.parseDouble(edittextString));
                            //send updat request
                            sendData.updateProduct("price",String.valueOf(edittextString),itemList.get(position).getProductID());
                            //update local database
                            databaseHelper.updateProduct(itemList.get(position));
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemsListViewHolder extends RecyclerView.ViewHolder {

        private RadioButton itemAvailabilityBtn;
        private ImageView itemImageView;
        private EditText itemSellingPriceEditText;
        private TextView itemNameTextView,itemMRPTextView;//itemCategoryTextView, promoteItemBtn;
        private ImageButton itemStarImageBtn;
        private FrameLayout itemNotAvailableFrameLayout;
        private View layout;

        public ItemsListViewHolder(View itemView) {
            super(itemView);

            layout = itemView;
            itemAvailabilityBtn = (RadioButton) layout.findViewById(R.id.item_availability_btn_id);
            itemImageView = (ImageView) layout.findViewById(R.id.itemslist_row_image_id);
            itemNameTextView = (TextView) layout.findViewById(R.id.itemslist_name_textview_id);
            //itemCategoryTextView = (TextView) layout.findViewById(R.id.itemlist_category_textview_id);
            itemSellingPriceEditText =  layout.findViewById(R.id.item_selling_price_textview_id);
            itemMRPTextView = (TextView) layout.findViewById(R.id.item_mrp_textview_id);
            itemStarImageBtn = (ImageButton) layout.findViewById(R.id.item_start_btn_id);
           // promoteItemBtn = (TextView) layout.findViewById(R.id.promote_product_btn_id);
            itemNotAvailableFrameLayout = (FrameLayout) layout.findViewById(R.id.item_not_available_framelayout_id);
        }
    }

}

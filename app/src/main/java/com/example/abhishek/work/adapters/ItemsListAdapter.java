package com.example.abhishek.work.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.abhishek.work.Model.ItemData;
import com.example.abhishek.work.R;
import com.example.abhishek.work.ServerOperations.SendData;

import java.util.ArrayList;
import java.util.List;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ItemsListViewHolder> {

    private List<ItemData> itemList = new ArrayList<ItemData>();
    private int position;
    private Context context;
    private SendData sendData;

    public ItemsListAdapter(Context context, List<ItemData> itemList) {
        this.itemList = itemList;
        this.context = context;
        sendData = new SendData(context);
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
        String url = "http://ec2-18-216-46-195.us-east-2.compute.amazonaws.com/magento/pub/media/catalog/product/" + itemList.get(position).getPhoto();
        if (!url.equals("0") && !url.isEmpty()) {
            Glide.with(context)
                    .load(url)
                    .into(holder.itemImageView);
        }

/*
        //radio button set
        if(itemList.get(position).isAvailable()){
            holder.itemAvailabilityBtn.setSelected(true);
            holder.itemAvailabilityBtn.setChecked(true);
            holder.itemNotAvailableFrameLayout.setVisibility(View.INVISIBLE);
        }else {
            holder.itemAvailabilityBtn.setSelected(false);
            holder.itemAvailabilityBtn.setChecked(false);
            holder.itemNotAvailableFrameLayout.setVisibility(View.VISIBLE);
        }

        //name,category,mrp,price  ---   value set
        holder.itemNameTextView.setText(itemList.get(position).getName());
        holder.itemCategoryTextView.setText(itemList.get(position).getCategory());
        holder.itemSellingPriceEditText.setText(itemList.get(position).getSellingPrice());
        holder.itemMRPTextView.setText(itemList.get(position).getMrp());

        //star button set
        if (itemList.get(position).isStar()){
            holder.itemStarImageBtn.setImageResource(R.drawable.star_btn_solid_vector);
        }else {
            holder.itemStarImageBtn.setImageResource(R.drawable.star_btn_hollow_vector);
        }

*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton itemAvailabilityBtn;
        private ImageView itemImageView;
        private Button itemSellingPriceEditText;
        private TextView itemNameTextView, itemCategoryTextView, itemMRPTextView, promoteItemBtn;
        private ImageButton itemStarImageBtn;
        private FrameLayout itemNotAvailableFrameLayout;
        private View layout;

        public ItemsListViewHolder(View itemView) {
            super(itemView);

            layout = itemView;
            itemAvailabilityBtn = (RadioButton) layout.findViewById(R.id.item_availability_btn_id);
            itemImageView = (ImageView) layout.findViewById(R.id.itemslist_row_image_id);
            itemNameTextView = (TextView) layout.findViewById(R.id.itemslist_name_textview_id);
            itemCategoryTextView = (TextView) layout.findViewById(R.id.itemlist_category_textview_id);
            itemSellingPriceEditText = (Button) layout.findViewById(R.id.item_selling_price_textview_id);
            itemMRPTextView = (TextView) layout.findViewById(R.id.item_mrp_textview_id);
            itemStarImageBtn = (ImageButton) layout.findViewById(R.id.item_start_btn_id);
            promoteItemBtn = (TextView) layout.findViewById(R.id.promote_product_btn_id);
            itemNotAvailableFrameLayout = (FrameLayout) layout.findViewById(R.id.item_not_available_framelayout_id);


            // button click listener
            itemAvailabilityBtn.setOnClickListener(this);
            itemStarImageBtn.setOnClickListener(this);
            promoteItemBtn.setOnClickListener(this);

            itemAvailabilityBtn.setSelected(true);
            itemAvailabilityBtn.setChecked(true);
        }

        @Override
        public void onClick(View view) {

            int id = view.getId();

            // item availability radio button click listener
            if (id == itemAvailabilityBtn.getId()) {
                if (itemAvailabilityBtn.isSelected()) {

                    //set FrameLayout visible
                    itemAvailabilityBtn.setSelected(false);
                    itemAvailabilityBtn.setChecked(false);
                    itemNotAvailableFrameLayout.setVisibility(View.VISIBLE);

                    //send request
                    int productId = itemList.get(position).getProductID();
                    sendData.updateProduct("availability","0",productId);

                    //set other elements as disabled
                    itemImageView.setClickable(false);
                    itemNameTextView.setClickable(false);
                    itemCategoryTextView.setClickable(false);
                    itemSellingPriceEditText.setClickable(false);
                    itemMRPTextView.setClickable(false);
                    itemStarImageBtn.setClickable(false);
                    promoteItemBtn.setClickable(false);

                    itemImageView.setEnabled(false);
                    itemNameTextView.setEnabled(false);
                    itemCategoryTextView.setEnabled(false);
                    itemSellingPriceEditText.setEnabled(false);
                    itemMRPTextView.setEnabled(false);
                    itemStarImageBtn.setEnabled(false);
                    promoteItemBtn.setEnabled(false);

                } else {
                    //set framelayout invisible
                    itemAvailabilityBtn.setSelected(true);
                    itemAvailabilityBtn.setChecked(true);
                    itemNotAvailableFrameLayout.setVisibility(View.INVISIBLE);

                    //send request
                    int productId = itemList.get(position).getProductID();
                    sendData.updateProduct("availability","1",productId);

                    //set other elements as enabled
                    itemImageView.setClickable(true);
                    itemNameTextView.setClickable(true);
                    itemCategoryTextView.setClickable(true);
                    itemSellingPriceEditText.setClickable(true);
                    itemMRPTextView.setClickable(true);
                    itemStarImageBtn.setClickable(true);
                    promoteItemBtn.setClickable(true);

                    itemImageView.setEnabled(true);
                    itemNameTextView.setEnabled(true);
                    itemCategoryTextView.setEnabled(true);
                    itemSellingPriceEditText.setEnabled(true);
                    itemMRPTextView.setEnabled(true);
                    itemStarImageBtn.setEnabled(true);
                    promoteItemBtn.setEnabled(true);

                }

                // item star button click listener
            } else if (id == itemStarImageBtn.getId()) {

                if(itemList.get(position).getStar() == 1){
                    itemStarImageBtn.setImageResource(R.drawable.star_btn_hollow_vector);
                    itemList.get(position).setStar(0);
                }else{
                    itemStarImageBtn.setImageResource(R.drawable.star_btn_solid_vector);
                    itemList.get(position).setStar(1);
                }


                // item promote product button click listener
            } else if (id == promoteItemBtn.getId()) {

            }
        }
    }

}

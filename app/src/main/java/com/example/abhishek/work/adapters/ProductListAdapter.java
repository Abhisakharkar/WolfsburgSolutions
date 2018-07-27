package com.example.abhishek.work.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.NewProductActivity;
import com.example.abhishek.work.ProductDetailsActivity;
import com.example.abhishek.work.ProductEditActivity;
import com.example.abhishek.work.R;

import java.io.File;
import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<ProductData> arrayList;

    public ProductListAdapter(Context context, ArrayList<ProductData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        holder.nameTextView.setText(arrayList.get(position).getName());
        holder.priceTextView.setText("Rs. " + String.valueOf(arrayList.get(position).getPrice()));
        String url = "http://ec2-13-59-88-132.us-east-2.compute.amazonaws.com/magento/pub/media/catalog/product" + arrayList.get(position).getPhoto();
        if (!url.equals("0") && !url.isEmpty()){
            Glide.with(context)
                    .load(url)
                    .into(holder.productImageView);
        }

        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO send request to magento_get_attribute_group with attribute_set_id
                int attributeSetId = arrayList.get(position).getAttribute_set_id();
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("attributeSetId",attributeSetId);
                context.startActivity(intent);
            }
        });
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ProductEditActivity.class);
                intent.putExtra("name", arrayList.get(position).getName());
                intent.putExtra("productID", arrayList.get(position).getProductID());
                Log.e("id id",String.valueOf(arrayList.get(position).getProductID()));
                intent.putExtra("attribute_set_id", arrayList.get(position).getAttribute_set_id());
                intent.putExtra("price", arrayList.get(position).getPrice());
                intent.putExtra("photo",arrayList.get(position).getPhoto());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView,priceTextView;
        private Button addBtn;
        private ImageView productImageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.product_list_row_name_textview_id);
            priceTextView = (TextView) itemView.findViewById(R.id.product_list_row_price_textview_id);
            addBtn = (Button) itemView.findViewById(R.id.product_list_row_add_btn_id);
            productImageView = (ImageView) itemView.findViewById(R.id.products_list_row_imageview_id);
        }
    }
}

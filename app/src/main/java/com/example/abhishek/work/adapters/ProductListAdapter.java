package com.example.abhishek.work.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.NewProductActivity;
import com.example.abhishek.work.ProductEditActivity;
import com.example.abhishek.work.R;

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
        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO send request to magento_get_attribute_group with attribute_set_id
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


                //do all of this in next activity
                //
                //TODO add product to local database
                //TODO add to database at server
                //return of server
                // --> result:boolean
                //if false  --> retry
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private Button addBtn;

        public ProductViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.product_list_row_name_textview_id);
            addBtn = (Button) itemView.findViewById(R.id.product_list_row_add_btn_id);
        }
    }
}

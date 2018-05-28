package com.example.abhishek.work.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.abhishek.work.Model.ProductData;
import com.example.abhishek.work.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList<ProductData> arrayList;

    public ProductListAdapter(Context context,ArrayList<ProductData> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_list,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.nameTextView.setText(arrayList.get(position).getName());
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

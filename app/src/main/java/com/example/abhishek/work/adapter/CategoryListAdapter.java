package com.example.abhishek.work.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.work.Model.CategoryData;
import com.example.abhishek.work.NewProductActivity;
import com.example.abhishek.work.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<CategoryData> arrayList = new ArrayList<>();

    public CategoryListAdapter(Context context,ArrayList<CategoryData> arrayList){
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_list,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {

        holder.nameTextView.setText(arrayList.get(position).getName());
        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,NewProductActivity.class);
                intent.putExtra("name",arrayList.get(position).getName());
                intent.putExtra("id",arrayList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.category_list_row_name_textview_id);
        }
    }
}

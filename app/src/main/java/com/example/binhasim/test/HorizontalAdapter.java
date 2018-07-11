package com.example.binhasim.test;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.itemsViewHolder>{

    int resource;
        @NonNull
        @Override
        public itemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                resource=R.layout.details_fragment;
            View view=(LayoutInflater.from(parent.getContext()).inflate(resource,parent,false));
            return new itemsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull itemsViewHolder holder, int position) {
            DataFormat dataFormat=ItemsAdapter.list.get(position);
            holder.setDateText(ItemsAdapter.getDate(dataFormat.getDateTime()));
            holder.setDescriptionText(dataFormat.getMainText());
            if(Logic.selectedItems.get(holder.getAdapterPosition(),false))
            {
                holder.itemView.setActivated(true);
            }
            else {holder.itemView.setActivated(false);}
        }
        @Override
        public int getItemCount() {
            return ItemsAdapter.list.size();
        }


    public static class itemsViewHolder extends RecyclerView.ViewHolder{
            private TextView DescriptionText,DateText;
            itemsViewHolder(View itemView) {
                super(itemView);
                DescriptionText=itemView.findViewById(R.id.Actual_Text);
                DateText=itemView.findViewById(R.id.Date);
            }
            void setDescriptionText(String descriptionText) { DescriptionText.setText(descriptionText); }
            void setDateText(String dateText) {
                DateText.setText(dateText);
            }
        }
    }
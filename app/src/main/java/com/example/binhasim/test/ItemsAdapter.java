package com.example.binhasim.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.itemsViewHolder>
{
private clickListener listener;
   static ArrayList<DataFormat> list=new ArrayList<>();


     ItemsAdapter(clickListener listener,ArrayList<DataFormat> reference)
    {
        super();
        this.listener=listener;
        list=reference;
    }
    public void setData(ArrayList<DataFormat> datafromFirebase)
    {
        list=datafromFirebase;
    }

    @NonNull
    @Override
    public itemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view=(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false));
        return new itemsViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull itemsViewHolder holder, int position) {
        // if(list!=null) {
             DataFormat dataFormat = list.get(position);
             holder.setDateText(getDate(dataFormat.getDateTime()));
             holder.setDescriptionText(dataFormat.getMainText());
             if (Logic.selectedItems.get(holder.getAdapterPosition(), false)) {
                 holder.itemView.setActivated(true);
             } else {
                 holder.itemView.setActivated(false);
             }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

public static String getDate(Object dataFormat)
{
    try {
        DateFormat dateFormat = getDateTimeInstance();
        Date netDate = (new Date((Long) dataFormat));
        return dateFormat.format(netDate);

    } catch (Exception e) {return null;
    }
}

    public static class itemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView DescriptionText,DateText;
        private clickListener clickListener;
         itemsViewHolder(View itemView, clickListener clickListener) {
            super(itemView);
            DescriptionText=itemView.findViewById(R.id.mainText);
            DateText=itemView.findViewById(R.id.dateText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.clickListener= clickListener;
        }

        void setDescriptionText(String descriptionText) {
            DescriptionText.setText(descriptionText);
        }

         void setDateText(String dateText) {
            DateText.setText(dateText);
        }


        @Override
        public void onClick(View v) {
            if(clickListener!=null)
            {
                clickListener.onItemClicked(v,getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return clickListener!=null&& clickListener.onItemLongClicked(v,getAdapterPosition());
        }
    }

    public interface clickListener {
        void onItemClicked(View v,int position);

        boolean onItemLongClicked(View v, int position);
    }



}

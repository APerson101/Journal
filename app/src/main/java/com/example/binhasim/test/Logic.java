package com.example.binhasim.test;

import android.util.SparseBooleanArray;
import android.view.View;

import java.util.ArrayList;

public  class Logic {

   static SparseBooleanArray selectedItems;
   ArrayList<View> views=new ArrayList<>();

    public Logic()
    {
        selectedItems=new SparseBooleanArray();
    }

    public void ToggleSelection(View v,int positionOfItemClicked)
    {
        if(selectedItems.get(positionOfItemClicked,false))
        {
            selectedItems.delete(positionOfItemClicked);
            v.setActivated(false);
            views.remove(v);
        }
        else
        {
            selectedItems.put(positionOfItemClicked,true);
            v.setActivated(true);
            views.add(v);
        }
    }

    public void enterSelected(int positionClicked)
    {
        selectedItems.put(positionClicked,true);
    }
    public int getNumberOfSelectionsMade()
    {
        return selectedItems.size();
    }

    public void clearSelection()
    {
        for (View view:views
                ) {
            view.setActivated(false);
        }
        selectedItems.clear();
    }

    public void DeleteSelectedItems()
    {
        while(selectedItems.size()!=0) {
            int currentMax = 0;
            for (int i = 0; i < selectedItems.size(); i++) {
                if (selectedItems.keyAt(i) > currentMax) {
                    currentMax = selectedItems.keyAt(i);
                }
            }
            ItemsAdapter.list.remove(currentMax);
            selectedItems.delete(currentMax);
        }
        selectedItems.clear();
    }

    public  ArrayList<Integer> getSelectedItems() {
        ArrayList<Integer> selectionsMade=new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
             selectionsMade.add(selectedItems.keyAt(i));
        }
        return selectionsMade;
    }

    public void ShareSelectedItems(int currentPosition)
    {
        //share selected items
    }

    public void deleteSelected(int currentPosition) {
        enterSelected(currentPosition);
        DeleteSelectedItems();
    }

    public void setFavourite(int currentPosition) {
    }

    public void updateData(int position, String text) {
        ItemsAdapter.list.get(position).setMainText(text);
    }
}

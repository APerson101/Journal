package com.example.binhasim.test;

import android.widget.ImageView;

import java.util.Map;

public class DataFormat {

   private String mainText;

    public Object getDateTime() {
        return dateTime;
    }

    public void setDateTime(Object dateTime) {
        this.dateTime = dateTime;
    }

    private Object dateTime;
 //  private ImageView imageView;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

  //  public ImageView getImageView(){return imageView;}

    public DataFormat(String mainText,Object DateTime)
   {
       this.mainText=mainText;
       this.dateTime=DateTime;
   }

   public DataFormat()
   {

   }


}

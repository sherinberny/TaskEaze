package com.ads.taskeaze.model;

import android.graphics.Bitmap;

/**
 * Created by user1 on 09-10-2017.
 */

public class TravelImageModel {

    private String _b64String=null;
    private Bitmap bitmap=null;
    private boolean isAddImageButton=false;


    public TravelImageModel(String _b64String, Bitmap bitmap, boolean isAddImageButton) {
        this._b64String = _b64String;
        this.bitmap = bitmap;
        this.isAddImageButton = isAddImageButton;

    }

    public String get_b64String() {
        return _b64String;
    }

    public void set_b64String(String _b64String) {
        this._b64String = _b64String;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isAddImageButton() {
        return isAddImageButton;
    }

    public void setAddImageButton(boolean addImageButton) {
        isAddImageButton = addImageButton;
    }
}

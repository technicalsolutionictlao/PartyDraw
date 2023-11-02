package com.ictlao.android.app.partydraw.Core.Models;

public class ItemFilterInvitation {
    private String mName;
    private int mImageRes;

    public ItemFilterInvitation(String name, int imageRes){
        this.mName = name;
        this.mImageRes = imageRes;
    }

    public String getName(){
        return mName;
    }

    public int getImageResource(){
        return mImageRes;
    }
}

package com.ictlao.android.app.partydraw.Core.Models;

public class TwoItem {
    public String PlayerGivePresentName;
    public String PlayerRequestName;
    public int ImageResId;
    public TwoItem(String giveName, int imageResId, String receiveName){
        this.PlayerGivePresentName = giveName;
        this.ImageResId = imageResId;
        this.PlayerRequestName = receiveName;
    }

    public int getImageResId() {
        return ImageResId;
    }

    public String getGiveName() {
        return PlayerGivePresentName;
    }

    public String getReceiveName() {
        return PlayerRequestName;
    }
}

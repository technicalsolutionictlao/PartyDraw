package com.ictlao.android.app.partydraw.Core.Models;

public class Item {
    public String Name;
    public int Image;

    public Item(String Name, int Image)
    {
        this.Name=Name;
        this.Image=Image;
    }
    public String getNames()
    {
        return Name;
    }
    public int getImage()
    {
        return Image;
    }
}

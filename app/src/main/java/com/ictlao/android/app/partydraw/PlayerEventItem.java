package com.ictlao.android.app.partydraw;

public class PlayerEventItem {
    private String Name;
    private int ClickNumber;
    private int Index;
    private int Result;


    public  PlayerEventItem(String name, int clickNumber, int index, int result){
        this.Name = name;
        this.ClickNumber = clickNumber;
        this.Index = index;
        this.Result = result;
    }

    public String getName(){
        return Name;
    }
    public int getClickNumber(){
        return ClickNumber;
    }
    public int getIndex(){
        return Index;
    }
    public int getResult(){
        return Result;
    }
}

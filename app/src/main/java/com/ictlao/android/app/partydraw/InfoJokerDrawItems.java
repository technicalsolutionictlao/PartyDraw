package com.ictlao.android.app.partydraw;


class InfoJokerDrawItems {
    private String mPlayerName;
    private String mStatusTurn;

    public InfoJokerDrawItems(String playerName, String statusTurn){
        this.mPlayerName = playerName;
        this.mStatusTurn = statusTurn;
    }

    public String getPlayerName(){
        return mPlayerName;
    }

    public String getStatusTurn(){
        return mStatusTurn;
    }
}

package com.ictlao.android.app.partydraw.Core.Models;

public class LobbyItems {
    private String PlayerName;
    private String Status;

    public LobbyItems(String playerName, String status){
        this.PlayerName = playerName;
        this.Status = status;
    }

    public String getPlayerName(){
        return PlayerName;
    }

    public String getStatus(){
        return Status;
    }
}

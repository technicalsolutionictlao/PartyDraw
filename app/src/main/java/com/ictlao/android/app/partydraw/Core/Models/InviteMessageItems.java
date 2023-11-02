package com.ictlao.android.app.partydraw.Core.Models;

public class InviteMessageItems {

    private String InviteName;
    private String InviteGroupName;
    private String InviteTime;
    private String InviteGroupPassword;

    public InviteMessageItems(String inviteName, String inviteGroupName, String inviteTime, String inviteGroupPassword){
        this.InviteName = inviteName;
        this.InviteGroupName = inviteGroupName;
        this.InviteTime = inviteTime;
        this.InviteGroupPassword = inviteGroupPassword;
    }

    public String getInviteName(){
        return InviteName;
    }

    public String getInviteGroupName(){
        return InviteGroupName;
    }

    public String getInviteTime(){
        return InviteTime;
    }

    public String getInviteGroupPassword(){
        return InviteGroupPassword;
    }
}

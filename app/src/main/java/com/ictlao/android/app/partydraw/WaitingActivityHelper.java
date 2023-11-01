package com.ictlao.android.app.partydraw;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class WaitingActivityHelper {

    private Activity mContext;

    public WaitingActivityHelper(Activity context){
        this.mContext = context;
    }

    public void removePlayerWaiting(String group_name, final String player){
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath+group_name);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.orderByChild(group_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getValue().equals(player)){
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeGroupInList(final String group_name){
        HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupListsPath);
        HomeActivity.mGroupListsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(group_name)) {
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeLobbyGroup(final String group_name){
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(group_name)){
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removePlayingGroup(final String group_name){
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(group_name)){
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeValuesGroup(final String group_name){
        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath);
        HomeActivity.mGroupInfo_Values_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(group_name)){
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setAdminToControlGame(String group_name, String player){
        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+group_name+HomeActivity.mTreeAdmin);
        HomeActivity.mAdminNameReference.setValue(player);
    }

    public void setPlayerReadyToPlay(String group, String player){
        HomeActivity.mReadyPlayingGameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseReadyPlayingPath+group);
        HomeActivity.mReadyPlayingGameReference.child(player).setValue(player);
    }

    public void removePlayerReadyToPlay(String group, final String player){
        HomeActivity.mReadyPlayingGameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseReadyPlayingPath+group);
        HomeActivity.mReadyPlayingGameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(player)){
                        snapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

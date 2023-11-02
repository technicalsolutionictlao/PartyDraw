package com.ictlao.android.app.partydraw.Core;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;

public class OnlineJokerDrawHelper {

    public OnlineJokerDrawHelper(Activity context){
        Initialize();
    }

    private void Initialize(){
       HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath);
    }

    public void removePlayerNameFromPlayingGame(final String group_name, final String player){
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.orderByChild(group_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.child(group_name).getChildren()){
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
}

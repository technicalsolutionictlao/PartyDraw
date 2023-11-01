package com.ictlao.android.app.partydraw;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class ManagePlayerStatus {

    private Activity mContext = null;
    private InternetProvider mInternetProvider;
    private boolean isConnected = false;


    public ManagePlayerStatus(Activity context){
        mContext = context;
        mInternetProvider = new InternetProvider(mContext);
        isConnected = mInternetProvider.isConnected();
    }

    public ManagePlayerStatus()
    {
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
    }

    public void setStatusOnline(String player_name){
        if(isConnected) {
            HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
            HomeActivity.mStatus_Online_Reference.child(player_name).setValue(player_name);
        }
    }

    public void removeStatusOnline(final String player_name){
        if(isConnected) {
            HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
            HomeActivity.mStatus_Online_Reference.orderByChild(player_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(player_name)) {
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

    public void setStatusLobby(String player_name){
        HomeActivity.mStatus_Lobby_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusLobbyModePath);
        HomeActivity.mStatus_Lobby_Reference.child(player_name).setValue(player_name);
    }

    public void removeStatusLobby(final String player_name){
        if(isConnected) {
            HomeActivity.mStatus_Lobby_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusLobbyModePath);
            HomeActivity.mStatus_Lobby_Reference.orderByChild(player_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(player_name)) {
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

    public void deleteOnlineStatus(String player)
    {
        if(HomeActivity.mStatus_Online_Reference != null)
        {
            if(!player.equals("")){
                HomeActivity.mStatus_Online_Reference.child(player).removeValue();
            }
        }
    }

}

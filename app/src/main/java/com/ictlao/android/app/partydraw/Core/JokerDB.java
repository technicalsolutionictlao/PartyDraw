package com.ictlao.android.app.partydraw.Core;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JokerDB {
   private static JokerDB mJokerDB = null;
   private DatabaseReference mDatabaseReference = null;

   private JokerDB(){
      mDatabaseReference = FirebaseDatabase.getInstance().getReference();
   }

   public static JokerDB getInstance(){
      if(mJokerDB == null){
         mJokerDB = new JokerDB();
      }
      return mJokerDB;
   }

   public void disposed(){
      mDatabaseReference = null;
      mJokerDB = null;
   }
}

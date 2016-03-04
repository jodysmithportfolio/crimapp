package com.somethingweird.crimapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


public final class Util {

    private Util(){

    }


    public static void call(Class mClass, Context mContext){
        String TAG = mClass.getSimpleName();
        Intent call911 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:911"));
        call911.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(call911);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(mContext, "Call Failed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, ex.toString());
        }
    }
}

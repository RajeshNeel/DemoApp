package com.gaurav.demoapp.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.gaurav.demoapp.R;

import dmax.dialog.SpotsDialog;

/**
 * Created by Gaurav Sharma on 24-08-2021 on 16:16 .
 */
public class CommonMethod {

    public static void showToast(Context context, String message, int duration){
        Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();

        view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(context, R.color.white));
        toast.show();
    }

    static CharSequence s;

    public static  void foo(CharSequence cs) {

        s = cs;
        System.out.println(cs);
    }


    private static SpotsDialog progressDialogs;

    public static void createProgress(Context context,final String message){

        CharSequence cs = message;
        String st = cs.toString();
        foo(st); // prints "string"

        progressDialogs = new SpotsDialog(context, message);
        progressDialogs.setCancelable(false);
        progressDialogs.setMessage(s);
        progressDialogs.setCanceledOnTouchOutside(false);
        progressDialogs.show();

    }

    public static void closeProgress(){
        if(progressDialogs!=null){
            if(progressDialogs.isShowing()){
                progressDialogs.dismiss();
            }
        }
    }

}
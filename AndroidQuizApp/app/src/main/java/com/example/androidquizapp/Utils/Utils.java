package com.example.androidquizapp.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class Utils {
    private static ProgressDialog pd;
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
    public static void showProgressDialog(Context context, String Message) {

            if (pd == null) {
                pd = new ProgressDialog(context);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(false);
            }
            pd.setMessage(Message);


                if(!((Activity) context).isFinishing()||!((Activity) context).isDestroyed()){
                pd.show();
            }

    }
    public static void hideProgressDialog() {

            if (pd != null) {
                try {
                    pd.dismiss();
                } catch (IllegalArgumentException e) {
                    Log.d("TAG", "hideProgressDialog: "+"Spinner not on window");
                }
                pd = null;
            }

    }
    public static boolean ChheckAndRequestpermission(Context context) {

        if (Build.VERSION.SDK_INT >= 23) {

            int cameraPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }


}

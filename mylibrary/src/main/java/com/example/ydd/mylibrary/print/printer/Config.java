package com.example.ydd.mylibrary.print.printer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.gprinter.io.PortManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Config {
    public static final int WIFI = 1;
    public static final int BLUETOOTH = 2;
    public static ConcurrentMap<String ,PortManager> map = new ConcurrentHashMap<>();
    public static void putPrinter(String address, PortManager portManager){
        if(map.get(address)==null){
            map.put(address,portManager);
        }
    }
    public static PortManager getPrinter(String address){
        return map.get(address);
    }

    public static void requestAllPower(Activity context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}

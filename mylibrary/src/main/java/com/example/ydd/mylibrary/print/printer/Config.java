package com.example.ydd.mylibrary.print.printer;

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
}

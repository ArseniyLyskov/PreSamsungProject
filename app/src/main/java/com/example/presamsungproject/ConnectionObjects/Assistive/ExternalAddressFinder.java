package com.example.presamsungproject.ConnectionObjects.Assistive;

import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.aware.WifiAwareNetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;
import com.example.presamsungproject.ConnectionObjects.MessageManager;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ExternalAddressFinder {
    public static void tryToFind(Context context) {
        if(!isWifiOn(context)) {
            Log.d("MyTag", "Wifi is off");
            return;
        }
        FindExternalAddressThread FEAThread = new FindExternalAddressThread();
        FEAThread.start();
    }

    private static boolean isWifiOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    static class FindExternalAddressThread extends Thread {
        @Override
        public void run() {
            try {
                Socket testConnection = new Socket();
                testConnection.connect(new InetSocketAddress("www.google.com", 80));
                //EXTERNAL_ADDRESS = testConnection.getLocalAddress().toString();
                MessageManager.EXTERNAL_ADDRESS = testConnection.getLocalAddress().getHostAddress();
                Log.d("MyTag", "ExternalAddress: " + MessageManager.EXTERNAL_ADDRESS);
                testConnection.close();
            } catch (Exception e) {
                Log.d("MyTag", "Getting external address error.");
                e.printStackTrace();
            }
        }
    }
}

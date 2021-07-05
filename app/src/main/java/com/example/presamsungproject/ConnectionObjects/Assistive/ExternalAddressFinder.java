package com.example.presamsungproject.ConnectionObjects.Assistive;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.example.presamsungproject.Models.InfoSingleton;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ExternalAddressFinder {
    private static FindExternalAddressThread FEAThread = null;

    public static void tryToFind(Context context) {
        if (!isWifiOn(context)) {
            Log.d("MyTag", "Wifi is off");
            return;
        }
        if (FEAThread != null)
            FEAThread.close();
        FEAThread = new FindExternalAddressThread();
        FEAThread.start();
    }

    private static boolean isWifiOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    static class FindExternalAddressThread extends Thread {
        private Socket testConnection;

        @Override
        public void run() {
            try {
                testConnection = new Socket();
                testConnection.connect(new InetSocketAddress("www.google.com", 80));
                InfoSingleton.getInstance().setEXTERNAL_ADDRESS(testConnection.getLocalAddress().getHostAddress());
                Log.d("MyTag", "ExternalAddress: " + InfoSingleton.getInstance().getEXTERNAL_ADDRESS());
                testConnection.close();
            } catch (Exception e) {
                Log.d("MyTag", "Getting external address error.");
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                testConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

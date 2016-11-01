package com.joshua.digitalwallet;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    String[] wifilist;

    ArrayAdapter adapter;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mainText = (TextView) findViewById( R.id.balance);
        TextView send = (TextView) findViewById( R.id.send);
        TextView receive = (TextView) findViewById( R.id.receive);

        // Initiate wifi service manager
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        //receiverWifi = new WifiReceiver();

        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //mainWifi.startScan();
        //mainText.setText("Starting Scan...");

        doInback();




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_popup();
                //turn WiFi on
                WfManager.isApOn(MainActivity.this);
                WfManager.configApState(MainActivity.this);
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r_popup();
                //turn WiFi hotspot on
                ApManager.isApOn(MainActivity.this); // check Ap state :boolean
                ApManager.configApState(MainActivity.this); // change Ap state :boolean
            }
        });


    }//end method onCreate

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {

            //sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            //sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            wifilist = new String[wifiList.size()];
            for(int i = 0; i < wifiList.size(); i++){

                wifilist[i] = wifiList.get(i).SSID.toString();
            }

            //mainText.setText(sb);
        }

    }

    public void doInback()
    {
        handler.postDelayed(new Runnable() {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifi.startScan();
                doInback();
            }
        }, 1000);

    }

    public void r_popup(){
        try{
            Dialog r_pop = new Dialog(MainActivity.this);
            r_pop.setContentView(R.layout.r_popup);
            r_pop.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            r_pop.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }//end method r_popup

    public void s_popup(){
        try{
            Dialog s_pop = new Dialog(MainActivity.this);
            s_pop.setContentView(R.layout.s_popup);
            s_pop.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            s_pop.show();

            TextView connect = (TextView) s_pop.findViewById( R.id.connect);
            //connect.setText(sb);//sb


            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.wifi_list_view, wifilist);
            final ListView listView = (ListView) s_pop.findViewById(R.id.wifiList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    String selectedFromList =(String) (listView.getItemAtPosition(myItemInt));

                    Toast.makeText(MainActivity.this,selectedFromList,Toast.LENGTH_SHORT).show();

                    //WifiConfiguration wifiConfig = new WifiConfiguration();
                    //wifiConfig.SSID = String.format("\"%s\"", selectedFromList);

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = String.format("\"%s\"", selectedFromList);
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    wifiManager.addNetwork(conf);
//remember id
                    int netId = wifiManager.addNetwork(conf);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(netId, true);
                    wifiManager.reconnect();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }//end method s_popup

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

}// end class MainActivity

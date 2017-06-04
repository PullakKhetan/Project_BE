 package com.example.pullak.a6_final;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Button select_devices;
    private Button connnect;
    public BluetoothAdapter myBluetooth = null;
    private ListView myListView;
    private ArrayAdapter<String> BTdevices;
    private String selected_devices=null;
    Set<BluetoothDevice> pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_devices=(Button)findViewById(R.id.button);
        connnect=(Button)findViewById(R.id.button2);
        connnect.setVisibility(View.INVISIBLE);
        myListView=(ListView)findViewById(R.id.listview);
        myListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        BTdevices=new ArrayAdapter<String>(this,R.layout.row_list,R.id.check_device);
        myListView.setAdapter(BTdevices);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!myBluetooth.isEnabled()) {
            select_devices.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Turning on Bluetooth", Toast.LENGTH_LONG).show();
            myBluetooth.enable();
            select_devices.setEnabled(true);
        }else{
            select_devices.setEnabled(true);
        }
        select_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTdevices.clear();
                selected_devices=null;
                connnect.setVisibility(View.INVISIBLE);
                pd = myBluetooth.getBondedDevices();
                if (pd.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Pair Your object with this Phone", Toast.LENGTH_SHORT).show();
                } else {
                    for (BluetoothDevice device : pd) {
                        String dName = device.getName();
                        String dAddress = device.getAddress();
                        BTdevices.add(dName + "\n" + dAddress);
                    }
                }
                myListView.setAdapter(BTdevices);
            }
        });
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                selected_devices=address;
                if(selected_devices.equals(null)){
                    connnect.setVisibility(View.INVISIBLE);
                }else {
                    connnect.setVisibility(View.VISIBLE);
                }

            }
        });

        connnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,operations.class);
                Bundle b=new Bundle();
                b.putString("address",selected_devices);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }
}

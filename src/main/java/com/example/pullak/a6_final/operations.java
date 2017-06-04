package com.example.pullak.a6_final;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class operations extends AppCompatActivity {
    private Button LedOn;
    private Button LedOff;
    private Button Ring;
    private Button Locate;
    private String address;
    private BluetoothAdapter myBluetooth;
    private BluetoothSocket btSoc;
    InputStream in=null;
    static final UUID myID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    NotificationCompat.Builder notify,notify1;
    private static final int notification_id=21311;
    private static final int notification_id_1=21312;
    private static final String TAG_in="in inputstream thread";
    private static final String TAG_conn="in connect method";
    private static final String TAG_arduino="to arduino";
    Uri tone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Db_Handler db=new Db_Handler(this,null,null,1);
    Date dt=new Date();
    SimpleDateFormat date=new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat time=new SimpleDateFormat("hh.mm.ss");
    String db_date=date.format(dt);
    String db_time=time.format(dt);
    String lat,lon;
    Objects_location obj;
    Thread input_thread;
    Thread conn_thread;
    byte[] input_msg=new byte[256];
    String received_msg;
    Runnable check_connection=new Runnable() {
        @Override
        public void run() {
            while(true){
                if(btSoc.isConnected()){

                }else{
                    Log.i("checking conn thread","connection lost");
                    notification1();
                    break;

                }
            }
        }
    };



    Runnable input_stream= new Runnable() {
        @Override
        public void run() {
            if (btSoc.isConnected()) {
                try{
                    in = btSoc.getInputStream();
                }catch (Exception e){
                    e.printStackTrace();
                }
                int begin=0;
                int bytes=0;

                while(true) {
                    received_msg = "";
                    try {
                        bytes+=in.read(input_msg,bytes,input_msg.length-bytes);
                        for(int i=begin;i<bytes;i++){
                            if(input_msg[i]=="#".getBytes()[0]){
                                received_msg=new String(input_msg,begin,i-begin);

                                begin=i+1;
                                if(i==bytes-1){
                                    bytes=0;
                                    begin=0;
                                }
                                switch (received_msg){
                                    case "Ringing":
                                        reverse_trigger.sendEmptyMessage(0);
                                        break;
                                    default:
                                        Log.i(TAG_in," "+received_msg);
                                        String[] temp=received_msg.split("\\$");
                                        lat=temp[0];
                                        lon=temp[1];
                                        obj=new Objects_location(address,lat,lon,db_date,db_time);
                                        db.addLocation(obj);
                                        break;
                                }
                            }
                        }
                    }  catch (IOException e){
                        Log.i("in inputstream thread","connection lost");
                        break;
                        ///  e.printStackTrace();
                    }
                }
            }else{
                Log.i(TAG_in,"socket is not there ");

            }
        }
    };
    Handler reverse_trigger=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            notification();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);
        LedOn=(Button)findViewById(R.id.button3);
        LedOff=(Button)findViewById(R.id.button4);
        Ring=(Button)findViewById(R.id.button5);
        Locate=(Button)findViewById(R.id.button6);
        notify=new NotificationCompat.Builder(this);
        notify.setAutoCancel(true);
        notify1=new NotificationCompat.Builder(this);
        notify1.setAutoCancel(true);
        Bundle select_address=getIntent().getExtras();
        address =select_address.getString("address");
        input_thread=new Thread(input_stream);
        conn_thread=new Thread(check_connection);
        Runnable connection=new Runnable() {
            @Override
            public void run() {
                try{
                    myBluetooth=BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice dp=myBluetooth.getRemoteDevice(address);
                        btSoc=dp.createInsecureRfcommSocketToServiceRecord(myID);
                        btSoc.connect();
                        input_thread.start();
                        conn_thread.start();
                        btSoc.getOutputStream().write("l".toString().getBytes());
                    }catch (Exception e){
                        Log.i("in connection thread","socket not created");
                        e.printStackTrace();
                    }
                }

        };
        Thread connect_device=new Thread(connection);
        connect_device.start();

        Timer t=new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                   btSoc.getOutputStream().write("l".toString().getBytes());
                    }catch(IOException e){
                    Log.i("in timer ","connection lost,notification called");
                    notification1();

                }

            }
        },2000,15000);
        Ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ring();
            }
        });
        LedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on();

            }
        });
        LedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 off();

            }
        });
        Locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc=db.getLastLocation(address);
                String[] temp=loc.split("\\$");
                Double lat=Double.parseDouble(temp[0]);
                Double lon=Double.parseDouble(temp[1]);
                Bundle coord=new Bundle();
                coord.putDouble("lat",lat);
                coord.putDouble("lon",lon);
                Intent i=new Intent(operations.this,MapsActivity.class);
                i.putExtras(coord);
                startActivity(i);
            }
        });

    }
    private void ring() {
        if(btSoc!=null){
            try{
                btSoc.getOutputStream().write("r".toString().getBytes());
                Toast.makeText(getApplicationContext(),"RINGING",Toast.LENGTH_SHORT).show();
            }
            catch (IOException e){
                Log.i("in ring method","connection lost");

            }
        }
    }
    public void notification1(){
        NotificationManager nManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notify1.setTicker("trying");
        notify1.setSmallIcon(R.mipmap.ic_launcher);
        notify1.setWhen(System.currentTimeMillis());
        notify1.setContentTitle("ALERT");
        notify1.setContentText("Object out of range");
        notify1.setSound(tone);
        Intent intt1= new Intent(this,MainActivity.class);
        PendingIntent penIntt1=PendingIntent.getActivity(this,0,intt1,PendingIntent.FLAG_UPDATE_CURRENT);
        notify1.setContentIntent(penIntt1);
        nManager.notify(notification_id_1,notify1.build());
    }
    public void notification(){
        NotificationManager nManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notify.setTicker("trying");
        notify.setSmallIcon(R.mipmap.ic_launcher_round);
        notify.setWhen(System.currentTimeMillis());
        notify.setContentTitle("ALERT");
        notify.setContentText("reverse triggered by object");
        notify.setSound(tone);
        Intent intt= new Intent(this,MainActivity.class);
        PendingIntent penIntt=PendingIntent.getActivity(this,0,intt,PendingIntent.FLAG_UPDATE_CURRENT);
        notify.setContentIntent(penIntt);
        nManager.notify(notification_id,notify.build());
    }
    private void off() {
        if(btSoc!=null){
            try{
                btSoc.getOutputStream().write("f".toString().getBytes());
            }
            catch(IOException e){
                Log.i("in off method","connection lost");

            }
        }
    }
    private void on() {
        if (btSoc!=null){
            try{
                Log.i(TAG_arduino,"led on msg sent");
                btSoc.getOutputStream().write("o".toString().getBytes());
            }catch (IOException e){
                Log.i("in on method","connection lost");

            }
        }
    }
}

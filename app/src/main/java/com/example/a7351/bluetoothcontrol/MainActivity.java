package com.example.a7351.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity {

    private TextView tv;
    Toast toast;

    private Button button1,button2,button3,button4,button5;
    private ListView lv;
    private ArrayAdapter ListViewArrayAdapter;

    private Set<BluetoothDevice>pairedDevices;//蓝牙设备对象
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器

    ArrayList BluetoothBut = new ArrayList();

    public static final String MY_UUID="00001101-0000-1000-8000-00805F9B34FB";


    Handler mHandler= new Handler();//暂时不做消息接收处理。
    SendDataThread mmSendDataThread=null;//后面调用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        tv= (TextView) findViewById(R.id.tv);
        button1 = (Button) findViewById(R.id.a1);
        button2= (Button) findViewById(R.id.a2);
        button3= (Button) findViewById(R.id.a3);
        button4= (Button) findViewById(R.id.a4);
        button5 = (Button) findViewById(R.id.a5);
        lv = (ListView)findViewById(R.id.BlueToothList);
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();//蓝牙适配器



        //判断蓝牙是否能正常运行
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "没有发现蓝牙模块", Toast.LENGTH_SHORT).show();
            return;
        } else
            ToastShow("蓝牙设备工作正常");
//            Toast.makeText(this, "蓝牙设备工作正常", Toast.LENGTH_SHORT).show();

        //开启蓝牙
        if (!mBluetoothAdapter.isEnabled())  mBluetoothAdapter.enable();
        else

            ToastShow("蓝牙已开启");
//            Toast.makeText(this,"蓝牙已开启",Toast.LENGTH_SHORT).show();

        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        //获取本机蓝牙地址
        final String address = mBluetoothAdapter.getAddress();

        ToastShow("本机名称：\"+ name+\"|本机地址："+address);
//        Toast.makeText(this,"本机名称："+ name+"|本机地址："+address, Toast.LENGTH_SHORT).show();


/*button test*/
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked button1", Toast.LENGTH_SHORT).show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked button2", Toast.LENGTH_SHORT).show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked button3", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked button4", Toast.LENGTH_SHORT).show();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevices=mBluetoothAdapter.getBondedDevices();
                for(BluetoothDevice bt : pairedDevices) {
                    BluetoothBut.add(bt.getName());
                    BluetoothBut.add(bt.getAddress());
                }
                lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,BluetoothBut));


            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取蓝牙设备物理地址
                String address= BluetoothBut.get(i).toString();
                Toast.makeText(MainActivity.this,address,Toast.LENGTH_SHORT).show();
                //建立新的远程蓝牙设备
                BluetoothDevice bluetoothDevice=mBluetoothAdapter.getRemoteDevice(address);
                //启动连接蓝牙设备子线程
                ConnectThread connecter=new ConnectThread(bluetoothDevice);
                connecter.start();

                //初始化数据传输子线程
                mmSendDataThread=new SendDataThread(connecter.mmSocket);
                mmSendDataThread.start();

//                HeartBeat heartBeat=new HeartBeat(mmSendDataThread);
//                heartBeat.start();
            }
        });


    }

    /*控制蓝牙数据发送频率*/
    int sum;
    @Override
    public boolean onTouchEvent(MotionEvent event){

//        if (MotionEvent.ACTION_MOVE==event.getAction())
        if (MotionEvent.ACTION_DOWN==event.getAction()||MotionEvent.ACTION_MOVE==event.getAction()){
//            int x=(int) event.getX();
            //获取触屏值并做处理
            int y=(int)(2159-event.getY()-359)/7;
            //限定y值上下界
            if(y<0)y=0;
            else if(y>255)y=255;

            /*检测蓝牙是否连接*/
            if(mmSendDataThread==null){
                ToastShow("蓝牙未连接");
                return super.onTouchEvent(event);}

            /*降低数据发送频率
            **待改进*/
            //y值显示
            tv.setText("\n您单击的位置是:\n" + "y:" + y);
            byte[] data = new byte[1];
            data = intToBytes2(y);
            /*速度为0直接停止，不进行数据频率控制*/
            if(y==0)
                mmSendDataThread.write(data);
            if(sum>10) {
                mmSendDataThread.write(data);
                sum=0;
            }
            else
                sum++;
        }
        return super.onTouchEvent(event);
    }



/***************************************方法、类定义****************************************************/


    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public byte[] intToBytes2(int value)
    {
        byte[] src = new byte[1];
        src[0] = (byte) ((value>>0) & 0xFF);
        return src;
    }


    public void ToastShow(String str){
        if(toast==null)
            toast=Toast.makeText(this,str,Toast.LENGTH_LONG);
        else
            toast.setText(str);
        toast.show();
    }


/*  [方法]获取已配对蓝牙设备
**  一种很另类但是很简洁的点击事件触发方式
**  button5触发*/
//    public void list(View view){
//        pairedDevices=mBluetoothAdapter.getBondedDevices();
//        ArrayList BluetoothBut = new ArrayList();
//        for(BluetoothDevice bt : pairedDevices){
//            BluetoothBut.add(bt.getName()+"  "+bt.getAddress());
//        }
////            Toast.makeText(getApplicationContext(), (String)BluetoothBut.get(0), Toast.LENGTH_LONG).show();
//
//        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,BluetoothBut);
//        lv.setAdapter(adapter);
//    }


    /*心跳包*/
//    private class HeartBeat extends Thread {
//
//        public HeartBeat(SendDataThread mmSendDataThread) {
//
//        }
//
//        public void run() {
//            SendDataThread sendDataThread=mmSendDataThread;
//            while (true) {
//                byte[] a={0};
//                sendDataThread.write(a);
//                Looper.prepare();
//                Toast.makeText(MainActivity.this, "beat!", Toast.LENGTH_LONG).show();
//                Looper.loop();
//                try {
//                    sleep(1000);
//                } catch (InterruptedException e) {
//
//                }
//            }
//        }
//
//    }

//设备连接子线程
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
//            Toast.makeText(MainActivity.this,"进入线程",Toast.LENGTH_LONG).show();
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                ToastShow("获取Socket...");
//                Toast.makeText(MainActivity.this,"获取Socket...",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                ToastShow("获取Socket失败");
//                Toast.makeText(MainActivity.this,"获取Socket失败",Toast.LENGTH_LONG).show();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            Log.d("1", "1.1");
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Looper.prepare();
                Toast.makeText(MainActivity.this, "mmSocket.connect()", Toast.LENGTH_LONG).show();
                Looper.loop();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Looper.prepare();
                Toast.makeText(MainActivity.this, "mmSocket.connect()失败", Toast.LENGTH_LONG).show();
                Looper.loop();
                try {
                    mmSocket.close();
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "close socket", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } catch (IOException closeException) {
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, "close socket 失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
                Log.d("1", "1.2");
                return;
            }

            // Do work to manage the connection (in a separate thread)
            // manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    //数据传输子线程
    private class SendDataThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public SendDataThread (BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            Log.d("2", "2");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            //暂不处理
            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(M, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    break;
//                }
                byte[] data ={0};
                try {
                    mmOutStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {

            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


}
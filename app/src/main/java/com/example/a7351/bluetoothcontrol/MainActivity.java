package com.example.a7351.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private TextView tv;

    private Button button1,button2,button3,button4,button5;
    private Set<BluetoothDevice>pairedDevices;
    private ListView lv;

    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        tv= (TextView) findViewById(R.id.tv);

        button1 = (Button) findViewById(R.id.a1);
        button5 = (Button) findViewById(R.id.a5);

        lv = (ListView)findViewById(R.id.BlueToothList);
//        lv.getBackground().getAlpha(40);
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
/*
*   button test
*/
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });



        //判断蓝牙是否能正常运行
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "没有发现蓝牙模块", Toast.LENGTH_SHORT).show();
            return;
        } else
            Toast.makeText(this, "蓝牙设备工作正常", Toast.LENGTH_SHORT).show();


        //开启蓝牙
        if (!mBluetoothAdapter.isEnabled())  mBluetoothAdapter.enable();
        else  Toast.makeText(this,"蓝牙已开启",Toast.LENGTH_SHORT).show();


        /***********
         * 扫描设备
         ********/
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        Toast.makeText(this,"本机名称："+ name, Toast.LENGTH_SHORT).show();
//获取本机蓝牙地址
        String address = mBluetoothAdapter.getAddress();
        Toast.makeText(this,"本机地址："+address,Toast.LENGTH_SHORT).show();




    }



/*  获取已配对蓝牙设备
*   一种很另类但是很简洁的触发方式
*   button5触发
*/
        public void list(View view){
            pairedDevices=mBluetoothAdapter.getBondedDevices();
            ArrayList BluetoothBut = new ArrayList();
            for(BluetoothDevice bt : pairedDevices){
                BluetoothBut.add(bt.getName());
            }
            Toast.makeText(getApplicationContext(), "Showing Paired Devices.", Toast.LENGTH_LONG).show();
            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,BluetoothBut);
            lv.setAdapter(adapter);
        }




    @Override
    public boolean onTouchEvent(MotionEvent event){

        if (MotionEvent.ACTION_MOVE==event.getAction()){
            float x=event.getX();
            float y=-(event.getY()-1100);
            tv.setText("您单击的位置是:\nx:"+x+"\n y:"+y);
        }
        return super.onTouchEvent(event);
    }

}

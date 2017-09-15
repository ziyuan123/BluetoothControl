package com.example.a7351.bluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button btserch;
    private ListView deviceListview;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private List<String> deviceList=new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        button1 = (Button) findViewById(R.id.a1);

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
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }


        /***********
         * 扫描设备
         ********/
        //获取本机蓝牙名称
        String name = mBluetoothAdapter.getName();
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
//获取本机蓝牙地址
        String address = mBluetoothAdapter.getAddress();
        Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
//获取已配对蓝牙设备
        /***********
         * 扫描设备
         ********/
        private void scanLeDevice(final boolean enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (enable) {
                    devices.clear();//清空集合
                    // Stops scanning after a pre-defined scan period.
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            }
                        }
                    }, INTERVAL_TIME);
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                } else {
                    try {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } catch (Exception e) {
                    }
                }
            }
        }

    }









    private void findView() {

        tv= (TextView) findViewById(R.id.tv);
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

    @Override
    public View findViewById(@IdRes int id) {
        return super.findViewById(id);
    }
}

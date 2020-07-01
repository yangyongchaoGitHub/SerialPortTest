package com.wenyuntech.serialporttest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wenyuntech.serialporttest.device.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2017/9/26 0026.
 *
 */
public class MainUI extends Activity implements View.OnClickListener {
    private static final String TAG = "MainUI";
    TextView tv_send;
    TextView tv_rcv;
    EditText et_msg;
    protected FileOutputStream fos;
    protected FileInputStream fis;
    private Thread listen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainui_activity);
        Button btn_at = (Button) findViewById(R.id.btn_at_mainui);
        Button btn_atdefault = (Button) findViewById(R.id.btn_atdefault_mainui);
        Button btn_atname = (Button) findViewById(R.id.btn_atname_center);
        Button btn_send = (Button) findViewById(R.id.btn_send_mainui);
        tv_send  = (TextView) findViewById(R.id.tv_send_mainui);
        tv_rcv = (TextView) findViewById(R.id.tv_rsv_mainui);
        et_msg = (EditText) findViewById(R.id.et_msg_mainui);

        btn_send.setOnClickListener(this);
        btn_at.setOnClickListener(this);
        btn_atdefault.setOnClickListener(this);
        btn_atname.setOnClickListener(this);

        init();

        View rl_root = ((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
        rl_root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(MainUI.this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mAdapter == null) {
            Log.e(TAG, "BluetoothAdapter.getDefaultAdapter() is null device is running?");

        } else if (!mAdapter.isEnabled()) {
            Log.i(TAG, "must enable bluetooth");
            mAdapter.enable();
            //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_mainui:
                String cmd = et_msg.getText().toString();
                send((cmd + " \r").getBytes());
                break;

            case R.id.btn_atname_center:
                String name = et_msg.getText().toString();

                send(("AT+NAME" + name + " \r").getBytes());
                break;
            case R.id.btn_atdefault_mainui:
                send("AT+DEFAULT \r".getBytes());
                break;
            case R.id.btn_at_mainui:
                send(new byte[]{0x41, 0x54, 0x61});
                //send("AT \r".getBytes());
                break;
            default:
        }
    }

    private void init() {
        SerialPort port = null;
        try {
            port = new SerialPort(new File("/dev/ttyACM0"), 115200);
            fos = (FileOutputStream) port.getOutputStream();
            fis = (FileInputStream) port.getInputStream();
            if (listen == null) {
                listen = new Listen(fis);
                listen.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "连接串口失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void send(byte[] msg) {
        try {
            String cmd = "";
            for (int i = 0; i < msg.length; i++) {
                cmd += msg[i];
            }
            Log.i(TAG, "send " + cmd);
            //fos.write(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Listen extends Thread {
        FileInputStream fis;
        boolean runing = true;
        ByteBuffer listenBuffer = ByteBuffer.allocate(256);

        public Listen(FileInputStream fis) {
            this.fis = fis;
        }

        @Override
        public void run() {
            while (runing) {
                try {
                    int bytes = fis.read(listenBuffer.array(), 0, 256);

                    if (bytes < 0) {
                        /**
                         * TODO　need to handle io error
                         */
                        Log.i("IDPBroker ", "listener fis is read err");
                        runing = false;
                        continue;
                    }
                    Log.i(TAG, "to decode");

                    send(decode(bytes));
                    listenBuffer.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        public byte[] decode(int bytes) {
            byte[] recever = new byte[bytes];
            System.arraycopy(listenBuffer.array(), 0, recever, 0, bytes);
            String cmd = "";
            for (int i = 0; i < recever.length; i++) {
                cmd += recever[i];
            }
            Log.i(TAG, "byte =  " + cmd);
            Log.i(TAG, "HEX = " + Helper.getHex(recever));
            return recever;
        }
    }
}

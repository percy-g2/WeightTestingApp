package com.androidevlinux.percy.weighttestingapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import android_serialport_api.sample.SerialPortConnection;
import android_serialport_api.sample.SerialPortListener;
public class MainActivity extends AppCompatActivity implements SerialPortListener {

    public static String weight = "0";
    SerialPortConnection serialPortConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        serialPortConnection = SerialPortConnection.onConnect(MainActivity.this);
        FloatingActionButton fab = findViewById(R.id.fab);
        final TextView txtWeight = findViewById(R.id.txtWeight);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtWeight.setText(weight);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void spDataReceived(byte[] bytes, int numb_of_bytes) {
        if (bytes == null || numb_of_bytes <= 0)
        {
            Log.i("Serial Receiver", " Data empty");
            return ;
        }

        /* Below code is convert ASCII bytes to String */
        StringBuilder sb = new StringBuilder();
        // Loop over all possible ASCII codes.

        for (int i = 0; i < numb_of_bytes; i++) {
            char c = (char)bytes[i];
            String display;
            // Figure out how to display whitespace.
            if (Character.isWhitespace(c)) {
                switch (c) {
                    case '\t':
                        display = "\\t";
                        break;
                    case ' ':
                        display = "space";
                        break;
                    case '\n':
                        display = "\\n";
                        break;
                    case '\r':
                        display = "\\r";
                        break;
                    case '\f':
                        display = "\\f";
                        break;
                    default:
                        display = "whitespace";
                        break;
                }
            } else if (Character.isISOControl(c)) {
                // Handle control chars.
                display = "control";
            } else {
                // Handle other chars.
                display = Character.toString(c);
            }
            // Write a string with padding.
            //System.out.printf("%1$-8d %2$-10s %3$s\n", i, display, Integer.toHexString(i));
            sb.append(display);
        }
        weight = sb.toString();
        System.out.println("Weighing scale :"+weight);
    }

    @Override
    public void spError(String errorLog) {

    }
}

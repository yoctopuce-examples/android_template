package com.yoctopuce.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YTemperature;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
{

    private TextView _temperatureTextView;
    private Handler _handler;
    private Runnable _periodicUpdate = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                if (_hardwaredetect == 0) {
                    YAPI.UpdateDeviceList();
                }
                _hardwaredetect = (_hardwaredetect + 1) % 20;
                if (_sensor == null) {
                    _sensor = YTemperature.FirstTemperature();
                }
                if (_sensor != null && _sensor.isOnline()) {
                    final String text = String.format(Locale.US, "%.2f %s", _sensor.get_currentValue(), _sensor.get_unit());
                    _temperatureTextView.setText(text);
                } else {
                    _temperatureTextView.setText("OFFLINE");
                    _sensor = null;
                }
            } catch (YAPI_Exception e) {
                Snackbar.make(_temperatureTextView, "Error:" + e.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE).show();
            }
            _handler.postDelayed(_periodicUpdate, 500);
        }
    };
    private double _hardwaredetect;
    private YTemperature _sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _temperatureTextView = (TextView) findViewById(R.id.temperature);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        _handler = new Handler();

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        try {
            YAPI.EnableUSBHost(this);
            YAPI.RegisterHub("usb");
        } catch (YAPI_Exception e) {
            Snackbar.make(_temperatureTextView, "Error:" + e.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE).show();
        }
        _handler.postDelayed(_periodicUpdate, 500);
    }


    @Override
    protected void onStop()
    {
        _handler.removeCallbacks(_periodicUpdate);
        YAPI.FreeAPI();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
}

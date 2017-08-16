package com.catennacio.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.catennacio.simpleblescanner.BleScanner;
import com.catennacio.simpleblescanner.BleScannerListener;
import com.catennacio.simpleblescanner.BleScannerOptions;
import com.catennacio.simpleblescanner.LiveBeaconReading;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BleScannerListener
{
    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView tv;
    private boolean isScanning;

    private BleScanner bleScanner;
    MenuItem actionScanMenuItem;

    private final int PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasPermission();

        tv = (TextView) findViewById(R.id.tv);
        tv.setMovementMethod(new ScrollingMovementMethod());

        BleScannerOptions.ScanStrategy scanStrategy;

        try
        {
            //As of Android N DP4 ble scanner cannot be started more than 5 times per 30 seconds, so only use SCAN_STRATEGY_CONTINUOUS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_CONTINUOUS;
            }
            else
            {
                scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_PERIODIC;
            }
            Log.d(TAG, "onCreate: scanStrategy = " + scanStrategy);

            BleScannerOptions bleScannerOptions = new BleScannerOptions(BleScannerOptions.ScanMode.SCAN_MODE_BALANCE,
                scanStrategy
            );

            bleScanner = new BleScanner(this, bleScannerOptions, this);
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        stopScan();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        actionScanMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_scan:
            {
                if (hasPermission())
                {
                    if (isScanning)
                    {
                        stopScan();
                    }
                    else
                    {
                        startScan();
                    }
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat
                .checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat
                .checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
            {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN))
                {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                }
                else
                {
                    ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN},
                        PERMISSIONS_REQUEST
                    );
                }

                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "onRequestPermissionsResult: all permissions granted");
                }
                else
                {
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions);
                }
                return;
            }
        }
    }

    private void startScan()
    {
        Log.d(TAG, "startScan: ");
        if (!isScanning)
        {
            bleScanner.start();
            isScanning = true;
            if (actionScanMenuItem != null)
            {
                actionScanMenuItem.setTitle(this.getResources().getString(R.string.action_stop_scan));
            }
        }
    }

    private void stopScan()
    {
        Log.d(TAG, "stopScan: ");
        if (isScanning)
        {
            bleScanner.stop();
            isScanning = false;
            if (actionScanMenuItem != null)
            {
                actionScanMenuItem.setTitle(this.getResources().getString(R.string.action_start_scan));
            }
        }
    }

    @Override
    public void onBleScannerResult(List<LiveBeaconReading> readings)
    {
        StringBuilder sb = new StringBuilder();
        for (LiveBeaconReading liveBeaconReading : readings)
        {
            sb.append("\n");
            sb.append(liveBeaconReading);
            sb.append("\n");
        }

        tv.append(sb.toString());
    }
}

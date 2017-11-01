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
import android.widget.Toast;

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
    private boolean allPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        allPermissionsGranted = false;
        hasPermission();

        tv = (TextView) findViewById(R.id.tv);
        tv.setMovementMethod(new ScrollingMovementMethod());

        BleScannerOptions.ScanStrategy scanStrategy;

        try
        {
            //As of Android N DP4 ble scanner cannot be started more than 5 times per 30 seconds, so only use SCAN_STRATEGY_CONTINUOUS
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_CONTINUOUS;
            }
            else
            {
                scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_PERIODIC;
            }*/

            scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_CONTINUOUS;
            Log.d(TAG, "onCreate: scanStrategy = " + scanStrategy);

            BleScannerOptions bleScannerOptions = new BleScannerOptions(BleScannerOptions.ScanMode.SCAN_MODE_BALANCE,
                scanStrategy
            );

            bleScanner = new BleScanner(this, bleScannerOptions, this);
            //bleScanner.addUuidToMonitor("b2a946c7-ac8d-48c2-b634-9deead60ea15");
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
                if (allPermissionsGranted)
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
                else
                {
                    hasPermission();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void hasPermission()
    {
        Log.d(TAG, "hasPermission: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(this, "App needs Location permission to scan", Toast.LENGTH_SHORT).show();
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH))
                {
                    Toast.makeText(this, "App needs Bluetooth permission to scan", Toast.LENGTH_SHORT).show();
                }
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN))
                {
                    Toast.makeText(this, "App needs Bluetooth admin permission to scan", Toast.LENGTH_SHORT).show();
                }
            }

            Log.d(TAG, "hasPermission: Requesting permission...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, PERMISSIONS_REQUEST);
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED )
                {
                    Log.d(TAG, "onRequestPermissionsResult: all permissions granted");
                    allPermissionsGranted = true;
                }
                else
                {
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions + " grantResult=" + grantResults);
                }
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
            Log.d(TAG, "onBleScannerResult: " + liveBeaconReading);
        }

        tv.append(sb.toString());
    }
}

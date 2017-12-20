package com.catennacio.simpleblescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duy Nguyen on 12/20/17.
 */

public class BleScannerLegacy extends BleScanner
{
    public static final String TAG = BleScannerLegacy.class.getSimpleName();
    private Handler scanHandler;
    private final Handler runOnUiThreadHandler;
    private List<LiveBeaconReading> readingList;

    protected BleScannerLegacy(@NonNull Context context, @NonNull BleScannerOptions scannerOptions,
        @NonNull final BleScannerListener listener)
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.scannerOptions = scannerOptions;
        this.bleScannerListener = listener;
        scanHandler = new Handler();
        runOnUiThreadHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void start()
    {
        if (!isScanning)
        {
            scanHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    isScanning = false;
                    bluetoothAdapter.stopLeScan(scanCallback);
                    BleScannerLegacy.this.notifyListener(readingList);

                    //keep scanning
                    bluetoothAdapter.startLeScan(scanCallback);
                }
            }, BleScannerOptions.DEFAULT_CONTINUOUS_DISPATCH_INTERVAL);

            readingList = new ArrayList<>();
            bluetoothAdapter.startLeScan(scanCallback);
            isScanning = true;
        }
    }

    private BluetoothAdapter.LeScanCallback scanCallback =
        new BluetoothAdapter.LeScanCallback()
        {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //add to reading list
                        readingList.add(new LiveBeaconReading(device, rssi, scanRecord));
                    }
                });
            }
        };


    @Override
    public void stop()
    {
        if(isScanning)
        {
            bluetoothAdapter.stopLeScan(scanCallback);
        }
        else
        {
            Log.e(TAG, "stop: Already stopped!");
        }
    }

    private void runOnUiThread(Runnable runnable)
    {
        runOnUiThreadHandler.post(runnable);
    }

    private void notifyListener(final List<LiveBeaconReading> readings)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "notifyListener: dispatching " + readings.size() + " readings...");
                bleScannerListener.onBleScannerResult(readings);
                readings.clear();
            }
        });
    }

}

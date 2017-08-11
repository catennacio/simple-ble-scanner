package com.catennacio.simpleblescanner;

import android.bluetooth.le.BluetoothLeScanner;

import java.util.List;

/**
 * Created by Duy Nguyen on 8/4/17.
 */

public class BleScanRunnable implements Runnable
{
    private BluetoothLeScanner scanner;
    private BleScannerOptions scannerOptions;
    private BleScanCallback bleScanCallback;

    public BleScanRunnable(BluetoothLeScanner scanner, BleScannerOptions scannerOptions)
    {
        this.scanner = scanner;
        this.scannerOptions = scannerOptions;
    }

    private void startScan()
    {
        bleScanCallback = new BleScanCallback(scannerOptions.getMonitoredUuids());
        scanner.startScan(null, scannerOptions.getScanSettings(), bleScanCallback);
    }

    private void stopScan()
    {
        scanner.stopScan(bleScanCallback);
    }

    List<LiveBeaconReading> getReadings()
    {
        return bleScanCallback.getReadings();
    }

    @Override
    public void run()
    {
        startScan();
    }

    void stop()
    {
        stopScan();
    }
}

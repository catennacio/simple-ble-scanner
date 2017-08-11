package com.catennacio.simpleblescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

/**
 * Created by Duy Nguyen on 7/31/17.
 */

public class BleScanner
{
    public static final String TAG = BleScanner.class.getSimpleName();

    private BleScannerOptions scannerOptions;
    private boolean isScanning = false;

    private BeaconReadingContinuousDispatcher beaconReadingContinuousDispatcher;
    private BleScannerListener bleScannerListener;

    //need to run on another thread, if not, start/stop bluetooth adapter can block UI thread
    private BleScanRunnable bleScanRunnable;

    private Handler periodicScanHandler;
    private Handler continuousScanHandler;

    private final Handler runOnUiThreadHandler;

    public BleScanner(@NonNull Context context, @NonNull BleScannerOptions scannerOptions, @NonNull final BleScannerListener listener) throws Exception
    {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null || bluetoothManager.getAdapter() == null)
        {
            throw new Exception("No bluetooth adapter.");
        }
        else
        {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            this.scannerOptions = scannerOptions;
            this.bleScannerListener = listener;
            periodicScanHandler = new Handler();
            continuousScanHandler=  new Handler();
            bleScanRunnable = new BleScanRunnable(bluetoothAdapter.getBluetoothLeScanner(), scannerOptions);
            runOnUiThreadHandler = new Handler(context.getMainLooper());
        }
    }

    public void startScan()
    {
        if(!isScanning)
        {
            Log.d(TAG, "startScan: ");
            switch(scannerOptions.getScanStrategy())
            {
                case SCAN_STRATEGY_CONTINUOUS:
                {
                    doContinuousNotify();
                    break;
                }
                default:
                case SCAN_STRATEGY_PERIODIC:
                {
                    doPeriodicScan();
                    break;
                }
            }

            isScanning = true;
        }
    }

    public void stopScan()
    {
        if(isScanning)
        {
            Log.d(TAG, "stopScan: ");
            bleScanRunnable.stop();
            switch(scannerOptions.getScanStrategy())
            {
                case SCAN_STRATEGY_CONTINUOUS:
                {
                    continuousScanHandler.removeCallbacks(continuousNotifyRunnable);
                    break;
                }
                default:
                case SCAN_STRATEGY_PERIODIC:
                {
                    periodicScanHandler.removeCallbacks(periodicScanRunnable);
                    break;
                }
            }
            isScanning = false;
        }
    }

    private Runnable continuousNotifyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            notifyListener(bleScanRunnable.getReadings());
            continuousScanHandler.postDelayed(continuousNotifyRunnable, scannerOptions.getContinuousDispatchInterval());
        }
    };

    private void doContinuousNotify()
    {
        //start the scan on another thread
        bleScanRunnable.run();

        //Notify the listener at every interval
        continuousScanHandler.postDelayed(continuousNotifyRunnable, scannerOptions.getContinuousDispatchInterval());
    }

    private Runnable periodicScanRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            bleScanRunnable.stop();
            notifyListener(bleScanRunnable.getReadings());

            //start the next scan cycle
            doPeriodicScan();
        }
    };

    private void doPeriodicScan()
    {
        //start the scan on another thread
        bleScanRunnable.run();

        //stop scanning and start new scan cycle right away
        periodicScanHandler.postDelayed(periodicScanRunnable, scannerOptions.getPeriodicScanLength());
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
                Log.d(TAG, "run: dispatching " + readings.size() + " readings...");
                bleScannerListener.onBleScannerResult(readings);
                readings.clear();
            }
        });
    }
}

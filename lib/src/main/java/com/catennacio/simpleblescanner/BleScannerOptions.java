package com.catennacio.simpleblescanner;

import android.bluetooth.le.ScanSettings;
import android.os.ParcelFormatException;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;

import java.util.HashSet;

/**
 * Created by Duy Nguyen on 8/1/17.
 */

public class BleScannerOptions
{
    public static final String TAG = BleScannerOptions.class.getSimpleName();

    private HashSet<ParcelUuid> uuids;
    private ScanSettings scanSettings;
    private ScanStrategy scanStrategy;

    //The larger the number the more beacons accumulated, the less frequent you will see a raw update from Azul
    //Do not set more than 1000 because JNI converting table may crash.
    private long periodicScanLength;
    private static long DEFAULT_PERIODIC_SCAN_LENGTH = 1000;//start to listen to ble signal, listen for 1 sec, then stop.

    //The larger the number the longer the algo is notified with the batch of readings, but the more readings it receives
    //Effect: less frequent cyan (prism) dot update, but more accurate.
    private long continuousDispatchInterval;
    private static final long DEFAULT_CONTINUOUS_DISPATCH_INTERVAL = 1000;//send batch of accumulated readings every 1 sec

    public enum ScanMode
    {
        SCAN_MODE_BALANCE,
        SCAN_MODE_LOW_LATENCY,
        SCAN_MODE_LOW_POWER,
        SCAN_MODE_OPPORTUNISTIC
    }

    /*
    * SCAN_STRATEGY_PERIODIC: start BLE adapter, listen for BLE signals for a period of time, stop the BLE adapter, then restart the process
    * SCAN_STRATEGY_CONTINUOUS: start BLE adapter, keep listening for BLE signals until told to stop.
    * Default is SCAN_STRATEGY_PERIODIC. Experiments suggest that we get more BLE readings this way. Don't know why yet.
    * */
    public enum ScanStrategy
    {
        SCAN_STRATEGY_PERIODIC,
        SCAN_STRATEGY_CONTINUOUS//keep scanning to ble signal until manually stopped
    }

    public BleScannerOptions()
    {
        uuids = new HashSet<>();
        setScanMode(ScanMode.SCAN_MODE_BALANCE);
        this.scanStrategy = ScanStrategy.SCAN_STRATEGY_PERIODIC;
        periodicScanLength = DEFAULT_PERIODIC_SCAN_LENGTH;
        continuousDispatchInterval = DEFAULT_CONTINUOUS_DISPATCH_INTERVAL;
    }

    public BleScannerOptions(ScanMode scanMode, ScanStrategy scanStrategy)
    {
        uuids = new HashSet<>();
        setScanMode(scanMode);
        this.scanStrategy = scanStrategy;
        periodicScanLength = DEFAULT_PERIODIC_SCAN_LENGTH;
        continuousDispatchInterval = DEFAULT_CONTINUOUS_DISPATCH_INTERVAL;
    }

    public void setPeriodicScanLength(long millis)
    {
        periodicScanLength = millis;
    }

    public long getPeriodicScanLength()
    {
        return periodicScanLength;
    }

    public void setContinuousDispatchInterval(long millis)
    {
        continuousDispatchInterval = millis;
    }

    public long getContinuousDispatchInterval()
    {
        return continuousDispatchInterval;
    }

    public ScanStrategy getScanStrategy()
    {
        return scanStrategy;
    }

    public ParcelUuid addUuidToMonitor(@NonNull String uuid) throws ParcelFormatException
    {
        ParcelUuid ret = null;
        try
        {
            Identifier identifier = Identifier.parse(uuid);
            ret = ParcelUuid.fromString(identifier.toString());
            uuids.add(ret);
        }
        catch(ParcelFormatException ex)
        {
            throw new ParcelFormatException("UUIDs not in valid format");
        }

        return ret;
    }

    public void removeUuidToMonitor(@NonNull String uuid)
    {
        uuids.remove(uuid);
    }

    public HashSet<ParcelUuid> getMonitoredUuids()
    {
        return uuids;
    }

    public void setScanMode(ScanMode scanMode)
    {
        int mode;
        switch (scanMode)
        {
            case SCAN_MODE_LOW_LATENCY:
            {
                mode = ScanSettings.SCAN_MODE_LOW_LATENCY;
                break;
            }
            case SCAN_MODE_LOW_POWER:
            {
                mode = ScanSettings.SCAN_MODE_LOW_POWER;
                break;
            }
            case SCAN_MODE_OPPORTUNISTIC:
            {
                mode = ScanSettings.SCAN_MODE_OPPORTUNISTIC;
                break;
            }
            case SCAN_MODE_BALANCE:
            default:
            {
                mode = ScanSettings.SCAN_MODE_BALANCED;
                break;
            }
        }

        scanSettings = new ScanSettings.Builder().setScanMode(mode).build();
    }

    public ScanSettings getScanSettings()
    {
        return scanSettings;
    }
}

package com.catennacio.simpleblescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanFilter;
import android.os.Handler;
import android.os.ParcelFormatException;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Duy Nguyen on 12/20/17.
 */

public abstract class BleScanner
{
    public static final String TAG = BleScanner.class.getSimpleName();
    protected BleScannerOptions scannerOptions;
    protected boolean isScanning;
    protected BleScannerListener bleScannerListener;
    protected BluetoothAdapter bluetoothAdapter;
    protected HashSet<ParcelUuid> uuids = new HashSet<>();
    protected List<ScanFilter> scanFilters = new ArrayList<>();

    public abstract void start();
    public abstract void stop();

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

    public boolean isScanning()
    {
        return isScanning;
    }

}

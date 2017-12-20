package com.catennacio.simpleblescanner;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Duy Nguyen on 12/20/17.
 */

public class SimpleBleScanner
{
    public static final String TAG = SimpleBleScanner.class.getSimpleName();
    public static BleScanner newInstance(@NonNull Context context, @NonNull BleScannerOptions scannerOptions, @NonNull final BleScannerListener listener)
    {
        Log.d(TAG, "SDK Version: " + Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= 21)
        {
            return new BleScannerV21(context, scannerOptions, listener);
        }
        else
        {
            return new BleScannerLegacy(context, scannerOptions, listener);
        }
    }
}

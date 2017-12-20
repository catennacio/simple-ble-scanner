package com.catennacio.simpleblescanner;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by Duy Nguyen on 8/10/17.
 */

public class Utils
{
    public static final String TAG = Utils.class.getSimpleName();

    public static String fromByteArrayToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
        {
            hex.append(b);
            hex.append(" ");
        }

        return hex.toString();
    }

    public static UUID getGuidFromByteArray(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isBluetoothOn()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    public static boolean setBluetooth(boolean enable)
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null)
        {
            boolean isEnabled = bluetoothAdapter.isEnabled();
            if (enable && !isEnabled)
            {
                return bluetoothAdapter.enable();
            }
            else if (!enable && isEnabled)
            {
                return bluetoothAdapter.disable();
            }
            // No need to change bluetooth state
            return true;
        }
        else
        {
            Log.e(TAG, "setBluetooth: No bluetooth adapter available!");
            return false;
        }
    }
}
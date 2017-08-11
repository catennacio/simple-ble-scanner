package com.catennacio.simpleblescanner;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by Duy Nguyen on 8/10/17.
 */

public class Utils
{
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
}
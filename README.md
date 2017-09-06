# simple-ble-scanner

A simple Android library to scan for iBeacon using Android core bluetooth stack.

Require SDK >= 22


**1. In AndroidManifest.xml:**

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>

**2. In gradle.build:**

    repositories
    {
        maven
        {
            url  'http://dl.bintray.com/catennacio/android'
        }
    }

    dependencies
    {
        compile 'com.catennacio.android:simple-ble-scanner:1.0.3'
    }

 
**3. In MainActivity.java:**

    public class MainActivity implements BleScannerListener
    {
        BleScanner bleScanner;
        
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            BleScannerOptions.ScanStrategy scanStrategy;
            try
            {
                //Since Android N DP4 ble scanner cannot be started more than 5 times 
                //per 30 seconds, so only use SCAN_STRATEGY_CONTINUOUS     
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_CONTINUOUS;
                }
                else
                {
                    scanStrategy = BleScannerOptions.ScanStrategy.SCAN_STRATEGY_PERIODIC;
                }
              
                Log.d(TAG, "onCreate: scanStrategy = " + scanStrategy);
                BleScannerOptions bleScannerOptions = new BleScannerOptions(BleScannerOptions.ScanMode.SCAN_MODE_BALANCE, scanStrategy);
                bleScanner = new BleScanner(this, bleScannerOptions, this);
                              
                //If you want to scan for specific UUID(s), do this.
                //If not, the library will scan for all UUIDs
                bleScanner.addUuidToMonitor("XXXXXXXX-XXXX-XXXX-XXXX");
                bleScanner.addUuidToMonitor("YYYYYYYY-YYYY-YYYY-YYYY");
            }
            catch (Exception ex)
            {
                Log.e(TAG, "onCreate: " + ex.toString(), ex);
            }
        }
    
	    private void startScan()
	    {
	        bleScanner.start();
        }
      
        private void stopScan()
        {
            bleScanner.stop();
        }
	    
        @Override
        public void onBleScannerResult(List<LiveBeaconReading> readings)
        {
            for(LiveBeaconReading liveBeaconReading : readings)
            {
                Log.d(TAG, liveBeaconReading);
            }
        }
    }
    
 
 Note: 
 - Remember to stop the scan in `onPause()` or `onDestroyed()` to stop the scan
 - Use `bleScanner.isScanning()` to check for status
# simple-ble-scanner

A simple Android library to scan for iBeacon using Android core bluetooth stack

**In gradle.build:**

    repositories
    {
      maven
      {
        url  'http://dl.bintray.com/catennacio/android'
      }
    }

    dependencies
    {
      compile 'com.catennacio.android:simple-ble-scanner:1.0.2'
    }

 
**In MainActivity.java:**

    public class MainActivity implements BleScannerListener
    {
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
           BleScannerOptions.ScanStrategy scanStrategy;
           try
           {
              //As of Android N DP4 ble scanner cannot be started more than 5 times 
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
              bleScannerOptions = new BleScannerOptions(BleScannerOptions.ScanMode.SCAN_MODE_BALANCE, scanStrategy);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "onCreate: " + ex.toString(), ex);
            }
        }
    
	    private void startScan()
	    {
        try
        {
          bleScanner = new BleScanner(this, bleScannerOptions, this);
          
          //If you want to scan for specific UUID(s), do this.
          //If not, the library will scan for all UUIDs
          bleScanner.addUuidToMonitor("XXXXXXXX-XXXX-XXXX-XXXX");
          bleScanner.addUuidToMonitor("YYYYYYYY-YYYY-YYYY-YYYY");
          bleScanner.start();
        }
        catch(Exception ex)
        {
          Log.e(TAG, "Error: "+ ex.toString());
        }
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

package project.mingli.equake;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener {


    TextView mLatitudeTextView, mLongitudeTextView, mXaxisTVacce, mYaxisTVacce, mZaxisTVacce,
            mXaxisTVgyro, mYaxisTVgyro, mZaxisTVgyro,
            mXaxisTVgrav, mYaxisTVgrav, mZaxisTVgrav,
            mXaxisTVlinAcce, mYaxisTVlinAcce, mZaxisTVlinAcce, mXaxisTVmagnetic, mYaxisTVmagnetic, mZaxisTVmagnetic,
            mRotationVec1, mRotationVec2, mRotationVec3;
    private LocationManager mlocationManager;

    private SensorManager sensorManager;
    private Sensor senAccelerometer, senGyroscope, senGravity, senLinearAccelerometer, senMagneticField, senRotationVector;
    private SensorEventListener gyroscopeEventListener, gravityEventListener, acceleroEventListener, linearAcceleroEventListener, magneticFieldEventListener
            , rotationVectorEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);


        mLatitudeTextView = (TextView) findViewById(R.id.latitude);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude);
        mXaxisTVacce = (TextView) findViewById(R.id.xaxis);   mYaxisTVacce = (TextView) findViewById(R.id.yaxis);   mZaxisTVacce = (TextView) findViewById(R.id.zaxis);
        mXaxisTVgyro = (TextView) findViewById(R.id.xaxisgyro);    mYaxisTVgyro = (TextView) findViewById(R.id.yaxisgyro);    mZaxisTVgyro = (TextView) findViewById(R.id.zaxisgyro);
        mXaxisTVgrav = (TextView) findViewById(R.id.xaxisgrav);    mYaxisTVgrav = (TextView) findViewById(R.id.yaxisgrav);    mZaxisTVgrav = (TextView) findViewById(R.id.zaxisgrav);
        mXaxisTVlinAcce = (TextView) findViewById(R.id.xaxis_lin_acce);    mYaxisTVlinAcce = (TextView) findViewById(R.id.yaxis_lin_acce);    mZaxisTVlinAcce = (TextView) findViewById(R.id.zaxis_lin_acce);
        mXaxisTVmagnetic = (TextView) findViewById(R.id.xaxis_magnetic);    mYaxisTVmagnetic = (TextView) findViewById(R.id.xaxis_magnetic);    mZaxisTVmagnetic = (TextView) findViewById(R.id.zaxis_magnetic);
        mRotationVec1 = (TextView) findViewById(R.id.rotationVec_1);    mRotationVec2 = (TextView) findViewById(R.id.rotationVec_2);    mRotationVec3 = (TextView) findViewById(R.id.rotationVec_3);
        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (senAccelerometer == null) {
            Toast.makeText(this, "Device has no Accelerometer Sensor.", Toast.LENGTH_LONG).show();
        } else {
            acceleroEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    Sensor mySensor = sensorEvent.sensor;
                    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        mXaxisTVacce.setText(Float.toString(sensorEvent.values[0]));
                        mYaxisTVacce.setText(Float.toString(sensorEvent.values[1]));
                        mZaxisTVacce.setText(Float.toString(sensorEvent.values[2]));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        senGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (senGyroscope == null) {
            Toast.makeText(this, "Device has no Gyroscope Sensor.", Toast.LENGTH_LONG).show();
        } else {

            gyroscopeEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    mXaxisTVgyro.setText(Float.toString(sensorEvent.values[0]));
                    mYaxisTVgyro.setText(Float.toString(sensorEvent.values[1]));
                    mZaxisTVgyro.setText(Float.toString(sensorEvent.values[2]));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        senGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (senGravity == null)
            Toast.makeText(this, "Device has no Gravity Sensor.", Toast.LENGTH_LONG).show();
        else {
            gravityEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    mXaxisTVgrav.setText(Float.toString(sensorEvent.values[0]));
                    mYaxisTVgrav.setText(Float.toString(sensorEvent.values[1]));
                    mZaxisTVgrav.setText(Float.toString(sensorEvent.values[2]));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        senLinearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (senLinearAccelerometer == null)
            Toast.makeText(this, "Device has no LinearAccelerometer Sensor.", Toast.LENGTH_LONG).show();
        else {
            linearAcceleroEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    mXaxisTVlinAcce.setText(Float.toString(sensorEvent.values[0]));
                    mYaxisTVlinAcce.setText(Float.toString(sensorEvent.values[1]));
                    mZaxisTVlinAcce.setText(Float.toString(sensorEvent.values[2]));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        senMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (senMagneticField == null)
            Toast.makeText(this, "Device has no Magnetic Field Sensor.", Toast.LENGTH_LONG).show();
        else {
            magneticFieldEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    mXaxisTVmagnetic.setText(Float.toString(sensorEvent.values[0]));
                    mYaxisTVmagnetic.setText(Float.toString(sensorEvent.values[1]));
                    mZaxisTVmagnetic.setText(Float.toString(sensorEvent.values[2]));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        senRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(senRotationVector == null)
            Toast.makeText(this, "Device has no Proximity Sensor.", Toast.LENGTH_LONG).show();
        else    {
           rotationVectorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    mRotationVec1.setText(Float.toString(sensorEvent.values[0]));
                    mRotationVec2.setText(Float.toString(sensorEvent.values[1]));
                    mRotationVec3.setText(Float.toString(sensorEvent.values[2]));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };
        }

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute("10.4444","20.44444");

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*There are two other methods that we need to override, onPause and onResume.
     These are methods of the Main class.
     It's good practice to unregister the sensor when the application hibernates and register the sensor again when the application resumes.
     Take a look at the code snippets below to get an idea of how this works in practice.
     */

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(acceleroEventListener);
        sensorManager.unregisterListener(gyroscopeEventListener);
        sensorManager.unregisterListener(gravityEventListener);
        sensorManager.unregisterListener(linearAcceleroEventListener);
        sensorManager.unregisterListener(magneticFieldEventListener);
        sensorManager.unregisterListener(rotationVectorEventListener);
        removeLocationUpdates();
    }

    private void removeLocationUpdates() {
        if(mlocationManager != null)
            mlocationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( checkLocation())   //  //check whether location service is enable or not in your  phone
        requestLocationUpdates();
        sensorManager.registerListener(acceleroEventListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeEventListener, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gravityEventListener, senGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(linearAcceleroEventListener, senLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magneticFieldEventListener, senMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotationVectorEventListener, senRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void requestLocationUpdates() {
        if (mlocationManager != null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)  ||
                mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert()    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                    .setMessage("Location settings is off. \nPlease Enable location to use the app.")
                    .setPositiveButton("Location settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
            mLatitudeTextView.setText(""+location.getLatitude());
            mLongitudeTextView.setText(""+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }



    class BackgroundTask extends AsyncTask<String, Void, String>    {
        Context context;
        AlertDialog alertDialog;

        BackgroundTask(Context context)    {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected String doInBackground(String... params) {
            String insert_url = "http://134.197.29.153/conn.php";
            String latitude = params[0];
            String longitude = params[1];

            try {
                URL url = new URL(insert_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            //    String data = URLEncoder.encode("lat","UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")+"&"
            //            + URLEncoder.encode("lon","UTF-8")+"="+URLEncoder.encode(longitude,"UTF-8");
            //    bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String result="";
                String line="";
                while ((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
}

/*    ACCELEROMETER SENSOR
    private boolean mInitialized = false;
    private final float NOISE = (float)1.0;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    The statically declared SHAKE_THRESHOLD variable is used to see whether a shake gesture has been detected or not. Modifying SHAKE_THRESHOLD increases or decreases the sensitivity so feel free to play with its value.
    private static final int SHAKE_THRESHOLD = 600;     */

    /*        if(!mInitialized)   {
                last_x = x; last_y = y; last_z =z;
                mInitialized = true;
            }   else    {
                float deltax = Math.abs(last_x - x);
                float deltay = Math.abs(last_y - y);
                float deltaz = Math.abs(last_z - z);
                if(deltax < NOISE)  deltax = (float)0.0;
                if(deltay < NOISE)  deltay = (float)0.0;
                if(deltaz < NOISE)  deltaz = (float)0.0;
                last_x =x;  last_y =y;  last_z =z;
                mXaxisTextView.setText(Float.toString(deltax));
                mYaxisTextView.setText(Float.toString(deltay));
                mZaxisTextView.setText(Float.toString(deltaz));
            }
    */
/* The system's sensors are incredibly sensitive.
When holding a device in your hand, it is constantly in motion,
no matter how steady your hand is.
The result is that the onSensorChanged method is invoked several times per second.
We don't need all this data so we need to make sure we only sample a subset of the data we get from the device's accelerometer.
We store the system's current time (in milliseconds) store it in curTime and check whether more than 100 milliseconds have passed since the last time onSensorChanged was invoked.
 */
  /*          long curTime = System.currentTimeMillis();
            if((curTime - lastUpdate) > 100)    {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime*10000;
                if(speed > SHAKE_THRESHOLD) {
                    mXaxisTextView.setText(""+last_x);
                    mYaxisTextView.setText(""+last_y);
                    mZaxisTextView.setText(""+last_z);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }   */

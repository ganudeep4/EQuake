package project.com.equake;



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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


public class MainActivity extends AppCompatActivity     {

    private final String LOG = "Check";

    TextView mLatitudeTextView, mLongitudeTextView, mXaxisTVacce, mYaxisTVacce, mZaxisTVacce,
    mXaxisTVgyro, mYaxisTVgyro, mZaxisTVgyro,
    mXaxisTVgrav, mYaxisTVgrav, mZaxisTVgrav,
    mXaxisTVlinAcce, mYaxisTVlinAcce, mZaxisTVlinAcce, mXaxisTVmagnetic, mYaxisTVmagnetic, mZaxisTVmagnetic,
    mRotationVec1, mRotationVec2, mRotationVec3;

    private LocationManager mlocationManager;
    private LocationListener locationListenerGps, locationListenerNetwork;

    private SensorManager sensorManager;
    private Sensor senAccelerometer, senGyroscope, senGravity, senLinearAccelerometer, senMagneticField, senRotationVector;
    private SensorEventListener gyroscopeEventListener, gravityEventListener, acceleroEventListener, linearAcceleroEventListener, magneticFieldEventListener, rotationVectorEventListener;

 //   FirebaseDatabase firebaseDb;
 //   DatabaseReference dbRef, dbRegId;
    private String androidId;
    private String uniqueKey;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    PhoneReading phoneReading;

    long myCurrentTimeMillis1, myCurrentTimeMillis2, myCurrentTimeMillis3,
    myCurrentTimeMillis4, myCurrentTimeMillis5;

    private String latitude, longitude;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG, "onCreate()");
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    /*    firebaseDb = FirebaseDatabase.getInstance();
        dbRegId = firebaseDb.getReference("UserId");
        uniqueKey = firebaseDb.getReference("Users").child(androidId).push().getKey();
        dbRef = firebaseDb.getReference("Users").child(androidId);
    */

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:f955a44b-a2bd-488b-a5f5-a4db8224d692",    /* Identity Pool ID */
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        phoneReading = new PhoneReading();
        phoneReading.setPhoneId(androidId);

    //    Toast.makeText(this, "Unique-key for this phone:  "+uniqueKey, Toast.LENGTH_LONG).show();

        myCurrentTimeMillis1 = System.currentTimeMillis();
        myCurrentTimeMillis2 = System.currentTimeMillis();
        myCurrentTimeMillis3 = System.currentTimeMillis();
        myCurrentTimeMillis4 = System.currentTimeMillis();
        myCurrentTimeMillis5 = System.currentTimeMillis();

        mLatitudeTextView = (TextView) findViewById(R.id.latitude);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude);
        mXaxisTVacce = (TextView) findViewById(R.id.xaxis);
        mYaxisTVacce = (TextView) findViewById(R.id.yaxis);
        mZaxisTVacce = (TextView) findViewById(R.id.zaxis);
        mXaxisTVgyro = (TextView) findViewById(R.id.xaxisgyro);
        mYaxisTVgyro = (TextView) findViewById(R.id.yaxisgyro);
        mZaxisTVgyro = (TextView) findViewById(R.id.zaxisgyro);
        mXaxisTVgrav = (TextView) findViewById(R.id.xaxisgrav);
        mYaxisTVgrav = (TextView) findViewById(R.id.yaxisgrav);
        mZaxisTVgrav = (TextView) findViewById(R.id.zaxisgrav);
        mXaxisTVlinAcce = (TextView) findViewById(R.id.xaxis_lin_acce);
        mYaxisTVlinAcce = (TextView) findViewById(R.id.yaxis_lin_acce);
        mZaxisTVlinAcce = (TextView) findViewById(R.id.zaxis_lin_acce);
        mXaxisTVmagnetic = (TextView) findViewById(R.id.xaxis_magnetic);
        mYaxisTVmagnetic = (TextView) findViewById(R.id.yaxis_magnetic);
        mZaxisTVmagnetic = (TextView) findViewById(R.id.zaxis_magnetic);
        mRotationVec1 = (TextView) findViewById(R.id.rotationVec_1);
        mRotationVec2 = (TextView) findViewById(R.id.rotationVec_2);
        mRotationVec3 = (TextView) findViewById(R.id.rotationVec_3);



        mlocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            Log.d(LOG, "onCreate(): isProviderEnabled().");
        else
            showAlert();
        locationListenerGps = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOG, "onLocationChanged(). GPS");
                String key= "";
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                mLatitudeTextView.setText("" + latitude);
                mLongitudeTextView.setText("" + longitude);
                if(latitude!=null && longitude!=null)   {
                    phoneReading.setLatitude(latitude);
                    phoneReading.setLongitude(longitude);
                    mapper.save(phoneReading);
                }
    /*            key = dbRef.child("Latitude").push().getKey();
                dbRef.child("Latitude").child(key).setValue(location.getLatitude());
                dbRef.child("Longitude").child(key).setValue(location.getLongitude());
    */
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
        };



        locationListenerNetwork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOG, "onLocationChanged().  NETWORK");
                String key= "";
                mLatitudeTextView.setText("" + location.getLatitude());
                mLongitudeTextView.setText("" + location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                mLatitudeTextView.setText("" + latitude);
                mLongitudeTextView.setText("" + longitude);
                if(latitude!=null && longitude!=null)   {
                    phoneReading.setLatitude(latitude);
                    phoneReading.setLongitude(longitude);
                    mapper.save(phoneReading);
                }

    /*            key = dbRef.child("Latitude").push().getKey();
                dbRef.child("Latitude").child(key).setValue(location.getLatitude());
                dbRef.child("Longitude").child(key).setValue(location.getLongitude());
     */       }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };


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
                        long myCurrentTimeMillis = System.currentTimeMillis();
                        if(myCurrentTimeMillis-myCurrentTimeMillis1 >= 1000)    {
    //                        String key = dbRef.child("Accelerometer X-axis").push().getKey();
                            //                        dbRef.child("Accelerometer X-axis").child(key).setValue(Float.toString(sensorEvent.values[0]));
                            //                        dbRef.child("Accelerometer Y-axis").child(key).setValue(Float.toString(sensorEvent.values[1]));
                            //                        dbRef.child("Accelerometer Z-axis").child(key).setValue(Float.toString(sensorEvent.values[2]));
                            //                        Log.d(LOG, "acce 1 second.");
                            myCurrentTimeMillis1 = System.currentTimeMillis();
                        }
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
                    long myCurrentTimeMillis = System.currentTimeMillis();
                    if(myCurrentTimeMillis-myCurrentTimeMillis2 >= 5000)    {
    /*                    String key = dbRef.child("GyroScope X-axis").push().getKey();
                        dbRef.child("GyroScope X-axis").child(key).setValue(Float.toString(sensorEvent.values[0]));
                        dbRef.child("GyroScope Y-axis").child(key).setValue(Float.toString(sensorEvent.values[1]));
                        dbRef.child("GyroScope Z-axis").child(key).setValue(Float.toString(sensorEvent.values[2]));
                        //                    Log.d(LOG, "Gyro 5 second.");
     */                   myCurrentTimeMillis2 = System.currentTimeMillis();
                    }
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
                    long myCurrentTimeMillis = System.currentTimeMillis();
                    if(myCurrentTimeMillis-myCurrentTimeMillis3 >= 1000)    {
    //                    String key = dbRef.child("Gravity X-axis").push().getKey();
                        //                    dbRef.child("Gravity X-axis").child(key).setValue(Float.toString(sensorEvent.values[0]));
                        //                    dbRef.child("Gravity Y-axis").child(key).setValue(Float.toString(sensorEvent.values[1]));
                        //                    dbRef.child("Gravity Z-axis").child(key).setValue(Float.toString(sensorEvent.values[2]));
                        //                    Log.d(LOG, "Grav 1 second.");
                        myCurrentTimeMillis3 = System.currentTimeMillis();
                    }
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
                    long myCurrentTimeMillis = System.currentTimeMillis();
                    if(myCurrentTimeMillis-myCurrentTimeMillis4 >= 1000)    {
     //                   String key = dbRef.child("LinearAccelerometer X-axis").push().getKey();
                        //                    dbRef.child("LinearAccelerometer X-axis").child(key).setValue(Float.toString(sensorEvent.values[0]));
                        //                    dbRef.child("LinearAccelerometer Y-axis").child(key).setValue(Float.toString(sensorEvent.values[1]));
                        //                    dbRef.child("LinearAccelerometer Z-axis").child(key).setValue(Float.toString(sensorEvent.values[2]));
                        //                    Log.d(LOG, "Line_Acce 1 second.");
                        myCurrentTimeMillis4 = System.currentTimeMillis();
                    }
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
                    long myCurrentTimeMillis = System.currentTimeMillis();
                    if(myCurrentTimeMillis-myCurrentTimeMillis5 >= 1000)    {
    //                    String key = dbRef.child("MagneticField X-axis").push().getKey();
                        //                    dbRef.child("MagneticField X-axis").child(key).setValue(Float.toString(sensorEvent.values[0]));
                        //                    dbRef.child("MagneticField Y-axis").child(key).setValue(Float.toString(sensorEvent.values[1]));
                        //                    dbRef.child("MagneticField Z-axis").child(key).setValue(Float.toString(sensorEvent.values[2]));
                        //                    Log.d(LOG, "Magnetic 1 second.");
                        myCurrentTimeMillis5 = System.currentTimeMillis();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
        }


        senRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (senRotationVector == null)
            Toast.makeText(this, "Device has no Proximity Sensor.", Toast.LENGTH_LONG).show();
        else {
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
     These are methods of the Main class
     It's good practice to unregister the sensor when the application hibernates and register the sensor again when the application resumes.
     Take a look at the code snippets below to get an idea of how this works in practice.
     */

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG, "onPause()");
        mlocationManager.removeUpdates(locationListenerGps);
        mlocationManager.removeUpdates(locationListenerNetwork);
        sensorManager.unregisterListener(acceleroEventListener);
        sensorManager.unregisterListener(gyroscopeEventListener);
        sensorManager.unregisterListener(gravityEventListener);
        sensorManager.unregisterListener(linearAcceleroEventListener);
        sensorManager.unregisterListener(magneticFieldEventListener);
        sensorManager.unregisterListener(rotationVectorEventListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG, "onResume()");
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

        if (mlocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            mlocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListenerNetwork);
        if (mlocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListenerGps);
        sensorManager.registerListener(acceleroEventListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeEventListener, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gravityEventListener, senGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(linearAcceleroEventListener, senLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(magneticFieldEventListener, senMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(rotationVectorEventListener, senRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void showAlert() {
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
                        //                finish();
                    }

                });
        dialog.show();
    }
}





// Inside onStatusChanged() method
/*    if (s.equals(providerFine)) {
            if (i == 0) {
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
                mlocationManager.requestLocationUpdates(providerCoarse, 1000, 0, this);
                Log.d(LOG, "onStatusChanged(): providerCoarse.");
            }
            if (i == 2) {
                mlocationManager.requestLocationUpdates(providerFine, 1000, 0, this);
                Log.d(LOG, "onStatusChanged(): providerFine.");
            }
        }   */


// Inside onResume() method. After if{} statement.
 /*    if (providerCoarse != null) {
            mlocationManager.requestLocationUpdates(providerCoarse, 1000, 0, this);
            Log.d(LOG, "onResume(): providerCoarse.");
        } else if (providerFine != null) {
            Log.d(LOG, "onResume(): providerFine.");
            mlocationManager.requestLocationUpdates(providerFine, 1000, 0, this);
        }
     */
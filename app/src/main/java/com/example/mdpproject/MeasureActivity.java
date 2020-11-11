package com.example.mdpproject;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.Provider;
import java.util.ArrayList;
import java.util.UUID;

public class MeasureActivity extends AppCompatActivity implements SensorEventListener {

    private static final int REQUEST_ENABLE_BT = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    private final static String TAG = MeasureActivity.class.getSimpleName();

    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    String name,user, gender, height, weight;

    private SensorManager sensorManager;
    TextView tvSteps, tvCalories, tvDistance;
    private Sensor stepdetectorSensor;
    Integer StepCounter = 0;
    Vibrator vibrator;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner = null;
    private BluetoothGatt bluetoothGatt;
    private boolean mScanning = false;
    private Handler handler;
    private ListView listView;
    private ArrayList leDevices;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        //Getting the Intent
        Intent i = getIntent();
        //Getting the Values from First Activity using the Intent received
        name=i.getStringExtra("name_key");
        user=i.getStringExtra("user_key");
        gender=i.getStringExtra("gender_key");
        height=i.getStringExtra("height_key");
        weight=i.getStringExtra("weight_key");

        tvSteps = findViewById(R.id.stepdetectorMeasurement); // sensor's values
        tvCalories = findViewById(R.id.caloriesMeasurement); // calories's values
        tvDistance = findViewById(R.id.distanceMeasurement); // distance's values
        listView = findViewById(R.id.leList);
        leDevices = new ArrayList();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler();

        // Get the reference to the sensor manager and the sensor:
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Obtain the reference to the default accelerometer sensor of the device:
        stepdetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // register listener and make the appropriate changes in the UI:
        sensorManager.registerListener(MeasureActivity.this, stepdetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        tvSteps.setText("Waiting for first step detector sensor value");

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        scanLeDevice(bluetoothAdapter.isEnabled());
    }
    private void vibrateForTime(int ms) {
        vibrator.vibrate(ms);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // Show the sensor's value in the UI:
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                break;
            case Sensor.TYPE_ACCELEROMETER:
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                StepCounter++;
                tvSteps.setText(Integer.toString(StepCounter));

                if (StepCounter%1000 == 0) {
                    vibrateForTime(1000);
                    Toast.makeText(this, "Device is vibrating", Toast.LENGTH_SHORT).show();
                }
                break;
            case Sensor.TYPE_PROXIMITY:
                break;
        }

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            scanner.startScan(scanCallback);
        } else {
            mScanning = false;
            scanner.stopScan(scanCallback);
        }
    }

    private  ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String name = result.getDevice().getName();
            if (name != null && !leDevices.contains(name)) {
                leDevices.add(result.getDevice().getName());
            }
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            ArrayAdapter leDeviceListAdapter = new ArrayAdapter(MeasureActivity.this,
                    android.R.layout.simple_list_item_single_choice,
                    leDevices);
            listView.setAdapter(leDeviceListAdapter);
            leDeviceListAdapter.notifyDataSetChanged();
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        bluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                Log.i("TAG", "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is carried out as per profile specifications.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    /*// Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read or notification operations.

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MeasureActivity.ACTION_GATT_CONNECTED.equals(action)) {
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (MeasureActivity.ACTION_GATT_DISCONNECTED.equals(action)) {
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (MeasureActivity.
                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(bluetoothLeService.getSupportedGattServices());
            } else if (MeasureActivity.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(MeasureActivity.EXTRA_DATA));
            }
        }
    };*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
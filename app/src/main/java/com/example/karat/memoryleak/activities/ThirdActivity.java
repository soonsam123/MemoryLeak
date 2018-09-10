package com.example.karat.memoryleak.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.karat.memoryleak.R;

import java.util.concurrent.TimeUnit;

/**
 * This Activity shows how to do and how to prevent a memory leak
 * from a Listener. It register a {@link LocationManager} and
 * a {@link CustomListener}, if you do not remove the listener
 * in {@link #onDestroy()} method you will cause a memory leak
 * when the activity is destroyed, for example when the user
 * rotates the device.
 */
public class ThirdActivity extends AppCompatActivity {

    private LocationManager mManager;
    private CustomListener mListener = new CustomListener();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerLocationUpdates();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        registerLocationUpdates();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Delete the following line to cause the memory leak.
        mManager.removeUpdates(mListener);
    }

    private void registerLocationUpdates() {
        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    TimeUnit.MINUTES.toMillis(1),
                    100
                    , mListener);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    7);
        }
    }

    private class CustomListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.i("Location", "onLocationChanged: Location has changed");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("Location", "onStatusChanged: Status has changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("Location", "onProviderEnabled: Provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("Location", "onProviderDisabled: Provider disabled");
        }
    }

    // ---------------------------------------------------------------------
    //                                  Menu
    // ---------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main:
                startActivity(new Intent(ThirdActivity.this, MainActivity.class));
                break;
            case R.id.menu_second:
                startActivity(new Intent(ThirdActivity.this, SecondActivity.class));
                break;
            case R.id.menu_third:
                startActivity(new Intent(ThirdActivity.this, ThirdActivity.class));
                break;
            case R.id.menu_fourth:
                startActivity(new Intent(ThirdActivity.this, FourthActivity.class));
                break;
            case R.id.menu_retrofit:
                startActivity(new Intent(ThirdActivity.this, RetrofitActivity.class));
                break;
            case R.id.menu_handle_cc:
                startActivity(new Intent(ThirdActivity.this, HandleCCActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

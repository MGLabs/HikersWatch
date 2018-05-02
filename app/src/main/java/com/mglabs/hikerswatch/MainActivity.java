package com.mglabs.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override //questo è il metodo che gestisce lo yes/no del dialog che richiede i permessi
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //abbiamo i permessi ma we still have to check explicitly otherwise the code wont compile
            startListening();

        }
    }

    public void startListening() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //then we can start our location manager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
    }


    @SuppressLint("MissingPermission")  //in realtà il controllo c'è
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //permissions
        if (Build.VERSION.SDK_INT < 23) {

            startListening();

        } else {
            //if permission is not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //ask permission  (1 è la request identifier for the other end. In questo caso abbiamo una richiesta sola quindi non è importante.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                //if permission already granted
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {

                    updateLocationInfo(location);

                }
            }

        }
    }

    public void updateLocationInfo(Location location) {
        //Log.i("LocationInfo", location.toString());

        TextView latTextView = findViewById(R.id.latTextView);
        TextView lonTextView = findViewById(R.id.lonTextView);
        TextView altTextView = findViewById(R.id.altTextView);
        TextView accTextView = findViewById(R.id.accTextView);

        latTextView.setText("Latitude: " + location.getLatitude());
        lonTextView.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());

        //Geocoder object for the address
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address = "Could not find address";
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addressList != null && addressList.size() > 0) {

                Log.i("PlaceInfo", addressList.get(0).toString());
                address = "";

                if (addressList.get(0).getSubThoroughfare() != null) {

                    address += addressList.get(0).getSubThoroughfare() + "";
                }

                if (addressList.get(0).getThoroughfare() != null) {

                    address += addressList.get(0).getThoroughfare() + "\n";
                }

                if (addressList.get(0).getLocality() != null) {

                    address += addressList.get(0).getLocality() + "\n";
                }

                if (addressList.get(0).getPostalCode() != null) {

                    address += addressList.get(0).getPostalCode() + "\n";
                }

                if (addressList.get(0).getCountryName() != null) {

                    address += addressList.get(0).getCountryName() + "\n";
                }
            }

            TextView addressTextView = findViewById(R.id.addTextView);
            addressTextView.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

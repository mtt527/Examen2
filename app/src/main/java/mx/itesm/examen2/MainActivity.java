package mx.itesm.examen2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private  static Context context;

    TextView latitudeText;
    TextView longitudeText;
    TextView altitudeText;
    TextView accuracyText;
    TextView speedText;
    TextView bearingText;
    TextView addressText;

    LocationManager locationManager;
    String provider;
    String message1 = "You must give a latitude value ";
    String message2 = "You must give a longitude value ";


    Double latitudeG ;
    Double longitudeG;

   MyLocationListener locationListener;

    public void getLocation(View view)
    {
        SharedPreferences locationData = getSharedPreferences("Location_Data", 0);
        SharedPreferences.Editor locationDataEditor = locationData.edit();

        String sLat = latitudeG.toString();
        String sLon = longitudeG.toString();

        if (sLat == null)
        {
            Toast.makeText(getApplicationContext(), message1, Toast.LENGTH_LONG).show();
        }
        if (sLon == null)
        {
            Toast.makeText(getApplicationContext(), message2, Toast.LENGTH_LONG).show();
        }
        locationDataEditor.putString("Latitude", sLat);
        locationDataEditor.commit();

        locationDataEditor.putString("Longitude", sLon);
        locationDataEditor.commit();

        startActivity(new Intent("mx.itesm.MAP"));

        //this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        latitudeText = (TextView)findViewById(R.id.latitudeLabel);
        longitudeText = (TextView)findViewById(R.id.longitudeLabel);
        altitudeText = (TextView)findViewById(R.id.altitudeLabel);
        accuracyText = (TextView)findViewById(R.id.accuracyLabel);
        speedText = (TextView)findViewById(R.id.speedLabel);
        bearingText = (TextView)findViewById(R.id.bearingLabel);
        addressText = (TextView)findViewById(R.id.addressLabel);

        SharedPreferences locationData = getSharedPreferences("Location_Data", 0);

        latitudeG = Double.parseDouble(locationData.getString("Latitud", "0"));
        longitudeG = Double.parseDouble(locationData.getString("Longitud", "0"));

        context = getApplicationContext();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        locationListener = new MyLocationListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, locationListener);
    }

    @Override
    protected void onPause() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    public class MyLocationListener extends Activity implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            Double alt = location.getAltitude();

            Float acu = location.getAccuracy();
            Float spd = location.getSpeed();
            Float bea = location.getBearing();

            String addressLine = "";
            String locality = "";
            String countryCode= "";

            Log.i("Latitude>> ", lat.toString());
            Log.i("Longitude>> ", lon.toString());
            Log.i("Altitude>> ", alt.toString());
            Log.i("Acuracy>> ", acu.toString());
            Log.i("Speed>> ", spd.toString());
            Log.i("Bearing>> ", bea.toString());

            latitudeG = lat;
            longitudeG = lon;
            try{
                List<Address> listAdresses = geocoder.getFromLocation(lat,lon,1);
                if((listAdresses != null) && (listAdresses.size() > 0))
                {
                    Log.i("Adress>> ", listAdresses.get(0).toString());
                    addressLine = listAdresses.get(0).getAddressLine(0);
                    locality = listAdresses.get(0).getLocality();
                    countryCode = listAdresses.get(0).getCountryCode();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            /*mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Your Position"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10));*/
            updateLabels(lat, lon, alt, acu, spd, bea, addressLine, locality, countryCode);

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
    }

    public static Context getContext()
    {
        return context;
    }

    public void updateLabels(Double lati, Double longi, Double alti, Float accu, Float speed, Float bear, String addressLine, String locality, String countryCode)
    {
        latitudeText.setText("Latitude: "+lati.toString());
        longitudeText.setText("Longitude: "+longi.toString());
        altitudeText.setText("altitude: " + alti.toString());
        accuracyText.setText("Accuracy: " + accu.toString());
        speedText.setText("Speed: " + speed.toString());
        bearingText.setText("Bearing: " + bear.toString());
        addressText.setText("Address: "+ addressLine + "\n" + locality + "\n" + countryCode + "\n");
    }

}

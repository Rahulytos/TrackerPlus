package com.example.user.tracker;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    MediaPlayer player;
    private Marker myMarker;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    public String location;
    LocationManager locationManager;
    public double cur_longitude;
    public double cur_latitude;
    public double des_long;
    public  double des_lat;
    public LatLng destination,current;
    public float distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    cur_latitude = location.getLatitude();
                    cur_longitude = location.getLongitude();

                    current=new LatLng(cur_latitude,cur_longitude);
                    //  Toast.makeText(getApplicationContext(),current.toString(),Toast.LENGTH_LONG).show();
                    if(myMarker!=null)
                    {
                        destination=myMarker.getPosition();

 /*                     Location locationA=new Location("Destination");
                        locationA.setLatitude(cur_latitude);
                        locationA.setLongitude(cur_longitude);
                        Location locationB=new Location("Current");
                        locationB.setLatitude(des_lat);
                        locationB.setLongitude(des_long);
                         distance=locationA.distanceTo(locationB);
*/
                        CalculationByDistance(destination,current);
/*
                        float[] results=new float[1];
                        Location.distanceBetween(cur_latitude,cur_longitude,des_lat,des_long,results);

                        results[0]=(results[0]/1000);
                        Toast.makeText(getApplicationContext(),Float.toString(results[0]),Toast.LENGTH_LONG).show();
*/

                        //double result= CalculationByDistance(current,destination);


                    }


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
            });
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener)this);





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }









    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"This app requeire location permission to be granted",Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
                break;

        }
    }



    public void onMapSearch(View view) {//location search bar
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            if(myMarker!=null)
            {
                myMarker.remove();
            }

            myMarker=mMap.addMarker(new MarkerOptions().position(latLng).title(location).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(),10.2f));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {//marker click event
        AlertDialog.Builder altdial=new AlertDialog.Builder(Map.this);
        altdial.setMessage("Do you want to set alarm here"+"??").setCancelable(false)//add the name of current location in the alert box
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        // startService(new Intent(getBaseContext(),gps_service.class).putExtra("destination", latLng.toString()));

                        myMarker=null;
                         //   player.stop();
                       // dialog.cancel();




                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  stopService(new Intent(getBaseContext(),gps_service.class));
                        destination=myMarker.getPosition();
                        myMarker=mMap.addMarker(new MarkerOptions().position(destination).title(location).draggable(true));
                        player.stop();
                        myMarker=null;

                        dialog.cancel();
                    }
                });

        AlertDialog alert=altdial.create();
        alert.setTitle("Alarm");
        alert.show();
        myMarker.remove();
        return false;
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        if(km<1) {

             player= MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
            player.setLooping(true);
            player.start();
            Toast.makeText(this, Double.toString(km), Toast.LENGTH_SHORT).show();
        }
        if(myMarker==null)
        {
            player.stop();
        }
    /*DecimalFormat newFormat = new DecimalFormat("####");
    int kmInDec =  Integer.valueOf(newFormat.format(km));
    double meter=valueResult%1000;
    int  meterInDec= Integer.valueOf(newFormat.format(meter));
    Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);*/

        return Radius * c;
    }


}


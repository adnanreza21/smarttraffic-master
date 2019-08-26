package aplikasiku.navwithdirectionlib;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

import aplikasiku.navwithdirectionlib.FDBMobel.Hospital;

public class InserterHospitalActivity extends AppCompatActivity {

    EditText editTextRSname, editTextRSloc, editTextRSlat, editTextRSlong;
    Button butGPS, butInsert;

    GPS gps;

    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserter_hospital);

        init();
    }

    private void init() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        editTextRSname = (EditText) findViewById(R.id.editTextRSname);
        editTextRSloc = (EditText) findViewById(R.id.editTextRSloc);
        editTextRSlat = (EditText) findViewById(R.id.editTextRSlat);
        editTextRSlong = (EditText) findViewById(R.id.editTextRSlong);

        butGPS = (Button) findViewById(R.id.buttonInsertRSgetGPS);
        butInsert = (Button) findViewById(R.id.buttonInsertRSok);

        butGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGPSLoc();
            }
        });

        butInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputIsComplete()){
                    insertFirebaseRS();
                }else{
                    Toast.makeText(getApplicationContext(), "Please complete all input column", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertFirebaseRS(){
        final ProgressDialog pdDialog = ProgressDialog.show(InserterHospitalActivity.this, "Insert RS", "Please wait...",false,false);
        mFirebaseDatabase.getReference("hospital").push().setValue(
                new Hospital(editTextRSname.getText().toString(),
                        editTextRSloc.getText().toString(),
                        "imageurl.com",
                        Double.parseDouble(editTextRSlat.getText().toString()),
                        Double.parseDouble(editTextRSlong.getText().toString())), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError==null){
                            Toast.makeText(getApplicationContext(), "Insert RS Ok", Toast.LENGTH_SHORT).show();
                            pdDialog.dismiss();
                            clearInput();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Insert RS Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean inputIsComplete(){
        boolean state = true;
        if(TextUtils.isEmpty(editTextRSname.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(editTextRSloc.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(editTextRSlat.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(editTextRSlong.getText())){
            state = false;
        }

        if(!TextUtils.isEmpty(editTextRSlat.getText())){
            try{Double.parseDouble(editTextRSlat.getText().toString());}
            catch(Exception e){
                state = false;
                Toast.makeText(getApplicationContext(), "Latitude should be double value", Toast.LENGTH_SHORT).show();
            }
        }

        if(!TextUtils.isEmpty(editTextRSlong.getText())){
            try{Double.parseDouble(editTextRSlong.getText().toString());}
            catch(Exception e){
                state = false;
                Toast.makeText(getApplicationContext(), "Longitude should be double value", Toast.LENGTH_SHORT).show();
            }
        }

        return state;
    }

    private void clearInput(){
        editTextRSname.setText("");editTextRSname.clearFocus();
        editTextRSloc.setText("");editTextRSloc.clearFocus();
        editTextRSlat.setText("");editTextRSlat.clearFocus();
        editTextRSlong.setText("");editTextRSlong.clearFocus();
    }

    private void getGPSLoc() {
        gps = new InserterHospitalActivity.GPS();
        if (gps.isGpsEnable()) {
            gps.requestNewLocation();
        } else {
            Toast.makeText(getApplicationContext(), "GPS feature not detected / available.", Toast.LENGTH_SHORT).show();
        }
    }

    private class GPS {
        String gpsJalan, gpsDesa, gpsKecamatan, gpsKabupaten, gpsProvinsi, gpsNegara, alamat;
        Double latitude, longitude;
        private LocationManager locationManager = null;
        private LocationListener locationListener = null;
        ProgressDialog pD1;

        public GPS() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        public void requestNewLocation() {
            locationListener = new InserterHospitalActivity.GPS.MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            pD1 = ProgressDialog.show(InserterHospitalActivity.this, "Mencari Lokasi Anda", "Harap tunggu...", true, false);
        }

        public boolean isGpsEnable(){
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ return true; }
            else{ return false; }
        }

        private class MyLocationListener implements LocationListener{
            @Override
            public void onLocationChanged(Location location) {
                //get long and lat
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //get city name
                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> address;

                try{
                    address = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(address.size()>0){
                        gpsJalan = address.get(0).getAddressLine(0);
                        gpsDesa = address.get(0).getSubLocality();
                        gpsKecamatan = address.get(0).getLocality();
                        gpsKabupaten = address.get(0).getSubAdminArea();
                        gpsProvinsi = address.get(0).getAdminArea();
                        gpsNegara = address.get(0).getCountryName();
                        alamat = gpsJalan + ", " + gpsDesa + ", " + gpsKecamatan + ". ";
                        //alamat+= gpsKabupaten + ", " + gpsProvinsi + ", " + gpsNegara;

                        pD1.dismiss();

                        editTextRSloc.setText(alamat);
                        editTextRSlat.setText(latitude.toString());
                        editTextRSlong.setText (longitude.toString());

                        locationManager.removeUpdates(locationListener);
                    }
                }catch(Exception e){
                    String error = e.getMessage();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        }
    }
}

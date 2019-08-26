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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import aplikasiku.navwithdirectionlib.FDBMobel.Area;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTLpoint;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTLpos;

public class SkemaTLbaruActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mDbTL;

    Spinner spinnerTLarea;
    EditText edTLname, edTLloc, edTLlat, edTLlong;
    CheckBox cbTLb, cbTLt, cbTLu, cbTLs;
    Button butInsertTL, butGetLocTL;

    String uid_area;
    GPS gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skema_tlbaru);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDbTL = mFirebaseInstance.getReference("new-tl");

        init();
    }

    private void init() {
        spinnerTLarea = (Spinner) findViewById(R.id.spinnerTLarea);
        edTLname = (EditText) findViewById(R.id.editTextTLname);
        edTLloc = (EditText) findViewById(R.id.editTextTLloc);
        edTLlat = (EditText) findViewById(R.id.editTextTLlat);
        edTLlong = (EditText) findViewById(R.id.editTextTLlong);
        cbTLb = (CheckBox) findViewById(R.id.checkBoxTLb);
        cbTLt = (CheckBox) findViewById(R.id.checkBoxTLt);
        cbTLu = (CheckBox) findViewById(R.id.checkBoxTLu);
        cbTLs = (CheckBox) findViewById(R.id.checkBoxTLs);
        butInsertTL = (Button) findViewById(R.id.buttonAddTL);
        butGetLocTL = (Button) findViewById(R.id.buttonGetLocTL);

        spinnerTLarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFirebaseInstance.getReference("area").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot area : dataSnapshot.getChildren()){
                            Area x = area.getValue(Area.class);
                            if(x.area_name.equals(spinnerTLarea.getSelectedItem().toString())){
                                uid_area = area.getKey();
                                Toast.makeText(getApplicationContext(), uid_area, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        butGetLocTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGPSLoc();
            }
        });

        butInsertTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputIsComplete()){
                    String tlname = edTLname.getText().toString();
                    String tlloc = edTLloc.getText().toString();
                    double tllat = Double.parseDouble(edTLlat.getText().toString());
                    double tllong = Double.parseDouble(edTLlong.getText().toString());

                    int tlcount=0;
                    if(cbTLb.isChecked()){tlcount++;}
                    if(cbTLt.isChecked()){tlcount++;}
                    if(cbTLu.isChecked()){tlcount++;}
                    if(cbTLs.isChecked()){tlcount++;}

                    insertTL(uid_area,tlname,tlloc,tllat,tllong,tlcount,
                            cbTLb.isChecked(),cbTLt.isChecked(),cbTLu.isChecked(),cbTLs.isChecked());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please complete all input column", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean inputIsComplete(){
        boolean state = true;
        if(TextUtils.isEmpty(edTLname.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(edTLloc.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(edTLlat.getText())){
            state = false;
        }
        else if(TextUtils.isEmpty(edTLlong.getText())){
            state = false;
        }
        else if(!cbTLb.isChecked() && !cbTLt.isChecked() && !cbTLu.isChecked() && !cbTLs.isChecked()){
            state = false;
        }

        if(!TextUtils.isEmpty(edTLlat.getText())){
            try{Double.parseDouble(edTLlat.getText().toString());}
            catch(Exception e){
                state = false;
                Toast.makeText(getApplicationContext(), "Latitude should be double value", Toast.LENGTH_SHORT).show();
            }
        }

        if(!TextUtils.isEmpty(edTLlong.getText())){
            try{Double.parseDouble(edTLlong.getText().toString());}
            catch(Exception e){
                state = false;
                Toast.makeText(getApplicationContext(), "Longitude should be double value", Toast.LENGTH_SHORT).show();
            }
        }

        return state;
    }

    private void insertTL(String uid_area, String TLname, String TLloc, double TLlat, double TLlong, int TLcount, boolean isTLb, boolean isTLt, boolean isTLu, boolean isTLs) {
        final ProgressDialog pdDialog = ProgressDialog.show(SkemaTLbaruActivity.this, "Insert TL", "Please wait...",false,false);

        NewSkemaTLpoint TLb, TLt, TLu, TLs;
        if(isTLb){TLb = new NewSkemaTLpoint("barat", false);}
        else{ TLb = null;}

        if(isTLt){TLt = new NewSkemaTLpoint("timur", false);}
        else{ TLt = null;}

        if(isTLu){TLu = new NewSkemaTLpoint("utara", false);}
        else{ TLu = null;}

        if(isTLs){TLs = new NewSkemaTLpoint("selatan", false);}
        else{ TLs = null;}

        final String uid_tl = mDbTL.push().getKey();
        mDbTL.child(uid_area).child(uid_tl).setValue(new NewSkemaTL(TLname, TLloc, TLlat, TLlong, TLcount, false, false, new NewSkemaTLpos(TLb, TLt, TLu, TLs)), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){
                    pdDialog.dismiss();
                    clearInput();
                    Toast.makeText(getApplicationContext(), "insert tl ok", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "insert tl fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getGPSLoc() {
        gps = new SkemaTLbaruActivity.GPS();
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
            locationListener = new SkemaTLbaruActivity.GPS.MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            pD1 = ProgressDialog.show(SkemaTLbaruActivity.this, "Mencari Lokasi Anda", "Harap tunggu...", true, false);
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

                        edTLloc.setText(alamat);
                        edTLlat.setText(latitude.toString());
                        edTLlong.setText (longitude.toString());

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

    private void clearInput(){
        edTLname.setText("");edTLname.clearFocus();
        edTLloc.setText("");edTLloc.clearFocus();
        edTLlat.setText("");edTLlat.clearFocus();
        edTLlong.setText("");edTLlong.clearFocus();

        cbTLb.setChecked(false);
        cbTLt.setChecked(false);
        cbTLu.setChecked(false);
        cbTLs.setChecked(false);
    }
}


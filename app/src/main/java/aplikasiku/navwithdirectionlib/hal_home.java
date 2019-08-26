package aplikasiku.navwithdirectionlib;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aplikasiku.navwithdirectionlib.FDBMobel.Hospital;
import aplikasiku.navwithdirectionlib.GPS.GoogleService;

public class hal_home extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    FirebaseAuth auth;
    TextView tvIdLogin, tvPosisiLogin, tvLatLogin, tvLongLogin;

    Button butKontrolerLampu, butSetLampu;

    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    Geocoder geocoder;

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating List of ImageUploadInfo class.
    List<Hospital> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hal_home);

        init();
        getDataLogin();
        getLocGPS();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if(addresses.size()>0){
                    String gpsJalan = addresses.get(0).getAddressLine(0);
                    String gpsDesa = addresses.get(0).getSubLocality();
                    String gpsKecamatan = addresses.get(0).getLocality();
                    String gpsKabupaten = addresses.get(0).getSubAdminArea();
                    String gpsProvinsi = addresses.get(0).getAdminArea();
                    String gpsNegara = addresses.get(0).getCountryName();
                    String alamat = gpsJalan + ", " + gpsDesa + ", " + gpsKecamatan + ". ";
                    //alamat+= gpsKabupaten + ", " + gpsProvinsi + ", " + gpsNegara;

                    tvPosisiLogin.setText(alamat);
                    tvLatLogin.setText("Lat: " + Double.toString(latitude));
                    tvLongLogin.setText("Long: " + Double.toString(longitude));

                    //to set on next intent
                    DataPerjalanan.originLat = latitude;
                    DataPerjalanan.originLong = longitude;
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    private void getLocGPS(){
        if (boolean_permission) {
            if (isMyServiceRunning(GoogleService.class)){
                Toast.makeText(getApplicationContext(), "Service sudah running", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Service belum running", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                startService(intent);
                registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
            }


        } else {
            Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataLogin() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            tvIdLogin.setText(auth.getCurrentUser().getEmail());
            Toast.makeText(getApplicationContext(), "Get data login", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        tvIdLogin = (TextView) findViewById(R.id.textViewIdLogin);
        tvPosisiLogin = (TextView) findViewById(R.id.textViewPosisiLogin);
        tvLatLogin = (TextView) findViewById(R.id.textViewLatLogin);
        tvLongLogin = (TextView) findViewById(R.id.textViewLongLogin);
        butKontrolerLampu = (Button) findViewById(R.id.buttonKontrolerLampu);

        butSetLampu = (Button) findViewById(R.id.butSetDestination);
        butSetLampu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvPosisiLogin.getText().toString().equals("posisi")){
                    Toast.makeText(getApplicationContext(), "Masih mencari posisi GPS", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(hal_home.this, SetDestinationActivity.class)
                            .putExtra("origin_address", tvPosisiLogin.getText().toString()));
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewListRS);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**
         *
         */

        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();

        butKontrolerLampu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(hal_home.this, AdminActivity.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference(InsertRsFirebase.Database_Path);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Hospital hospital = postSnapshot.getValue(Hospital.class);

                    list.add(hospital);
                }

                mAdapter = new RSRecyclerViewAdapter(getApplicationContext(),list, new RSRecyclerViewAdapter.CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        if((DataPerjalanan.originLat==0) || (DataPerjalanan.originLong==0)){
                            Toast.makeText(getApplicationContext(), "Latitude longitude user masih dicari", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DataPerjalanan.destination = list.get(position).RsName;
                            DataPerjalanan.destinationLat = list.get(position).RsLat;
                            DataPerjalanan.destinationLong = list.get(position).RsLong;

                            Intent intentMap = new Intent(hal_home.this, hal_map.class);
                            startActivity(intentMap);
                        }
                    }
                });
                mRecyclerView.setAdapter(mAdapter);


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                //progressDialog.dismiss();

            }
        });

//
//        final Hospital[] rsData = new Hospital[3];
//        rsData[0] = new Hospital();
//        rsData[0].RsName = "RSUP Dr. Sardjito";
//        rsData[0].RsLoc = "Jl. Kesehatan No. 1, Sekip, Sinduadi, Sleman, Sinduadi, Mlati, Kabupaten Sleman, DIY";
//        rsData[0].RsLat = -7.768459;
//        rsData[0].RsLong = 110.373480;
//
//        rsData[1] = new Hospital();
//        rsData[1].RsName = "RS Panti Rapih";
//        rsData[1].RsLoc = "Jl. Cik Di Tiro No.30, Caturtunggal, Kec. Depok, Kabupaten Sleman, DIY";
//        rsData[1].RsLat = -7.776956;
//        rsData[1].RsLong = 110.376149;
//
//        rsData[2] = new Hospital();
//        rsData[2].RsName = "RS Bethesda";
//        rsData[2].RsLoc = "Jl. Jend. Sudirman No.70, Kotabaru, Gondokusuman, Kota Yogyakarta, DIY";
//        rsData[2].RsLat = -7.783235;
//        rsData[2].RsLong = 110.378552;



        fn_permission();
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(hal_home.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {}
            else {
                ActivityCompat.requestPermissions(hal_home.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
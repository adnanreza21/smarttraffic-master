package aplikasiku.navwithdirectionlib;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aplikasiku.navwithdirectionlib.FDBMobel.LogPerjalanan;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaRequest;
import aplikasiku.navwithdirectionlib.FDBMobel.NewSkemaTL;
import aplikasiku.navwithdirectionlib.FDBMobel.Requester;
import aplikasiku.navwithdirectionlib.GPS.GoogleService;
import aplikasiku.navwithdirectionlib.GPS.LatLngLoc;
import aplikasiku.navwithdirectionlib.GPS.SortTLinRoute;


public class hal_map extends AppCompatActivity {
    private static final String TAG = hal_map.class.getName();

    GoogleMap gogelMap;
    String serverKey = "AIzaSyBPWWTZcnHwIfe8c_gLJkaLm8WGdJnLS7Y";

    LatLng origin = new LatLng(DataPerjalanan.originLat,DataPerjalanan.originLong);
    LatLng defaultViewMap = new LatLng(DataPerjalanan.originLat,DataPerjalanan.originLong);
    LatLng destination = new LatLng(DataPerjalanan.destinationLat,DataPerjalanan.destinationLong);

    ArrayList<LatLng> directionRoute;
    ArrayList<LatLngLoc> testPoint;
    ArrayList<LatLngLoc> TLinRoute;
    ArrayList<LatLngLoc> sTLinRoute;

    ArrayList<LatLngLoc> PointListTL;

    Double latitude,longitude;
    Geocoder geocoder;
    SortTLinRoute sortTLinRoute;

    Marker markerAmbulance;
    Marker markerRS;

    FirebaseDatabase mFirebaseInstance;
    FirebaseAuth auth;
    private String uid_area = "-KlKL87oFYPYGnFJeb5F";

    TextView halInfo;

    private int noTLonRoute =0;
    String sound = "mati";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hal_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gogelMap = googleMap;

                gogelMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13));

            }
        });

        testPoint = new ArrayList<LatLngLoc>();
        TLinRoute = new ArrayList<LatLngLoc>();

        PointListTL = new ArrayList<LatLngLoc>();
        sTLinRoute= new ArrayList<LatLngLoc>();
        sTLinRoute = null;

        halInfo = (TextView) findViewById(R.id.halmapinfo);

        //Toast.makeText(getApplicationContext(), DataPerjalanan.destination, Toast.LENGTH_SHORT).show();

        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //Toast.makeText(getApplicationContext(), "Get direction", Toast.LENGTH_SHORT).show();
                rquestDirection();
                registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
            }
        }.start();


    }

    private void rquestDirection() {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .optimizeWaypoints(true)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        //direction.getRouteList().get(0).getLegList().get(0).getStepList() //titik turn by turn
                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                        directionRoute = directionPositionList;
                        gogelMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 8, Color.BLUE));
                        gogelMap.setTrafficEnabled(true);
                        gogelMap.getUiSettings().setZoomControlsEnabled(true);

                        // Enable / Disable my location button
                        gogelMap.getUiSettings().setMyLocationButtonEnabled(true);

                        // Enable / Disable Compass icon
                        gogelMap.getUiSettings().setCompassEnabled(true);

                        // Enable / Disable Rotate gesture
                        gogelMap.getUiSettings().setRotateGesturesEnabled(true);

                        // Enable / Disable zooming functionality
                        gogelMap.getUiSettings().setZoomGesturesEnabled(true);
                        addMarkerAmbulance(origin,"Start");
                        addMarkerRS(destination,DataPerjalanan.destination);

                        firebaseGetDataTL(uid_area);
                        /*
                        //demo compute heading//
                        LatLngLoc tugu = new LatLngLoc(new LatLng(-7.782884, 110.367098),"TUGU JOGJA");
                        LatLngLoc bkd = new LatLngLoc(new LatLng(-7.782027, 110.356351), "BKD JOGJA");
                        LatLngLoc smk3yk = new LatLngLoc(new LatLng(-7.777586, 110.366027), "SMK3YK");
                        LatLngLoc mcdonald = new LatLngLoc(new LatLng(-7.783260, 110.371703), "McDONALD");
                        LatLngLoc PLNmangkubumi = new LatLngLoc(new LatLng(-7.787897, 110.366830), "PLNmangkubumi");

                        gogelMap.addMarker(new MarkerOptions().position(tugu.getLocation()).title(tugu.getNameLoc()));
                        gogelMap.addMarker(new MarkerOptions().position(bkd.getLocation()).title(bkd.getNameLoc() + ": " + arahAsalAmbulance(tugu.getLocation(), bkd.getLocation()))).showInfoWindow();
                        gogelMap.addMarker(new MarkerOptions().position(smk3yk.getLocation()).title(smk3yk.getNameLoc() + ": " + arahAsalAmbulance(tugu.getLocation(),smk3yk.getLocation())));
                        gogelMap.addMarker(new MarkerOptions().position(mcdonald.getLocation()).title(mcdonald.getNameLoc() + ": " + arahAsalAmbulance(tugu.getLocation(), mcdonald.getLocation())));
                        gogelMap.addMarker(new MarkerOptions().position(PLNmangkubumi.getLocation()).title(PLNmangkubumi.getNameLoc() + ": " + arahAsalAmbulance(tugu.getLocation(), PLNmangkubumi.getLocation())));

                        //demo update tl
                        updateRequestTL("-KvgGfURCFOLpqV_60E4", "TLu", 50);
                        */
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
    }

    private void firebaseGetDataTL(String uid_area) {
        mFirebaseInstance.getReference("new-tl").child(uid_area).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setTLonRoute(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTLonRoute(DataSnapshot dataSnapshot) {
        //Toast.makeText(getApplicationContext(), "SetTLonRoute", Toast.LENGTH_SHORT).show();
        NewSkemaTL[] tldata = new NewSkemaTL[(int) dataSnapshot.getChildrenCount()];
        int count = 0;
        for(DataSnapshot data : dataSnapshot.getChildren()){
            NewSkemaTL tl = data.getValue(NewSkemaTL.class);
            LatLng tlLatLong = new LatLng(tl.TLlat, tl.TLlong);
            PointListTL.add(new LatLngLoc(tlLatLong, tl.TLname, data.getKey()));
            tldata[count] = tl;
            count++;
        }

        for (int i = 0; i < PointListTL.size(); i++){
            Boolean dijalur = PolyUtil.isLocationOnPath(PointListTL.get(i).getLocation(),directionRoute,false,10);
            if (dijalur) {
                TLinRoute.add(PointListTL.get(i));
                addMarkerTL(PointListTL.get(i).getLocation());
                //Toast.makeText(getApplicationContext(), tldata[i].TLname + " on route", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(getApplicationContext(), tldata[i].TLname + " not on route", Toast.LENGTH_SHORT).show();
            }
        }

        sortTLinRoute = new SortTLinRoute(origin,TLinRoute);
        sTLinRoute = sortTLinRoute.getSortTLInRoute();

        LatLngLoc RS = new LatLngLoc(destination,DataPerjalanan.destination);
        sTLinRoute.add(RS);

        for (int z = 0; z < sTLinRoute.size(); z++){
            Log.d(TAG, "onDirectionSuccess: "+ z +" nama "+ sTLinRoute.get(z).getNameLoc() );
        }
    }

    private void setNewRouting() {
        final DatabaseReference mDbRoute;
        mDbRoute = mFirebaseInstance.getReference("routing");

        final String uid_route = mDbRoute.push().getKey();
        mDbRoute.child(uid_route).setValue(new aplikasiku.navwithdirectionlib.FDBMobel.Route(auth.getCurrentUser().getUid(), "-keytl1,-keytl2,keytl3", new LogPerjalanan()), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){
                    //Toast.makeText(getApplicationContext(), "Insert route oke", Toast.LENGTH_SHORT).show();
                    mDbRoute.child(uid_route).child("log").push().setValue(new LogPerjalanan("alamatstart", 77.0, 110.0), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){
                                //Toast.makeText(getApplicationContext(), "Insert log oke", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Get data from service GoogleService
     */

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

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.3f%s", distance, unit);
    }

    private void addMarkerRS(LatLng position,String nmRS) {
        IconGenerator iconFactory = new IconGenerator(this);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rs)).
                position(position).title(nmRS).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()).zIndex(10);

        markerRS = gogelMap.addMarker(markerOptions);
        markerRS.showInfoWindow();
    }

    private void addMarkerAmbulance(LatLng position,String jarak) {
        IconGenerator iconFactory = new IconGenerator(this);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ambulance)).
                position(position).title(jarak).visible(true).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()).zIndex(11);
        markerAmbulance = gogelMap.addMarker(markerOptions);
        markerAmbulance.showInfoWindow();
    }

    private void addMarkerTL(LatLng position) {

        IconGenerator iconFactory = new IconGenerator(this);
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_tl)).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()).zIndex(9);
        gogelMap.addMarker(markerOptions);
    }

    private void updateMarkerAmbulance(LatLng locNow,String jarakTL,String nmTL) {
        //markerAmbulance.remove();
        if (sTLinRoute != null) {
            markerAmbulance.setPosition(locNow);
            markerAmbulance.setTitle(jarakTL);
            markerAmbulance.setSnippet("Arah ke TL "+nmTL);
            markerAmbulance.showInfoWindow();

//            CameraPosition cameraPosition = new CameraPosition.Builder().target(
//                    new LatLng(locNow.latitude,locNow.longitude)).zoom(12).build();
//
//            gogelMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        //addMarkerAmbulance(locNow,jarakTL);
    }

    /**
     * Get data from service GoogleService
     */

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));
            LatLng LocNow = new LatLng(latitude, longitude);
            if (sTLinRoute != null) {
                double distance = SphericalUtil.computeDistanceBetween(LocNow, sTLinRoute.get(noTLonRoute).getLocation());

                //gogelMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LocNow, 13));

                LatLng area1 = new LatLng(-7.768131, 110.387519);
                LatLng area2 = new LatLng(-7.767976, 110.387569);
                LatLng area3 = new LatLng(-7.768028, 110.387677);
                LatLng area4 = new LatLng(-7.768163, 110.387625);

                ArrayList<LatLng> polyArea = new ArrayList<LatLng>();
                polyArea.add(area1);
                polyArea.add(area2);
                polyArea.add(area3);
                polyArea.add(area4);

                String Jarak = formatNumber(distance);

//                if ((PolyUtil.isLocationOnEdge(LocNow,polyArea,false,200))) {
//                    noTLonRoute++;
//                    sound = "Mati";
//                }
                int jmlpoint = sTLinRoute.size();
                if (distance < 120){
                    if (noTLonRoute < jmlpoint - 1) {
                        noTLonRoute++;
                        sound = "matii";
                    }
                }
                if ((distance < 800) && (distance>=120)){
                    sound = "bunyi";

                    //update tl and add request
                    if(!TextUtils.isEmpty(sTLinRoute.get(noTLonRoute).getTLkey())){
                        String tlkey = sTLinRoute.get(noTLonRoute).getTLkey();
                        String TLbtus = arahAsalAmbulance(sTLinRoute.get(noTLonRoute).getLocation(),LocNow);
                        updateRequestTL(tlkey, TLbtus, distance);
                    }
                }

                String text = new String(
                        "Lokasi Lat " + latitude + " Lng " + longitude
                                +" ID TL "+ noTLonRoute
                                + " Jarak dg TL "+ sTLinRoute.get(noTLonRoute).getNameLoc()
                                + " adl " + Jarak
                                + " Sound " + sound);

                halInfo.setText(text);
                updateMarkerAmbulance(LocNow,Jarak,sTLinRoute.get(noTLonRoute).getNameLoc());
            } else {
                Toast.makeText(getApplicationContext(), "Datanya sTLinRoute Kosong", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private String arahAsalAmbulance(LatLng tldes, LatLng ambulance){
        String arah = "";
        double derajat = SphericalUtil.computeHeading(tldes, ambulance);
        if(derajat>=-45 && derajat <=45){
            arah = "TLu";
        }
        else if(derajat>45 && derajat <=135){
            arah = "TLt";
        }
        else if(derajat>135 || derajat<-135){
            arah = "TLs";
        }
        else if(derajat<-45 && derajat>=-135){
            arah = "TLb";
        }

        Log.d("DERAJAT", Double.toString(derajat) + " " + arah);
        return arah;
    }

    private void updateRequestTL(final String keyTL, final String TLbtus, final double distance){
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("perequest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot1) {
                            if(!dataSnapshot1.exists()){
                                updateTL(keyTL, TLbtus, distance, 1);
                                mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("useNumber").setValue(1);
                            }
                            else{
                                mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("perequest").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot2) {
                                        if(dataSnapshot2.exists()){
                                            final Requester requester = dataSnapshot2.getValue(Requester.class);
                                            mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot3) {
                                                    NewSkemaRequest req = dataSnapshot3.getValue(NewSkemaRequest.class);
                                                    if(requester.antrian==req.useNumber){
                                                        updateTL(keyTL, TLbtus, distance, requester.antrian);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        else{
                                            updateTL(keyTL, TLbtus, distance, (int)(dataSnapshot1.getChildrenCount()+1));
                                            mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("useNumber").setValue(dataSnapshot1.getChildrenCount()+1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    //tidak ada tl point dari arah ambulance yg di dapat
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //only call in updateRequestTL
    private void updateTL(String keyTL, String TLbtus, double distance, int antrian) {
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("perequest").child(auth.getCurrentUser().getUid()).setValue(new Requester("ambulance", antrian));
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("adaRequest").setValue(true);
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("request").child("jarakAcuan").setValue(distance);
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLpos").child(TLbtus).child("isEmergency").setValue(true);
        mFirebaseInstance.getReference("new-tl").child(uid_area).child(keyTL).child("TLplaysound").setValue(true);
    }

    private void removeRequestTL(){

    }
}
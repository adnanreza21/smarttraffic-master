package aplikasiku.navwithdirectionlib;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SetDestinationActivity extends AppCompatActivity {

    String TAG = ".SetDestinationActivity";
    private GoogleMap mMap;
    AsyncHttpClient client;
    ImageView ivCenter;
    TextView tvOrigin,tvDestination;
    boolean isCameraMoveListenerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_destination);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Lokasi tujuan");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "LatLng: " + place.getLatLng().toString());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
                if(!isCameraMoveListenerSet) {
                    mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            if (mMap != null) {
                                double lat = mMap.getCameraPosition().target.latitude;
                                double lng = mMap.getCameraPosition().target.longitude;

                                GetLocationBy(new LatLng(lat,lng));
                            }
                        }
                    });
                    isCameraMoveListenerSet = true;
                }
                ivCenter.setVisibility(View.VISIBLE);
                GetLocationBy(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        initMap();

        ivCenter = (ImageView) findViewById(R.id.iv_center_location);
        String origin_address = getIntent().getStringExtra("origin_address");
        tvOrigin = (TextView) findViewById(R.id.tv_origin_address);
        tvOrigin.setText(origin_address);
        tvDestination = (TextView) findViewById(R.id.tv_des_address);
    }

    private void initMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                client = new AsyncHttpClient();
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.7752787,110.3848264), 12));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        });
    }

    boolean isLoadLocation = false;

    private void GetLocationBy(LatLng latLng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json" +
                "?latlng=" + latLng.latitude + "," + latLng.longitude +
                "&key=" + "AIzaSyBPWWTZcnHwIfe8c_gLJkaLm8WGdJnLS7Y";

        if(!isLoadLocation){
            isLoadLocation = true;
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try{
                        String responseString = new String(responseBody, "UTF-8");
                        JSONObject responseJsonObject = new JSONObject(responseString);

                        JSONArray resultJsonArray = responseJsonObject.getJSONArray("results");
                        JSONObject firstResult = resultJsonArray.getJSONObject(0);
                        Log.d(TAG, firstResult.getString("formatted_address"));
                        tvDestination.setText(firstResult.getString("formatted_address"));

                    }catch (Exception e){
                        Log.d(TAG, e.getMessage());
                    }
                    isLoadLocation=false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TAG, error.getMessage());
                    isLoadLocation=false;
                }
            });
        }
    }
}
package com.example.makitani.myproduct;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;



public class TopActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private int mPositionNum = 0;
    private static final int MENU_DELETE_MARKERS = 0;
    private static final int REQUEST_FOR_LOCATION_INFO = 100;
    Realm mRealm;

    private LoginButton loginButton;

    private Button LogIn;

    private View.OnClickListener LogIn_ClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            LogIn_Click();
        }
    };


    private void setViewObject () {

        LogIn = (Button) findViewById(R.id.search);
    }

    private void setListner() {
        LogIn.setOnClickListener(LogIn_ClickListener);
    }

    private void LogIn_Click() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        setUpMapIfNeeded();
        setViewObject();
        setListner();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null) {
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_DELETE_MARKERS, Menu.NONE, "(Debug用)マーカー消す");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE_MARKERS:
                deleteDatafromRealm();
                return true;
        }
        return false;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * データベースに保存されている位置情報データをマップに表示する
     */


    /**
     * ロケーションをDBに保存する
     *
     * @param latLng
     */
    private void saveLocation(LatLng latLng) {
        if (mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        mRealm.beginTransaction();
        com.example.makitani.myproduct.LocationData data = mRealm.createObject(com.example.makitani.myproduct.LocationData.class);
        data.setLatitude(latLng.latitude);
        data.setLongitude(latLng.longitude);
        data.setTitle("position " + mPositionNum);
        mRealm.commitTransaction();
    }

    private void drawMarker(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("position " + mPositionNum));
    }

    private void gotoAddLocationInfoActivity(LatLng latLng){
        Intent intent = AddLocationInfoActivity.createIntent(this, latLng);
        startActivityForResult(intent, REQUEST_FOR_LOCATION_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_LOCATION_INFO){
            //DBから再ロードする
            loadDataFromRealm();
        }
    }

    private void loadDataFromRealm() {
        if (mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        RealmQuery<com.example.makitani.myproduct.LocationData> query = mRealm.where(com.example.makitani.myproduct.LocationData.class);

        RealmResults<com.example.makitani.myproduct.LocationData> realmResults = query.findAll();
        for (com.example.makitani.myproduct.LocationData data : realmResults) {
            LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(data.getTitle()));
        }
    }

    private void deleteDatafromRealm() {
        if (mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        mRealm.beginTransaction();
        RealmQuery<com.example.makitani.myproduct.LocationData> query = mRealm.where(com.example.makitani.myproduct.LocationData.class);
        RealmResults<com.example.makitani.myproduct.LocationData> realmResults = query.findAll();
        realmResults.clear();
        mRealm.commitTransaction();

        mMap.clear();
        mPositionNum = 0;
    }

    //    暫定で場所を指定している。クリック時の緯度軽度を取得してここに代入?
    private void setUpMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else {
            //mylocationがnullの場合は銀座を中心として表示
            LatLng clicklat = new LatLng(35.671241, 139.765041);
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(clicklat, 15);
            mMap.moveCamera(cu);
            mMap.addMarker(new MarkerOptions().position(clicklat).title("クリックした場所"));
            mMap.addMarker(new MarkerOptions()
                    .position(clicklat)
                    .draggable(true)
                    .title("クリックした場所"));


            // Enable MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);

            // Get LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Get Current Location
            Location myLocation = locationManager.getLastKnownLocation(provider);

            if (myLocation != null) {

                // set map type
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Get latitude of the current location
                double latitude = myLocation.getLatitude();

                // Get longitude of the current location
                double longitude = myLocation.getLongitude();


                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Show the current location in Google Map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));
            }
        }

    }
}
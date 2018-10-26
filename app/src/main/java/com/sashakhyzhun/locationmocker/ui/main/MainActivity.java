package com.sashakhyzhun.locationmocker.ui.main;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sashakhyzhun.locationmocker.BuildConfig;
import com.sashakhyzhun.locationmocker.R;
import com.sashakhyzhun.locationmocker.data.model.MyLocation;
import com.sashakhyzhun.locationmocker.ui.mylist.MyListViewActivity;
import com.sashakhyzhun.locationmocker.ui.howto.HowToActivity;
import com.sashakhyzhun.locationmocker.utils.MyStrings;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.sashakhyzhun.locationmocker.utils.CommonUtil.forceCloseKeyboard;

public class MainActivity extends FragmentActivity  implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private FloatingActionButton myLocationButton;
    private FloatingActionButton startFakingButton;

    private Location droppedMarker = null;
    private Boolean isMocking = false;
    public float FAKE_ACCURACY = (float) 3.0f;
    private Realm realm;
    private GoogleApiClient mGoogleApiClient;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located.
    private Location mCurrentLocation;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";
    private static final String KEY_SEARCH_BAR = "search";
    private static final String KEY_DROPPED_PIN = "dropped";

    private ImageView addFav;
    private String searchBarText = "";

    // Location Listener when mocking location (basically useless)
    private LocationListener lis = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            searchBarText = savedInstanceState.getString(KEY_SEARCH_BAR);
            droppedMarker = savedInstanceState.getParcelable(KEY_DROPPED_PIN);
        }

        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_maps);
        stub.inflate();

        // Checks to see if Call is made from a different activity
        // Initialize location manager and location provider
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final String provider = LocationManager.GPS_PROVIDER;




        myLocationButton = (FloatingActionButton) findViewById(R.id.find_my_location);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLocationPermissionGranted) {
                    askForLocationPermission();
                } else {
                    getDeviceLocation();
                    if (mCurrentLocation == null) {
                        // ...
                    } else {
                        if (isMocking) {
                            toast("Mocked current location");
                        } else {
                            toast("Real current location");
                        }
                        myLocationButton.setImageResource(R.mipmap.ic_launcher_blue);
                    }
                }

            }
        });

        addFav = (ImageView) findViewById(R.id.addToFavorite);
        addFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfMarkerExistsInFavorites()) {
                    removePlaceFromFavorites();
                } else {
                    askForName();
                }
            }
        });


        startFakingButton = (FloatingActionButton) findViewById(R.id.start_faking);
        startFakingButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            // SecurityException will be thrown when MOCK Location is disabled
            public void onClick(View view) throws SecurityException {
                if (!isMocking && droppedMarker != null) {
                    startFakingLocation(lm, provider);
                } else if (isMocking) {
                    stopFakingLocation(lm, provider);
                } else {
                    toast("Please choose a location to mock");
                }
                setupStartStopButton();
            }
        });

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        buildGoogleApiClient();
        createLocationRequest();

        // Initialize DB
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        // Initialize tools for navigation drawer
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        refreshFavoriteButton();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void goToLocation(MyLocation goToLocation) {
        myLocationButton.setImageResource(R.mipmap.ic_crosshairs_gps_grey600_24dp);
        searchBarText = goToLocation.placeName;
        LatLng latLng = new LatLng(goToLocation.latitude, goToLocation.longitude);
        prepareFakeLocation(latLng);
        refreshFavoriteButton();
    }

    /**
     * Checks if "Mock Locations" is enabled(true) or disabled(false) in Developer Settings
     * TODO: If it is disabled, ask user if they want to enable it
     */
    public boolean isMockLocationEnabled() {
        boolean isMockEnabled = false;
        try {
            // if marshmallow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppOpsManager opsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                isMockEnabled = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID) == AppOpsManager.MODE_ALLOWED);
            } else {
                // in marshmallow this will always return true
                isMockEnabled = !Settings.Secure.getString(this.getContentResolver(), "mock_location").equals("0");
            }
        } catch (Exception e) {
            return isMockEnabled;
        }
        return isMockEnabled;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void isCallFromDifferentActivity() {
        Intent intent = this.getIntent();
        if (intent.hasExtra("mock_status")) {
            if (intent.getBooleanExtra("mock_status", false)) {
                isMocking = true;
                setupStartStopButton();
            }
        } else if (intent.hasExtra("from_id")) {
            if (intent.getStringExtra("from_id").equals("ListViewActivity")) {
                MyLocation goToPlace = new MyLocation();
                goToPlace.placeName = intent.getStringExtra("place_name");
                goToPlace.latitude = intent.getDoubleExtra("place_lati", 0.0);
                goToPlace.longitude = intent.getDoubleExtra("place_long", 0.0);
                goToLocation(goToPlace);
                intent.removeExtra("from_id");
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }


    /**
     * If marker is place, it makes 'favorite' button visible
     * If marker location is in DB, button is filled red, else gray border
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void refreshFavoriteButton() {
        if (droppedMarker == null) {
            addFav.setVisibility(View.INVISIBLE);
        } else {
            addFav.setVisibility(View.VISIBLE);
            if (checkIfMarkerExistsInFavorites()) {
                addFav.setImageDrawable(getDrawable(R.mipmap.ic_favorite_black_24dp));
                addFav.setColorFilter(Color.parseColor(MyStrings.RED));
            } else {
                addFav.setImageDrawable(getDrawable(R.mipmap.ic_favorite_border_black_24dp));
                addFav.setColorFilter(Color.parseColor(MyStrings.DARK_GRAY));
            }
        }
    }

    /**
     * Set's dropped marker's latitude and longitude
     */
    public void prepareFakeLocation(LatLng point) {
        droppedMarker = new Location("");
        droppedMarker.setLatitude(point.latitude);
        droppedMarker.setLongitude(point.longitude);
    }

    /**
     * Gets called when user clicks 'heart' button.
     * Asks for placeName and saves the dropped marker location to favorites
     */
    public void askForName() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_favorite_name, null);
        final EditText favPlaceName = (EditText) promptsView.findViewById(R.id.placeName);
        favPlaceName.setText(searchBarText);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int id) {
                        String placeName = favPlaceName.getText().toString();
                        forceCloseKeyboard(favPlaceName, MainActivity.this);
                        if (placeName.equals("")) {
                            toast("Name cannot be blank");
                        } else {
                            addPlaceToFavoritesDB(placeName);
                            refreshFavoriteButton();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        forceCloseKeyboard(favPlaceName, MainActivity.this);
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Removes favorite place from favorites DB
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void removePlaceFromFavorites() {
        RealmResults<MyLocation> favorite = realm.where(MyLocation.class)
                .equalTo("id", MyStrings.favID)
                .equalTo("latitude", droppedMarker.getLatitude())
                .equalTo("longitude", droppedMarker.getLongitude())
                .findAll();

        realm.beginTransaction();
        favorite.deleteAllFromRealm();
        realm.commitTransaction();
        refreshFavoriteButton();
    }


    /**
     * Checks if 'dropped marker' exists in Favorites DB
     * return True if exists and False otherwise
     * NOTE: There can only be ONE dropped marker at any given time
     */
    public Boolean checkIfMarkerExistsInFavorites() {
        RealmQuery<MyLocation> favorites = realm.where(MyLocation.class)
                .equalTo("id", MyStrings.favID)
                .equalTo("latitude", droppedMarker.getLatitude())
                .equalTo("longitude", droppedMarker.getLongitude());

        return favorites.count() != 0;
    }

    public Boolean checkIfMarkerExistsInRecent() {
        RealmQuery<MyLocation> recent = realm.where(MyLocation.class).equalTo("id", MyStrings.recID).equalTo("latitude", droppedMarker.getLatitude()).equalTo("longitude", droppedMarker.getLongitude());
        return recent.count() != 0;
    }


    public void addPlaceToFavoritesDB(final String placeName) {
        realm.beginTransaction();
        final MyLocation favPlace = realm.createObject(MyLocation.class);
        favPlace.id = MyStrings.favID;
        favPlace.placeName = placeName;
        favPlace.latitude = droppedMarker.getLatitude();
        favPlace.longitude = droppedMarker.getLongitude();
        realm.commitTransaction();
    }

    public void setupStartStopButton() {
        if (isMocking) {
            //show red button to stop
            startFakingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.red_tint)));
            startFakingButton.setImageResource(R.mipmap.ic_stop_white_24dp);

        } else {
            //show green button to start
            startFakingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.green_tint)));
            startFakingButton.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
        }
    }

    public void stopFakingLocation(LocationManager lm, String provider) {
        lm.removeTestProvider(provider);
        isMocking = false;
        toast("Location Mocking stopped");
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startFakingLocation(final LocationManager lm, final String provider) {
        try {
            isMocking = true;
            toast("Location Mocking started");
            lm.requestLocationUpdates(provider, 50, 0, lis);
            lm.addTestProvider(provider,
                    Objects.equals("requiresNetwork", ""),
                    Objects.equals("requiresSatellite", ""),
                    Objects.equals("requiresCell", ""),
                    Objects.equals("hasMonetaryCost", ""),
                    Objects.equals("supportsAltitude", ""),
                    Objects.equals("supportsSpeed", ""),
                    Objects.equals("supportsBearing", ""),
                    Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE);

            final Location newLocation = new Location(provider);
            newLocation.setLatitude(droppedMarker.getLatitude());
            newLocation.setLongitude(droppedMarker.getLongitude());
            newLocation.setAccuracy(FAKE_ACCURACY);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            mCurrentLocation = newLocation;
            if (!checkIfMarkerExistsInRecent()) {
                addDroppedMarkerToRecent();
            }
            final Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (isMocking) {
                        newLocation.setTime(System.currentTimeMillis());
                        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        lm.setTestProviderLocation(provider, newLocation);
                    } else {
                        t.cancel();
                    }
                }
            }, 0, 2000);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds location details to recent when user starts a Mock
     */
    public void addDroppedMarkerToRecent() {

        realm.beginTransaction();
        final MyLocation mockedLocation = realm.createObject(MyLocation.class);
        mockedLocation.id = MyStrings.recID;
        mockedLocation.placeName = searchBarText;
        mockedLocation.latitude = droppedMarker.getLatitude();
        mockedLocation.longitude = droppedMarker.getLongitude();
        realm.commitTransaction();
    }


    @SuppressLint("RestrictedApi")
    public void snackBarForMockSetting() {
        startFakingButton.setVisibility(View.INVISIBLE);
        if (!isMockLocationEnabled()) {
            Snackbar.make(findViewById(android.R.id.content), "Mock location setting is disabled", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            Toast.makeText(getApplicationContext(), "Allow this app to mock your location", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setActionTextColor(Color.RED)
                    .show();
        } else {
            startFakingButton.setVisibility(View.VISIBLE);
        }
    }


    private void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }
        if (mLocationPermissionGranted) {
            mCurrentLocation = new Location(LocationManager.GPS_PROVIDER); // was method to get current location;
        }
    }

    /**
     * Asks permission to use user's location.
     */
    public void askForLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        if (!mLocationPermissionGranted) {
            toast("Allow permission to use location");
        }
    }


    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    private void searchByCoordinates() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_coordinates, null);
        final EditText coordinates = (EditText) promptsView.findViewById(R.id.coordinateValue);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MyLocation corLocation = new MyLocation();
                            corLocation.placeName = coordinates.getText().toString();
                            String[] latlng = corLocation.placeName.split(",");
                            corLocation.latitude = Double.parseDouble(latlng[0]);
                            corLocation.longitude = Double.parseDouble(latlng[1]);
                            goToLocation(corLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast("Invalid Coordinates");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        forceCloseKeyboard(coordinates, MainActivity.this);
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startMockUsingFusedProvider() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.addTestProvider(LocationManager.GPS_PROVIDER,
                    Objects.equals("requiresNetwork", ""),
                    Objects.equals("requiresSatellite", ""),
                    Objects.equals("requiresCell", ""),
                    Objects.equals("hasMonetaryCost", ""),
                    Objects.equals("supportsAltitude", ""),
                    Objects.equals("supportsSpeed", ""),
                    Objects.equals("supportsBearing", ""),
                    Criteria.POWER_LOW,
                    Criteria.ACCURACY_FINE);

            Location newLocation = new Location("fused");
            newLocation.setLatitude(droppedMarker.getLatitude());
            newLocation.setLongitude(droppedMarker.getLongitude());
            newLocation.setAccuracy(FAKE_ACCURACY);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            LocationServices.FusedLocationApi.setMockMode(mGoogleApiClient, true);
            LocationServices.FusedLocationApi.setMockLocation(mGoogleApiClient, newLocation);
            isMocking = true;
            mCurrentLocation = newLocation;
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("Please enable MOCK Location for this app in settings");
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    @Override
    public void onConnectionSuspended(int i) { }
}
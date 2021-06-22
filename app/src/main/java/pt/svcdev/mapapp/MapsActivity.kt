package pt.svcdev.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pt.svcdev.mapapp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val onMapLongClickListener = GoogleMap.OnMapLongClickListener {
        mMap.addMarker(MarkerOptions().position(it))
    }

    private val onMarkerClickListener = GoogleMap.OnMarkerClickListener {
        it.remove()
        return@OnMarkerClickListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        requestCurrentLocation()
    }

    private fun requestCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        val locationManager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        val provider = locationManager.getBestProvider(criteria, true)
        provider?.let {
            locationManager.requestLocationUpdates(it, 10000, 50F,
                object: LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.clear()
                        mMap.addMarker(MarkerOptions()
                            .position(currentLatLng))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        mMap.setMinZoomPreference(15.0f)
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }
                }
            )
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMapLongClickListener(onMapLongClickListener)
        mMap.setOnMarkerClickListener(onMarkerClickListener)
    }

}
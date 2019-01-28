package victor96man.mitiempo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    val GPS_REQUEST = 1001
    val LOCATION_REQUEST = 1000
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var isGPS = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = (10 * 1000).toLong() // 10 seconds
        locationRequest!!.fastestInterval = (5 * 1000).toLong() // 5 seconds

        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // Encender GPS
                isGPS = isGPSEnable
            }
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        txtEstatus!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient!!.removeLocationUpdates(locationCallback!!)
                        }
                    }
                }
            }
        }

        actualizarBT!!.setOnClickListener { v ->

            if (!isGPS) {
                Toast.makeText(this, getString(R.string.GPS_mensaje), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getLocation()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )

        } else {
            mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@MainActivity) { location ->
                if (location != null) {
                    wayLatitude = location.latitude
                    wayLongitude = location.longitude
                    txtEstatus!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                } else {
                    mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@MainActivity) { location ->
                        if (location != null) {
                            wayLatitude = location.latitude
                            wayLongitude = location.longitude
                            txtEstatus!!.text = String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude)
                        } else {
                            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.Permiso_denegado), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }

}

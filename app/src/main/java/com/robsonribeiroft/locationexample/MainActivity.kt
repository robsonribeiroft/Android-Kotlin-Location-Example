package com.robsonribeiroft.locationexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var geocoder: Geocoder? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var text: TextView? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text = findViewById(R.id.text) as TextView
        button = findViewById(R.id.button) as Button

        button!!.setOnClickListener(View.OnClickListener {
            text!!.text = "permission"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
                    return@OnClickListener
                }
            }
            text!!.text = "loading..."
            geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try {
                        val addresses = geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
                        val placeHolder = text!!.text.toString()
                        text!!.text = "$placeHolder\n${addresses[0].getAddressLine(0)}"
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

                override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

                }

                override fun onProviderEnabled(s: String) {

                }

                override fun onProviderDisabled(s: String) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }


            /*
                get location every 1 second, but can select by distance traveled
                * */
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, locationListener)

            //Remove location updates
            //locationManager.removeUpdates(locationListener);

            //Get location just once
            //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                button!!.callOnClick()
            }
        }
    }

    companion object {

        private val MY_PERMISSIONS_REQUEST_LOCATION = 10
    }

}

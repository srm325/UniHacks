package com.srm325.navsafe.ui.features.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.srm325.navsafe.R
import com.srm325.navsafe.data.Repository
import com.srm325.navsafe.data.model.Post
import kotlinx.android.synthetic.main.map_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


private lateinit var mMap: GoogleMap
private val repository = Repository()
private lateinit var viewModel: MapViewModel
val addressList: ArrayList<String> = ArrayList<String>(listOf(""))
val incidentList: ArrayList<String> = ArrayList<String>(listOf(""))
lateinit var mLocationRequest: LocationRequest
var mLastLocation: Location? = null
lateinit var myMarker: Marker
internal var mCurrLocationMarker: Marker? = null
internal var mFusedLocationClient: FusedLocationProviderClient? = null
var currentAdminArea : String = ""
lateinit var source : LatLng
lateinit var destination:LatLng


class ChatListFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        fun newInstance() = ChatListFragment()
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        @SuppressLint("BinaryOperationInTimber")
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Timber.e("MapsActivity" + "Location: " + location.latitude + " " + location.longitude)
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }
                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))
                if (latLng != null) {
                    source = latLng
                    currentAdminArea = getCityFromLatLng(
                            activity,
                            LatLng(location.latitude, location.longitude)
                    ) as String
                    Timber.e("Admin area$currentAdminArea")
                    for (i in addressList) {
                        var county = getCityFromAddress(activity, i) as String
                        if (county == currentAdminArea) {
                            var address123 = getLocationFromAddress(activity, i) as LatLng
                            Timber.e(address123.toString())
                            mMap.addMarker(
                                    MarkerOptions()
                                            .position(address123)
                                            .title("Crime spot")
                                            .icon(
                                                    BitmapDescriptorFactory.defaultMarker(
                                                            BitmapDescriptorFactory.HUE_RED
                                                    )
                                            )

                            )
                            mMap.addCircle(
                                    CircleOptions()
                                            .center(address123)
                                            .radius(100.0)
                                            .strokeWidth(3F)
                                            .strokeColor(Color.RED)
                                            .fillColor(Color.parseColor("#22ff0400"))
                            )
                        }
                    }
                }
            }
        }
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.map_fragment, container, false)
//allow strict mode

        //allow strict mode
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val fm: FragmentManager = childFragmentManager;
        var mapFragment = fm.findFragmentById(R.id.map) as SupportMapFragment?
        val searchBtn: ImageButton = view.findViewById(R.id.searchbutton)

            searchBtn.setOnClickListener {
                if (searchbox != null){
                    mMap.clear()
                    val address12 = getLocationFromAddress(activity, searchbox.text.toString()) as LatLng
                    Timber.e(address12.toString())
                    destination=address12
                    myMarker = mMap.addMarker(
                            MarkerOptions()
                                    .position(address12)
                                    .title(searchbox.text.toString())
                                    .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                    BitmapDescriptorFactory.HUE_GREEN
                                            )
                                    )

                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address12, 15F))
                    searchbox.text = null

            } else {

                }
        }

        val user = Firebase.auth.currentUser
        val postList:MutableList<Post> = mutableListOf()
        val db = Firebase.firestore
        mFusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        db.collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val post = document.toObject(Post::class.java)
                        if (post.address != null) {
                            addressList.add(post.address)
                            incidentList.add(post.description)
                        }
                    }
                }
        addressList.removeAt(0)
        incidentList.removeAt(0)
        Timber.e(addressList.toString())


        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit()
            Timber.e("I tried")
        }
        if (mapFragment == null) {
            Timber.e("Get fucked")
        }else{
            Timber.e("Map not null")
            mapFragment.getMapAsync(this)
        }

        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

    }
    fun getCurrentUser() = repository.getCurrentUser()
    @SuppressLint("BinaryOperationInTimber")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val settings: UiSettings = mMap.uiSettings
        settings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this);
        googleMap.isTrafficEnabled = true
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 6000000 // 60s interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.let {
                        ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient?.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                )
                mMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
            )
            mMap.isMyLocationEnabled = true
        }

    }


    private fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng = LatLng(0.0, 0.0)
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                Timber.e("Address not found")
            } else {
                val location: Address = address[0]
                p1 = LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p1
    }
    private fun checkLocationPermission() {
        if (activity?.let {
                    ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (activity?.let {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }!!
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                context?.let {
                    AlertDialog.Builder(it)
                            .setTitle("Location Permission Needed")
                            .setMessage("This app needs the Location permission, please accept to use location functionality")
                            .setPositiveButton(
                                    "OK"
                            ) { _, _ ->
                                //Prompt the user once explanation has been shown
                                activity?.let { it1 ->
                                    ActivityCompat.requestPermissions(
                                            it1,
                                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                            MY_PERMISSIONS_REQUEST_LOCATION
                                    )
                                }
                            }
                            .create()
                            .show()
                }


            } else {
                // No explanation needed, we can request the permission.
                activity?.let {
                    ActivityCompat.requestPermissions(
                            it,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                    )
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (activity?.let {
                                ContextCompat.checkSelfPermission(
                                        it,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            } == PackageManager.PERMISSION_GRANTED
                    ) {

                        mFusedLocationClient?.requestLocationUpdates(
                                mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper()
                        )
                        mMap.isMyLocationEnabled = true
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    private fun getCityFromAddress(context: Context?, strAddress: String?): String? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: String = ""
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                Timber.e("Address not found")
            } else {
                val location: Address = address[0]
                p1 = location.subAdminArea
                Timber.e(p1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p1
    }
    private fun getUrl(origin: LatLng, dest: LatLng): String? {
        val strorigin = "origin=" + origin.latitude.toString() + "," + origin.longitude
        val strdest = "destination=" + dest.latitude.toString() + "," + dest.longitude
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val parameters = "$strorigin&$strdest&$sensor&$mode"
        val output = "json"
        val API_KEY: String = "AIzaSyDMNds7jkm7x5t6YixsjDTz-_iywFW9uqY"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$API_KEY"
/*HERE
        val str_origin = "origin=" + origin.latitude.toString() + "%2C" + origin.longitude
        val str_dest = "destination=" + dest.latitude.toString() + "%2c" + dest.longitude
        val transportmode = "transportMode=car"
        val parameters = "$str_origin&$transportmode&$str_dest"
        val output = "return=polyline"
        val API_KEY: String = "-2tUjsluW_sYRxJK8MewPG0ug4AfXEUC7I1aPAd5RV4"
        return "https://router.hereapi.com/v8/routes?$parameters&$output&apikey=$API_KEY"

 */
    }

    override fun onMarkerClick(marker: Marker): Boolean {

            val data: String
            var inputStream: InputStream? = null
            var connection: HttpURLConnection? = null
            val directionUrl = URL(getUrl(source, marker.position))
            Timber.e(directionUrl.toString())
            connection = directionUrl.openConnection() as HttpURLConnection
            connection.connect()
            inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuffer = StringBuffer()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            data = stringBuffer.toString()
            bufferedReader.close()

            inputStream.close()
            connection.disconnect()
            val doc = JSONObject(data)
            /*HERE APi
            val routes = doc.getJSONArray("routes")
            val sections = routes.getJSONObject(0).getJSONArray("sections")
            val polylin = sections.getJSONObject(0).getString("polyline")

             */
            //Directions API
            val routes = doc.getJSONArray("routes")
            val sections = routes.getJSONObject(0).getJSONObject("overview_polyline")
            val polylin = sections.getString("points")


            Timber.e(polylin.toString())
            val decoded: List<LatLng> = PolyUtil.decode(polylin)
            Timber.e(decoded.toString())
            var latLNG  = source
            for (e in decoded) {
                Timber.e(e.toString())
                mMap.addPolyline(PolylineOptions()
                    .add(latLNG, e)
                    .width(10F)
                    .color(Color.BLUE))
                latLNG = e

        }
        return true
    }

    fun getCityFromLatLng(context: Context?, latlong: LatLng?): String? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: String = ""
        try {
            if (latlong != null) {
                address = coder.getFromLocation(latlong.latitude, latlong.longitude, 1)
                if (address == null) {
                    Timber.e("Address not found")
                } else {
                    val location: Address = address[0]
                    p1 = location.subAdminArea
                    Timber.e(p1)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p1
    }

}

/*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.srm325.gobble.R


class ChatListFragment : Fragment(){

    companion object {
        fun newInstance() = ChatListFragment()
    }

    private lateinit var viewModel: ChatListViewModel
   /* var locationRequest: LocationRequest? = null
    var locationCallback: LocationCallback? = null
    var locationProvider: FusedLocationProviderClient? = null
    var MAP: GoogleMap? = null

    */
    var lat = 43.1226686
    var lon = -77.5901883

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_list_fragment, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatListViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val coder = Geocoder(activity)
            MAP = googleMap
            val area = LatLng(lat, lon);
            MAP!!.addMarker(MarkerOptions().position(area).title("Current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            MAP!!.moveCamera(CameraUpdateFactory.newLatLng(area))

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MAPEXCEPTION", e.message!!)
        }

    }



}
        */



package com.example.kotlin_bel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.kotlin_bel.databinding.ActivityMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region

//import com.google.firebase.Goe

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    lateinit var geocoder: Geocoder
    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest
//    lateinit var locationCallback: LocationCallback

    private var userLocationMarker: Marker? = null
    private var userLocationAccuracyCircle : Circle? = null



    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    lateinit var region: Region

    lateinit var beaconReferenceApplication: BeaconReferenceApplication

    private lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    private lateinit var database: DatabaseReference



    private var databasew: FirebaseDatabase = FirebaseDatabase.getInstance()

//
    private var dbReference: DatabaseReference = databasew.getReference("usersLcation")

    private lateinit var fusedLocClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beaconReferenceApplication = application as BeaconReferenceApplication

        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(beaconReferenceApplication.region)

        regionViewModel.rangedBeacons.observe(this, rangingObserver)

        auth = Firebase.auth
        Log.e("TGSSS", "====: " + auth)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        geocoder = Geocoder(this);

//


        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(MainActivity.TAG, "signInAnonymously:success")
                    val user = auth.currentUser
//                    updateUI(user)
//                    println(">>>>>>>>>>>>>>>${user}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MainActivity.TAG, "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }


        locationRequest = LocationRequest.create().apply {
            interval = 500
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            maxWaitTime = 8000
        }
//        dbReference = Firebase.database.reference

        database = Firebase.database.reference

//        dbReference.addValueEventListener(locListener)



        getLocationsOnMap()

    }



    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            println(">>>>>>>>>>>>>>>${currentUser.uid}")
        }
//        updateUI(currentUser)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            // you need to request permissions...
        }

//        getLocationsOnMap()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
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

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        getCurrentLocation()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
                REQUEST_LOCATION)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), //permission in the manifest
                REQUEST_LOCATION)
        }
        else{
            mMap.isMyLocationEnabled = true
        }
//        getAllLocations()
        dbReference.addValueEventListener(locListener)
//        mMap.isMyLocationEnabled = true
    }




        val rangingObserver = Observer<Collection<Beacon>> { beacons ->
            Log.d(MainActivity.TAG, "Ranged>>>>>>>: ${beacons.count()} beacons")
            if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {


            }

        }







    companion object {
        private const val REQUEST_LOCATION = 1 //request code to identify specific permission request
        private const val TAG = "MapsActivity" // for debugging
    }


    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION)
    }


    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
//            Log.e(TAG, "**********onLocationResult====: " +locationResult.lastLocation.latitude)

            if (auth.currentUser != null) {

//                database.child("usersLcation").child(auth.currentUser!!.uid).setValue(locationResult.lastLocation)
               var userLocationPhat =  database.child("usersLcation")
                var geoUserLocationPhat = GeoFire(userLocationPhat)
                var userUid = auth.currentUser!!.uid
                var locationResults = locationResult.lastLocation

                geoUserLocationPhat.setLocation(userUid, GeoLocation(locationResults.latitude, locationResults.longitude))
//
            }

            if (mMap != null) {
                setUserLocationMarker(locationResult.lastLocation)



            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocClient.removeLocationUpdates(locationCallback)
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
                REQUEST_LOCATION)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), //permission in the manifest
                REQUEST_LOCATION)
        }
        else{
            fusedLocClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

    }


    fun setUserLocationMarker(location: Location){
        val latLng = LatLng(location.latitude, location.longitude)

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17F))

        /*

//       var userLocationMarker: Marker

        if(userLocationMarker ==  null){
            //Create a new marker
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))
            markerOptions.rotation(location.bearing)
            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            userLocationMarker = mMap.addMarker(markerOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))


//            if (auth.currentUser != null) {
//                Log.e(TAG, "VVVVVVVVVV: " + latLng)
//                db.collection("usersLocation").document(auth.currentUser!!.uid)
//                    .set(latLng)
//                    .addOnSuccessListener { documentReference ->
//                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w("TAG", "Error adding document", e)
//                    }
//            }
        }else{
            userLocationMarker!!.setPosition(latLng);
            userLocationMarker!!.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F));
        }

        if (userLocationAccuracyCircle == null) {
            val circleOptions = CircleOptions()
            circleOptions.center(latLng)
            circleOptions.strokeWidth(4f)
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
            circleOptions.fillColor(Color.argb(32, 255, 0, 0))
            circleOptions.radius(location.accuracy.toDouble())
            userLocationAccuracyCircle = mMap.addCircle(circleOptions)
        } else {
            userLocationAccuracyCircle!!.center = latLng
            userLocationAccuracyCircle!!.radius = location.accuracy.toDouble()
        }

        */


    }

    fun getLocationsOnMap(){


    }

    val locListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {



            for(sn in snapshot.children ){

                var locationUpdateLat = sn.child('l'.toString()).child('0'.toString()).getValue()
                var locationUpdateLog = sn.child('l'.toString()).child('1'.toString()).getValue()
//                Log.e(MainActivity.TAG, "########################<<<<<=> ${locationUpdate}")

//                val location = sn.getValue(LocationInfo::class.java)
//                val locationLat = location?.latitude
//                val locationLong = location?.longitude

//                Log.e(TAG, "**********onLocationResult====: " +location)

                if (locationUpdateLat != null && locationUpdateLog!= null) {
                    // create a LatLng object from location
                    val latLng = LatLng(locationUpdateLat as Double, locationUpdateLog as Double)
//                    Log.e(TAG, "**********onLocationResult====: " +latLng)
                    //create a marker at the read location and display it on the map

                        //Create a new marker
                        val markerOptions = MarkerOptions()
                        markerOptions.position(latLng)
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))
                        markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
                        userLocationMarker = mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))


                }
                else {
                    // if location is null , log an error message
                    Log.e(TAG, "user location cannot be found")
             }


////
////
////                 val markerOptions = MarkerOptions()
////                 markerOptions.position(latLng)
////                 markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))
//// //                                    markerOptions.rotation(location.bearing)
////                 markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
////                 userLocationMarker = mMap.addMarker(markerOptions)
////                 mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))
            }


//            val post = dataSnapshot.getValue<Post>()

//            var locationUpdate = snapshot.getValue<Location>()
//            Log.e(MainActivity.TAG, "########################<<<<<=> ${locationUpdate}")

//            for (dc in snapshot.children ) {
//
//                        Log.e(MainActivity.TAG, "########################<<<<<=> ${dc.}")
////                        val lat = dc.getValue("sa","dsd")
////                        val lng = dc.getValue()
//
////                        val latLng = LatLng(lat as Double, lng as Double)
////
//////                        location.latitude, location.longitude
////                        mMap.addMarker(MarkerOptions().position(latLng)
////                            .title("You are currently here!"))
////                        // create an object that will specify how the camera will be updated
////                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
////
////                        mMap.moveCamera(update)
//////
//                    }

//            if(snapshot.exists()){
////                val values = value as List<Any>
//                val map = snapshot.value as(List<Any>)
//                var locationLat = 0.0
//                var locationLang = 0.0
//
//                if (map[0] != null){
//                    locationLat = map[0].toString().toDouble()
//                }
//                if (map[1] != null){
//                    locationLang = map[1].toString().toDouble()
//                }
//
////                    val latLng = LatLng(locationLat, locationLong)
//                val latLng = LatLng(locationLat, locationLang)
//                val markerOptions = MarkerOptions()
//                markerOptions.position(latLng)
//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))
//                markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
//                userLocationMarker = mMap.addMarker(markerOptions)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))
//            }

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }





    fun getAllLocations(){
        database.child("usersLcation").get().addOnSuccessListener{v->
                        Log.e(TAG, "**********onLocationResult^^^^^^^====: " + v.child(auth.currentUser!!.uid).child("l").child("0").value)
                        Log.e(TAG, "**********onLocationResult^^^^^^^====: " + v.child(auth.currentUser!!.uid).child("l").child("1").value)

            var lat = v.child(auth.currentUser!!.uid).child("l").child("0").value
            var log = v.child(auth.currentUser!!.uid).child("l").child("1").value

            val latLng = LatLng(lat as Double, log as Double)
//                    Log.e(TAG, "**********onLocationResult====: " +latLng)
                    //create a marker at the read location and display it on the map

                        //Create a new marker
                        val markerOptions = MarkerOptions()
                        markerOptions.position(latLng)
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bicycle))
                        markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
                        userLocationMarker = mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17F))
//            child(auth.currentUser!!.uid)
        }.addOnFailureListener {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION)
        {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission has been denied")
            }
        }
    }
}
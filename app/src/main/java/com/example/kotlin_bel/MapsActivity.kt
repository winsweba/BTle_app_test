package com.example.kotlin_bel

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.kotlin_bel.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    lateinit var region: Region

    lateinit var beaconReferenceApplication: BeaconReferenceApplication

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    private lateinit var fusedLocClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beaconReferenceApplication = application as BeaconReferenceApplication

        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(beaconReferenceApplication.region)

        regionViewModel.rangedBeacons.observe(this, rangingObserver)

        auth = Firebase.auth

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocClient =
            LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


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

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            println(">>>>>>>>>>>>>>>${currentUser.uid}")
        }
//        updateUI(currentUser)
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
        getCurrentLocation()
    }




        val rangingObserver = Observer<Collection<Beacon>> { beacons ->
            Log.d(MainActivity.TAG, "Ranged>>>>>>>: ${beacons.count()} beacons")
            if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {

//            beaconCountTextView.text = "Ranging enabled: ${beacons.count()} beacon(s) detected"
//            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,

//               val rssiV = beacons
//                    .map { "${it.rssi}" }.toList()

//            println("<<<<>>>>>>>>> ${rssiV[0]}")

//            val rssiV = beacons
//            val aa = beacons
//                .sortedBy { it.distance }
//                .map { "${it.id1}\nid2: ${it.id2} id3:${it.id3} \n rssi: ${it.rssi}\nest. distance: ${it.distance} m" }.toTypedArray()
//
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//
//                // call requestLocPermissions() if permission isn't granted
//                requestLocPermissions()
//            } else {
//                fusedLocClient.lastLocation.addOnSuccessListener { location ->
//                    val latLng = LatLng(location.latitude, location.longitude)
//                    if (location != null && beacons.isNotEmpty()){
//
//                         //create a marker at the exact location
////                        mMap.addMarker(MarkerOptions().position(latLng)
////                            .title("You are currently here!"))
////                        // create an object that will specify how the camera will be updated
////                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
////
////                        mMap.moveCamera(update)
//                    val locationLatLong = hashMapOf(
//                        "latitude" to location.latitude,
//                        "longitude" to location.longitude,
////                        "rssi" to  rssiV
//                    )
//
//                        if (auth.currentUser != null) {
//                            db.collection("usersLocation").document(auth.currentUser!!.uid)
//                                .set(locationLatLong)
//                                .addOnSuccessListener { documentReference ->
//                                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
//                                }
//                                .addOnFailureListener { e ->
//                                    Log.w("TAG", "Error adding document", e)
//                                }
//                        }
//                    }
////                println(">>>>>> ${location.latitude}")
////                println(">>>>>> ${location.longitude}")
//                }
//            }

                fun getLocationsOnMap(){

        db.collection("usersLocation")
//                    db.collection("users")
                        .addSnapshotListener { snapshots, e ->
                            if (e != null) {
                                Log.w(MainActivity.TAG, "listen:error", e)
                                return@addSnapshotListener
                            }

                            if (snapshots != null) {

//

                                for (dc in snapshots) {

                                    Log.e(MainActivity.TAG, "########################<<<<<=> ${dc.data.getValue("latitude")}")
                                    val lat = dc.data.getValue("latitude")
                                    val lng = dc.data.getValue("longitude")

                                    val latLng = LatLng(lat as Double, lng as Double)

//                        location.latitude, location.longitude
                                    mMap.addMarker(MarkerOptions().position(latLng)
                                        .title("You are currently here!"))
                                    // create an object that will specify how the camera will be updated
                                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                                    mMap.moveCamera(update)
//
                                }
//
                            }
                        }
                }
                if(beacons.isNotEmpty()){
                    getLocationsOnMap()
                    Log.e(MainActivity.TAG, "########################<<<<<<<=====<=>>>>>")
                }

//
            }

        }




    fun getCurrentLocation() {

                if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

            // call requestLocPermissions() if permission isn't granted
            requestLocPermissions()
        } else {
            fusedLocClient.lastLocation.addOnSuccessListener { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                if (location != null){

                    // create a marker at the exact location
                    mMap.addMarker(MarkerOptions().position(latLng)
                        .title("You are currently here!"))
                    // create an object that will specify how the camera will be updated
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    mMap.moveCamera(update)



                    if (auth.currentUser != null) {
                        db.collection("usersLocation").document(auth.currentUser!!.uid)
                            .set(latLng)
                            .addOnSuccessListener { documentReference ->
                                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error adding document", e)
                            }
                    }


                }
//                println(">>>>>> ${location.latitude}")
//                println(">>>>>> ${location.longitude}")
            }
        }

        /*

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

            // call requestLocPermissions() if permission isn't granted
            requestLocPermissions()
        } else {

            fusedLocClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                if (location != null) {

                    val latLng = LatLng(location.latitude, location.longitude)
                    // create a marker at the exact location
                    mMap.addMarker(MarkerOptions().position(latLng)
                        .title("You are currently here!"))
                    // create an object that will specify how the camera will be updated
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    mMap.moveCamera(update)
                } else {
                    // if location is null , log an error message
                    Log.e(TAG, "No location found")
                }



            }
        }

        */
    }



//    fun getLocationsOnMap(){
//
////        db.collection("usersLocation")
////            .get()
////            .addOnSuccessListener { result ->
////                for (document in result) {
////                    Log.e("MapsActivity.TAG", "########################${document} => ${document.data.getValue("latitude")}")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.d("MapsActivity.TAG", "Error getting documents: ^^^^^^^^^^^^^^^^^^^^^^^^", exception)
////            }
////
//
////        db.collection("users")
////            .addSnapshotListener { value, e ->
////                if (e != null) {
////                    Log.w(TAG, "Listen failed.", e)
////                    return@addSnapshotListener
////                }
////
//////                val cities = ArrayList<String>()
////                for (doc in value!!) {
//////                    doc.getString("name")?.let {
//////                        cities.add(it)
//////                    }
////                    Log.e(TAG, "Listen failed.$doc" )
////                }
//////                Log.d(TAG, "Current cites in CA: $cities")
////            }
//
////        db.collection("usersLocation")
//        db.collection("users")
//            .addSnapshotListener { snapshots, e ->
//                if (e != null) {
//                    Log.w(MainActivity.TAG, "listen:error", e)
//                    return@addSnapshotListener
//                }
//
//                if (snapshots != null) {
//
////
//
//                    for (dc in snapshots) {
//
//                        Log.e(MainActivity.TAG, "########################<<<<<=> ${dc.data.getValue("latitude")}")
//                        val lat = dc.data.getValue("latitude")
//                        val lng = dc.data.getValue("longitude")
//
//                        val latLng = LatLng(lat as Double, lng as Double)
//
////                        location.latitude, location.longitude
//                        mMap.addMarker(MarkerOptions().position(latLng)
//                            .title("You are currently here!"))
//                        // create an object that will specify how the camera will be updated
//                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
//
//                        mMap.moveCamera(update)
////
//                    }
////
//                }
//            }
//    }



    companion object {
        private const val REQUEST_LOCATION = 1 //request code to identify specific permission request
        private const val TAG = "MapsActivity" // for debugging
    }


    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION)
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
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission has been denied")
            }
        }
    }
}
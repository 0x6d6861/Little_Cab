package co.heri.littlecab.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import co.heri.littlecab.adapters.BottomSheetSliderAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import co.heri.littlecab.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition


class MainFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mMap: GoogleMap
    private lateinit var mMapView: MapView
    private lateinit var viewLayout: View

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var mLocationRequest: LocationRequest


    private val REQUEST_FINE_LOCATION=0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         viewLayout = inflater.inflate(R.layout.main_fragment, container, false)

            this.initBottomSheet(viewLayout);

            val viewPager = viewLayout.findViewById(R.id.pager) as ViewPager
            viewPager.adapter = BottomSheetSliderAdapter()

            val bottomTab = viewLayout.findViewById<TabLayout>(R.id.bottom_tab);
            bottomTab.setupWithViewPager(viewPager, true)

            viewLayout.findViewById<View>(R.id.place_picker_search).setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }


        this.initializeMap(savedInstanceState);

        viewLayout.findViewById<Button>(R.id.myLocation).setOnClickListener {

            LocationServices.getFusedLocationProviderClient(this.activity!!.parent).lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val position = CameraPosition.Builder()
                        .target(LatLng(it.latitude, it.longitude))
                        .zoom(15.0f)
                        .build()
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
                }
            }

        }

        return viewLayout;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel


    }



    private fun initializeMap(savedInstanceState: Bundle?) {
        mMapView = viewLayout.findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }



        mMapView.getMapAsync(this@MainFragment)

        //Initializing googleApiClient
        googleApiClient = GoogleApiClient.Builder(this@MainFragment.context!!)
                .addConnectionCallbacks(this@MainFragment)
                .addOnConnectionFailedListener(this@MainFragment)
                .addApi(LocationServices.API)
                .build();

    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this@MainFragment.context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this@MainFragment.activity?.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION
            )
        }
    }

    private fun initBottomSheet(viewLayout: View){

        val place_picker = viewLayout.findViewById<View>(R.id.place_picker);

        var place_picker_height = 0


        place_picker.afterLayout {
            place_picker_height = place_picker.height;
            place_picker.translationY = place_picker_height * -1f;

        }

        bottomSheetBehavior =
            BottomSheetBehavior.from(viewLayout.findViewById(R.id.bottom_sheet) as View);
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
                place_picker.translationY = interpolateValues(slideOffset, place_picker_height * -1f, 0f)
//                Log.e("D/SLIDE", interpolateValues(slideOffset,5f, 58f).toString())
                place_picker.alpha = interpolateValues(slideOffset, 0f, 1f)
            }
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try { // Customise the styling of the base map using a JSON object defined
// in a raw resource file.
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@MainFragment.context, R.raw.gmaps_style
                )
            )


            mMap.setPadding(50, 50, 50, 750);
            mMap.uiSettings.isCompassEnabled = false;

            if (!success) {
                Log.e("MAP_FRAGMENT", "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e("MAP_FRAGMENT", "Can't find style. Error: ", e)
        }

        if(checkPermissions()) {
            mMap.isMyLocationEnabled = true;
//        mMap.uiSettings.isMyLocationButtonEnabled = false;
        } else {
            // show why location is mendatory
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    private fun interpolateValues(fraction: Float, startValue: Float, endValue: Float): Float {
        return startValue + (fraction * (endValue - startValue)).toFloat()
    }

    fun View.afterLayout(what: () -> Unit) {
        if(isLaidOut) {
            what.invoke()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    what.invoke()
                }
            })
        }
    }



    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onConnected(p0: Bundle?) {
        //
    }

    override fun onConnectionSuspended(p0: Int) {
        //
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //
    }

}


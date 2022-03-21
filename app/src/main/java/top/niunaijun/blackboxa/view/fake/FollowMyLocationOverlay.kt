package top.niunaijun.blackboxa.view.fake


import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackboxa.databinding.ActivityOsmdroidBinding
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast


/**
 *
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/14
 */
class FollowMyLocationOverlay : AppCompatActivity() {
    val TAG: String = "FollowMyLocationOverlay"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private val binding: ActivityOsmdroidBinding by inflate()

    lateinit var startPoint: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map
        setContentView(binding.root)

        val location: BLocation? = intent.getParcelableExtra("location")

        startPoint = if (location == null) {
            GeoPoint(30.2736, 120.1563)
        } else {
            GeoPoint(location.latitude, location.longitude)
        }


        val startMarker = Marker(binding.map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        binding.map.overlays.add(startMarker)
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                startPoint = p
                startMarker.position = p
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                binding.map.overlays.add(startMarker)
                toast(p.latitude.toString() + " - " + p.longitude)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        binding.map.overlays.add(MapEventsOverlay(mReceive))
        val mapController = binding.map.controller
        mapController.setZoom(12.5)
//        val startPoint = GeoPoint(30.2736, 120.1563)
        mapController.setCenter(startPoint)
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
    }

    override fun onBackPressed() {
        finishWithResult(startPoint)
    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding.map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this)
        //Configuration.getInstance().save(this, prefs)
        binding.map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun finishWithResult(geoPoint: GeoPoint) {
        intent.putExtra("latitude", geoPoint.latitude)
        intent.putExtra("longitude", geoPoint.longitude)
        setResult(Activity.RESULT_OK, intent)
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.run {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        finish()
    }

}
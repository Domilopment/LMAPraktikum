package de.dmmm.lmapraktikum.ui.locationmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.dmmm.lmapraktikum.R
import com.google.android.gms.maps.SupportMapFragment
import de.dmmm.lmapraktikum.MainActivity
import org.json.JSONObject

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var model: LocationManagerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //model = ViewModelProviders.of(activity!!).get(LocationManagerViewModel::class.java)
        model = (activity as MainActivity).myService.model

        return view
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

        model.point.observe(this.activity!!, Observer< JSONObject > { updatedObject ->
            if (updatedObject != null) {
                val pos = model.getPos()
                val myPos = LatLng(
                    pos[0],
                    pos[1]
                )
                mMap!!.addMarker(MarkerOptions().position(myPos).title("My Position"))
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 10f))
            }
        })
    }
}

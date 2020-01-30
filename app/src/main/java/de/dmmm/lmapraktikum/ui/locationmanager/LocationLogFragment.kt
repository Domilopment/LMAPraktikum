package de.dmmm.lmapraktikum.ui.locationmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.dmmm.lmapraktikum.R
import android.widget.TextView
import androidx.lifecycle.Observer
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.dmmm.lmapraktikum.MainActivity
import de.dmmm.lmapraktikum.locationTracker.FileHelper
import org.json.JSONArray
import org.json.JSONObject

class LocationLogFragment : Fragment() {

    private lateinit var log: TextView
    private lateinit var model: LocationManagerViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var data: JSONArray

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_location_log, container, false)

        log = root.findViewById(R.id.location_logger)
        log.movementMethod = ScrollingMovementMethod()

        fab = root.findViewById(R.id.fab)
        Log.e("init?", fab.toString())
        fab.setOnClickListener { view ->
            if (::data.isInitialized)
                FileHelper.saveToFile(data.toString())
            Snackbar.make(view, "Log is Saved", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        //model = ViewModelProviders.of(activity!!).get(LocationManagerViewModel::class.java)
        model = (activity as MainActivity).myService.model

        model.log.observe(this.activity!!, Observer<String> { updatedObject ->
            if (updatedObject != null) {
                this.log.text = updatedObject
                this.log.append("\n")
            }
        })

        model.point.observe(this.activity!!, Observer<JSONObject> { updatedObject ->
            if (updatedObject != null) {
                this.data = model.getData()
            }
        })

        return root
    }
}
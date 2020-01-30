package de.dmmm.lmapraktikum.ui.sensors

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.dmmm.lmapraktikum.R
import android.view.LayoutInflater
import androidx.fragment.app.Fragment

class SensorsLogFragment : Fragment() {
    private lateinit var log: TextView
    private lateinit var model: SensorsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sensors_log, container, false)

        log = view.findViewById(R.id.sensors_logger)
        log.movementMethod = ScrollingMovementMethod()

        model = ViewModelProviders.of(activity!!).get(SensorsViewModel::class.java)

        model.log.observe(this.activity!!, Observer<String> { updatedObject ->
            if (updatedObject != null) {
                log.text = (updatedObject)
                log.append("\n")
            }
        })

        return view
    }
}
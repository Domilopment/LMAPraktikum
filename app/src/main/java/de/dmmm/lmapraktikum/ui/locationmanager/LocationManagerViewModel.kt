package de.dmmm.lmapraktikum.ui.locationmanager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

class LocationManagerViewModel : ViewModel() {
    private val data = JSONArray()
    var log = MutableLiveData<String>()
    var point = MutableLiveData<JSONObject>()

    init {
        log.value = ""
    }

    fun getLog(): String? {
        return log.value
    }

    fun setLog(item: String) {
        this.log.value += "\n$item\n"
    }

    fun getPoint(): JSONObject? {
        return point.value
    }

    fun setPoint(point: JSONObject){
        this.point.value = point
        this.data.put(point)
    }

    fun getData(): JSONArray {
        return data
    }

    fun getPos(): DoubleArray {
        val o = point.value
        return doubleArrayOf(
            o!!.getDouble("latitude"),
            o.getDouble("longitude")
        )
    }
}

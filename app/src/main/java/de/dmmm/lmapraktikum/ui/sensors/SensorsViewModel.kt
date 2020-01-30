package de.dmmm.lmapraktikum.ui.sensors

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorsViewModel : ViewModel() {
    var log = MutableLiveData<String>()

    init {
        log.value = ""
    }

    fun getLog(): String? {
        return log.value
    }

    fun setLog(item: String) {
        this.log.value += "\n$item\n"
    }
}

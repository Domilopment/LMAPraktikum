package de.dmmm.lmapraktikum.locationTracker

import android.location.Location

interface OnLocationChangeListener {
    /**
     * To be called from Tracker Classes when Location is changed to notify MainActivity
     */
    fun onLocationChanged(location: Location)
    /**
     * Add Logger records
     * @param str Text to be logged
     */
    fun setLogRecord(str: String)
}

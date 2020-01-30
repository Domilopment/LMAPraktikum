package de.dmmm.lmapraktikum.service

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import de.dmmm.lmapraktikum.MainActivity
import de.dmmm.lmapraktikum.R
import de.dmmm.lmapraktikum.settings.SettingsActivity
import de.dmmm.lmapraktikum.ui.locationmanager.LocationManagerViewModel
import de.dmmm.lmapraktikum.locationTracker.GPSFusedLocationTracker
import de.dmmm.lmapraktikum.locationTracker.GPSLocationTracker
import de.dmmm.lmapraktikum.locationTracker.OnLocationChangeListener
import org.jetbrains.anko.doAsync
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Future

class MyService : Service(), OnLocationChangeListener {
    private val CHANNEL_ID = "GPSTracker"
    private val NOTIFICATION_ID = 1
    private val binder = MyBinder()

    private lateinit var task: Future<Unit>

    private var switchPrefGPS = false
    private var switchPrefNetwork = false
    private var seekBarPrefGPS = 1000
    private var listPrefGPS = ""
    private var switchPrefFuesed = false
    private var listPrefAccuracy = "100"
    private var seekBarPrefFuesed = 1000


    private lateinit var gpsLocationTracker: GPSLocationTracker
    private lateinit var gpsFusedLocationTracker: GPSFusedLocationTracker
    private var mainActivity: MainActivity? = null
    lateinit var model: LocationManagerViewModel
    private var change = false

    private fun buildNotification(text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            0
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Count GPS Tracks")
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .addAction(0, "Stop", null)
            .build()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel(notificationManager!!)

        val notification = buildNotification("")
        startForeground(NOTIFICATION_ID, notification)

        task = doAsync {
            while (true) {
                Thread.sleep(100)
                if (change) {
                    change = false
                    var point = model.getPoint()
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        buildNotification("Gesammelte Daten: " + model.getData().length() +
                                "                                                       " +
                                "\n\nLatitude: " + point!!.get("latitude") +
                                "\nLongitude: " + point.get("longitude") +
                                "\nAltitude: " + point.get("altitude"))
                    )
                }
            }
        }
        return START_STICKY
    }

    fun startLocationLoggin(activity: MainActivity){
        if (mainActivity == null) {
            mainActivity = activity
            gpsLocationTracker = GPSLocationTracker(mainActivity, this)
            gpsFusedLocationTracker = GPSFusedLocationTracker(mainActivity, this)
            model = ViewModelProviders.of(mainActivity!!).get(LocationManagerViewModel::class.java)
        }

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        switchPrefGPS = prefs.getBoolean(SettingsActivity.KEY_PREF_SWITCH_GPS, false)
        switchPrefNetwork = prefs.getBoolean(SettingsActivity.KEY_PREF_SWITCH_NETWORK, false)
        seekBarPrefGPS = prefs.getInt(SettingsActivity.KEY_PREF_SEEKBAR_GPS, 1000)
        listPrefGPS = prefs.getString(SettingsActivity.KEY_PREF_LIST_RECEIVER, "").toString()
        switchPrefFuesed = prefs.getBoolean(SettingsActivity.KEY_PREF_SWITCH_FUESED, false)
        listPrefAccuracy = prefs.getString(SettingsActivity.KEY_PREF_LIST_ACCURACY, "100").toString()
        seekBarPrefFuesed = prefs.getInt(SettingsActivity.KEY_PREF_SEEKBAR_FUESED, 1000)

        if (listPrefGPS == "LocationManager") {
            gpsFusedLocationTracker.stopTracking()
            gpsLocationTracker.trackingTimeThreshold = seekBarPrefGPS.toLong()
            gpsLocationTracker.stopTracking()
            if (switchPrefNetwork) {
                gpsLocationTracker.startTrackingWithNetwork()
            }
            if (switchPrefGPS) {
                gpsLocationTracker.startTracking()
            }
        } else if (listPrefGPS == "FusedLocationProvider") {
            gpsLocationTracker.stopTracking()
            gpsFusedLocationTracker.setInterval(seekBarPrefFuesed.toLong())
            when(listPrefAccuracy.toInt()){
                100 -> gpsFusedLocationTracker.setHighAccuracy()
                102 -> gpsFusedLocationTracker.setBalancedAccuracy()
                104 -> gpsFusedLocationTracker.setLowAccuracy()
                105 -> gpsFusedLocationTracker.setLowestAccuracy()
            }
            gpsFusedLocationTracker.stopTracking()
            if (switchPrefFuesed) {
                gpsFusedLocationTracker.startTracking()
            }
        }
    }

    inner class MyBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): MyService = this@MyService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onLocationChanged(location: Location) {
        var point: JSONObject? = null
        if (listPrefGPS == "LocationManager") {
            point = gpsLocationTracker.data
        } else if (listPrefGPS == "FusedLocationProvider") {
            point = gpsFusedLocationTracker.data
        }

        val lat = location.latitude
        val lng = location.longitude
        val alt = location.altitude
        var tim = ""

        change = true
        try {
            tim = point!!.getString("timestamp")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        model.setLog(
            "Latitude: $lat" +
                    "\nLongitude: $lng" +
                    "\nAltitude: $alt" +
                    "\nTimestamp: $tim"
        )
        model.setPoint(point!!)
    }

    override fun setLogRecord(str: String) {
        model.setLog(str)
    }

    override fun onDestroy() {
        gpsLocationTracker.stopTracking()
        gpsFusedLocationTracker.stopTracking()
        task.cancel(true)
        super.onDestroy()
    }
}
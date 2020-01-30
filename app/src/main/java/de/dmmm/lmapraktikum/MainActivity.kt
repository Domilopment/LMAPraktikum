package de.dmmm.lmapraktikum

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.dmmm.lmapraktikum.sensorManager.OnSensorDataChangeListener
import android.view.MenuItem
import de.dmmm.lmapraktikum.sensors.Magnetometer
import de.dmmm.lmapraktikum.sensors.Gyroscope
import de.dmmm.lmapraktikum.sensors.Accelerometer
import de.dmmm.lmapraktikum.sensorManager.JSONSensorDataManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import de.dmmm.lmapraktikum.service.MyService
import de.dmmm.lmapraktikum.settings.SettingsActivity
import de.dmmm.lmapraktikum.ui.sensors.SensorsViewModel
import kotlinx.android.synthetic.main.my_toolbar.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnSensorDataChangeListener {
    private lateinit var jsonSensorDataManager: JSONSensorDataManager
    private lateinit var globalSettings: SharedPreferences
    private lateinit var model: SensorsViewModel
    lateinit var myService: MyService
    var mBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MyService.MyBinder
            myService = binder.getService()
            myService.startLocationLoggin(this@MainActivity)
            mBound = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_location_manager, R.id.navigation_sensors
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val intent = Intent()
            val pm = getSystemService(PowerManager::class.java)
            if (pm!!.isIgnoringBatteryOptimizations((packageName))) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        if (checkNeededPermissions()) {
            startApplication()
        }
    }

    private fun checkNeededPermissions() : Boolean{
        val neededPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val missingPermissions = ArrayList<String>()
        for (neededPermission in neededPermissions) {
            if (ActivityCompat.checkSelfPermission(applicationContext, neededPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(neededPermission)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 0)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(connection)
            mBound = false
        }
    }

    fun stopService(){
        val intent = Intent(this, MyService::class.java).also {
            if (mBound) {
                unbindService(connection)
                 mBound = false
            }
        }
        stopService(intent)
    }

    private fun startApplication() {
        model = ViewModelProviders.of(this).get(SensorsViewModel::class.java)

        globalSettings = PreferenceManager.getDefaultSharedPreferences(this)

        jsonSensorDataManager = JSONSensorDataManager(this@MainActivity, this)
        val accelerometer = Accelerometer(this@MainActivity)
        val gyroscope = Gyroscope(this@MainActivity)
        val magnetometer = Magnetometer(this@MainActivity)
        jsonSensorDataManager.addSensor(accelerometer)
        jsonSensorDataManager.addSensor(gyroscope)
        jsonSensorDataManager.addSensor(magnetometer)

        val switchPrefAccelero =
            globalSettings.getBoolean(SettingsActivity.KEY_PREF_SWITCH_ACCELERO, false)
        if (switchPrefAccelero) {
            accelerometer.start()
        } else {
            accelerometer.stop()
        }

        val switchPrefGyro = globalSettings.getBoolean(SettingsActivity.KEY_PREF_SWITCH_GYRO, false)
        if (switchPrefGyro) {
            gyroscope.start()
        } else {
            gyroscope.stop()
        }

        val switchPrefCompass =
            globalSettings.getBoolean(SettingsActivity.KEY_PREF_SWITCH_COMPASS, false)
        if (switchPrefCompass) {
            magnetometer.start()
        } else {
            magnetometer.stop()
        }

        if (switchPrefAccelero || switchPrefGyro || switchPrefCompass) {
            jsonSensorDataManager.startLogging()
        } else {
            jsonSensorDataManager.stopLogging()
        }

        val seekBarPrefGeneral =
            globalSettings.getInt(SettingsActivity.KEY_PREF_SEEKBAR_GENERAL, 5000)
        jsonSensorDataManager.interval = seekBarPrefGeneral
        //End examples
    }

    private fun allPermissionsGranted(grantedPermissions: IntArray): Boolean {
        for (singleGrantedPermission in grantedPermissions)
            if (singleGrantedPermission == PackageManager.PERMISSION_DENIED)
                return false
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (allPermissionsGranted(grantResults)) {
            startApplication()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    override fun setLogRecord(str: String) {
        model.setLog(str)
    }

    override fun onSensorDataChanged() {
        val data = jsonSensorDataManager.data
        var acc: JSONObject? = null
        var gyr: JSONObject? = null
        var mag: JSONObject? = null
        var tim = ""
        var x: Double?
        var y: Double?
        var z: Double?

        try {
            acc = data.get("Accelerometer") as JSONObject
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        try {
            gyr = data.get("Gyroscope") as JSONObject
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        try {
            mag = data.get("Magnetometer") as JSONObject
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        try {
            tim = data.get("timestamp").toString()
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        if (data.has("Accelerometer")) try {
            x = acc!!.get("x") as Double
            y = acc.get("y") as Double
            z = acc.get("z") as Double
            model.setLog(
                "\nAccelerometer:" +
                "\nx: $x" +
                "\ny: $y" +
                "\nz: $z\n"
            )
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        if (data.has("Gyroscope")) try {
            x = gyr!!.get("x") as Double
            y = gyr.get("y") as Double
            z = gyr.get("z") as Double
            model.setLog("\nGyroscope:" +
                "\nx: $x" +
                "\ny: $y" +
                "\nz: $z\n")
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        if (data.has("Magnetometer")) try {
            x = mag!!.get("x") as Double
            y = mag.get("y") as Double
            z = mag.get("z") as Double
            model.setLog("\nMagnetometer:" +
                "\nx: $x" +
                "\ny: $y" +
                "\nz: $z\n")
        } catch (e:JSONException) {
            e.printStackTrace()
        }

        model.setLog("timestamp: $tim")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

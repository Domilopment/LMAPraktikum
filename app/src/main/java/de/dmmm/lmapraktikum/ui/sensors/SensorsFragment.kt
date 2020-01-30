package de.dmmm.lmapraktikum.ui.sensors

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.dmmm.lmapraktikum.R
import kotlinx.android.synthetic.main.fragment_sensors.*

class SensorsFragment : Fragment() {

    private lateinit var viewModel: SensorsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this).get(SensorsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_sensors, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sectionsPagerAdapter = SensorsSectionsPagerAdapter(this.activity!!, this.childFragmentManager)
        val viewPager: ViewPager = view_pager_sensors
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = tabs
        tabs.setupWithViewPager(viewPager)
    }

}

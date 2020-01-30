package de.dmmm.lmapraktikum.ui.locationmanager

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.dmmm.lmapraktikum.R
import kotlinx.android.synthetic.main.fragment_location_manager.*

class LocationManagerFragment : Fragment() {

    private lateinit var viewModel: LocationManagerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this).get(LocationManagerViewModel::class.java)
        return inflater.inflate(R.layout.fragment_location_manager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sectionsPagerAdapter = LocationManagerSectionsPagerAdapter(this.activity!!, this.childFragmentManager)
        val viewPager: ViewPager = view_pager_location_manager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = tabs
        tabs.setupWithViewPager(viewPager)
    }

}

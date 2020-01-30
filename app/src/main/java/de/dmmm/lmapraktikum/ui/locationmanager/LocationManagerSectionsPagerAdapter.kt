package de.dmmm.lmapraktikum.ui.locationmanager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.dmmm.lmapraktikum.R

private val TAB_TITLES = arrayOf(
    R.string.locatiom_manager_tab_1,
    R.string.locatiom_manager_tab_2
)
private val FRAGMENTS = arrayOf(
    LocationLogFragment(),
    MapsFragment()
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class LocationManagerSectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getLocationlog is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return FRAGMENTS[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return try {
            context.resources.getString(TAB_TITLES[position])
        } catch (e: Exception) {
            "No Title"
        }
    }

    override fun getCount(): Int {
        return FRAGMENTS.size
    }
}
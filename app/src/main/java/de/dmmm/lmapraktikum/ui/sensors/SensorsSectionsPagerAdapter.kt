package de.dmmm.lmapraktikum.ui.sensors

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.dmmm.lmapraktikum.R
import java.lang.Exception

private val TAB_TITLES = arrayOf(
    R.string.locatiom_manager_tab_1
)
private val FRAGMENTS = arrayOf(
    SensorsLogFragment()
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SensorsSectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @Throws(Exception::class)
    override fun getItem(position: Int): Fragment {
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
package com.johngoodstadt.memorize.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.johngoodstadt.memorize.fragments.RecallGroupsTabFragment
import com.johngoodstadt.memorize.fragments.TodayTabFragment
import com.johngoodstadt.memorize.utils.Constants

private val TAB_TITLES = Constants.MAIN_TABS

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (position==0)
            return RecallGroupsTabFragment.newInstance()
        else
            return TodayTabFragment.newInstance()

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }


    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}
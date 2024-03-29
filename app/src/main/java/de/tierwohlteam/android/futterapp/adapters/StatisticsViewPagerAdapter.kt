package de.tierwohlteam.android.futterapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class StatisticsViewPagerAdapter(fragmentManager: FragmentManager,
                              lifeCycle: Lifecycle,
                              private val fragments:ArrayList<Fragment>):
    FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}

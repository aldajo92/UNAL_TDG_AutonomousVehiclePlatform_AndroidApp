package com.projects.aldajo92.jetsonbotunal.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projects.aldajo92.jetsonbotunal.NUM_PAGES
import com.projects.aldajo92.jetsonbotunal.main.graphs.GraphsFragment

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> GraphsFragment()
        else -> ScreenSlidePageFragment()
    }
}
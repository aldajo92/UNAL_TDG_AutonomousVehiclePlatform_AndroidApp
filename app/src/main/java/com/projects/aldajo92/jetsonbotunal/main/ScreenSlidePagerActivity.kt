package com.projects.aldajo92.jetsonbotunal.main

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.projects.aldajo92.jetsonbotunal.R

/**
 * The number of pages (wizard steps) to show in this demo.
 */

class ScreenSlidePagerActivity : FragmentActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_screen_slide)

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.viewPager)

        // The pager adapter, which provides the pages to the view pager widget.
//        val pagerAdapter = ScreenSlidePagerAdapter(this)
//        viewPager.adapter = pagerAdapter
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }


}
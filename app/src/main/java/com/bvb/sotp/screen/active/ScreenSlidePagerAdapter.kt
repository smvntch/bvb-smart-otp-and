package com.bvb.sotp.screen.active

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment = TutorialFragment.newInstance(position)
}
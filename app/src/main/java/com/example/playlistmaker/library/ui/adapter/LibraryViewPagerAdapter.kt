package com.example.playlistmaker.library.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.library.ui.activity.FavoritesFragment
import com.example.playlistmaker.library.ui.activity.PlaylistsFragment

class LibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return TABS_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
        1 -> PlaylistsFragment.newInstance()
        else -> FavoritesFragment.newInstance()
        }
    }
    companion object{
        private const val TABS_COUNT = 2
    }
}
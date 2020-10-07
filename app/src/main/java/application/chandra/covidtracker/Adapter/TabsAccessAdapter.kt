package application.chandra.covidtracker.Adapter

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import application.chandra.covidtracker.Fragments.IndiaList
import application.chandra.covidtracker.Fragments.IndiaStats
import application.chandra.covidtracker.Fragments.WorldStats

class TabsAccessAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        if(position==0){
            return WorldStats()
        }
        else if (position==1){
            return IndiaStats()
        }
        else if (position==2){
            return IndiaList()
        }
        else{
            return WorldStats()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(position==0){
            return "World"
        }
        else if (position==1){
            return "India"
        }
        else if (position==2){
            return "State List"
        }
        else{
            return null
        }
    }

}
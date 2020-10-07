package application.chandra.covidtracker.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.widget.Toolbar
import application.chandra.covidtracker.R
import application.chandra.covidtracker.Adapter.TabsAccessAdapter
import application.chandra.covidtracker.Fragments.IndiaList

class MainActivity : AppCompatActivity() {

    lateinit var myViewPager : ViewPager
    lateinit var myTabLayout: TabLayout
    lateinit var myAdapter: TabsAccessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myViewPager =findViewById(R.id.viewPager)

        myAdapter = TabsAccessAdapter(supportFragmentManager)

        myViewPager.adapter = myAdapter

        myTabLayout = findViewById(R.id.tabLayout)
        myTabLayout.setupWithViewPager(myViewPager)

    }
}